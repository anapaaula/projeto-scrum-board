package com.ufcg.psoft.scrum_board;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import com.ufcg.psoft.scrum_board.controller.UserController;
import com.ufcg.psoft.scrum_board.controller.UserStoryController;
import com.ufcg.psoft.scrum_board.controller.ProjectController;
import com.ufcg.psoft.scrum_board.dto.AddUserToProjectDTO;
import com.ufcg.psoft.scrum_board.dto.AddUserToUserStoryDTO;
import com.ufcg.psoft.scrum_board.dto.NewProjectDTO;
import com.ufcg.psoft.scrum_board.dto.NewUserStoryDTO;
import com.ufcg.psoft.scrum_board.dto.ProjectDTO;
import com.ufcg.psoft.scrum_board.dto.UserDTO;
import com.ufcg.psoft.scrum_board.dto.UserStoryDTO;
import com.ufcg.psoft.scrum_board.dto.UserStoryReportDTO;
import com.ufcg.psoft.scrum_board.dto.UserStoryReportForUserDTO;
import com.ufcg.psoft.scrum_board.exception.UserAlreadyExistsException;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserStoryControllerTests {
    
 
    @Autowired
    private UserController userController;

    @Autowired
    private ProjectController projectController;

    @Autowired
    private UserStoryController userStoryController;

    private Set<UserDTO> userDTOsSet;

    private ProjectDTO project;

    private Set<UserStoryDTO> userStoryDTOsSet;


    @BeforeAll
    void setUp() throws UserAlreadyExistsException {
        List<String> apRoles = new ArrayList<>();
        List<String> miRoles = new ArrayList<>();
        List<String> veRoles = new ArrayList<>();

        apRoles.add("PRODUCT_OWNER");
        apRoles.add("DEVELOPER");
        miRoles.add("DEVELOPER");
        miRoles.add("RESEARCHER");
        veRoles.add("DEVELOPER");

        UserDTO ap = new UserDTO("Ana Paula", "anapaula@gmail.com", "ap", apRoles);
        UserDTO mi = new UserDTO("Marcus Ideao", "marcusideao@gmail.com", "mi", miRoles);
        UserDTO ve = new UserDTO("Vitor Emanuel", "vitoremanuel@gmail.com", "ve", veRoles);

        this.userDTOsSet = new HashSet<>();
        this.userStoryDTOsSet = new HashSet<>();

        this.userDTOsSet.add(ap);
        this.userDTOsSet.add(mi);
        this.userDTOsSet.add(ve);

        this.userController.create(ap);
        this.userController.create(mi);
        this.userController.create(ve);

        NewProjectDTO np = new NewProjectDTO("ScrumBoard", "Psoft", "UFCG");

        this.project = (ProjectDTO) this.projectController.create(np, "ap").getBody();

        AddUserToProjectDTO userToProject = new AddUserToProjectDTO(this.project.getId(), "mi", "DEVELOPER");

        this.projectController.addUserToProject("ap", userToProject);

        NewUserStoryDTO newUserStoryDTO = new NewUserStoryDTO("US2", "CRUD de projetos", this.project.getId());

        UserStoryDTO userStoryDTO = (UserStoryDTO) this.userStoryController.create("ap", newUserStoryDTO).getBody();

        this.userStoryDTOsSet.add(userStoryDTO);
    }


    @Test
    void createUserStoryTest() {
        NewUserStoryDTO newUserStoryDTO = new NewUserStoryDTO("US1", "CRUD de usuários", this.project.getId());
        UserStoryDTO userStoryDTO = (UserStoryDTO) this.userStoryController.create("ap", newUserStoryDTO).getBody();
        this.userStoryDTOsSet.add(userStoryDTO);

        assertEquals(newUserStoryDTO.getTitle(), userStoryDTO.getTitle());
        assertEquals(newUserStoryDTO.getDescription(), userStoryDTO.getDescription());
        assertEquals(newUserStoryDTO.getProjectId(), userStoryDTO.getProjectId());
    }


    @Test
    void createUserStoryNotAuthorizedTest() {
        NewUserStoryDTO newUserStoryDTO = new NewUserStoryDTO("US6", "CRUD de user stories", this.project.getId());
        assertEquals("The user with username 've' has not the permission to perform this action!", (String) this.userStoryController.create("ve", newUserStoryDTO).getBody());
    }


    @Test
    void addUserToUserStoryTest() {
        AddUserToProjectDTO userToProject = new AddUserToProjectDTO(this.project.getId(), "mi", "DEVELOPER");

        this.projectController.addUserToProject("ap", userToProject);

        AddUserToUserStoryDTO userToUS = new AddUserToUserStoryDTO(this.userStoryDTOsSet.stream().findFirst().get().getId(), "hp");

        this.userStoryController.joinUserStory("mi", userToUS.getUserStoryId()).getBody();
    }


    @Test
    void addUserToUserStoryHasNoRoleTest() throws UserAlreadyExistsException {
        List<String> hpRoles = new ArrayList<>();

        hpRoles.add("PRODUCT_OWNER");

        UserDTO userDTO = new UserDTO("Huandrey Pontes", "huandreypontes@gmail.com", "hp", hpRoles);

        this.userController.create(userDTO);

        this.userDTOsSet.add(userDTO);

        AddUserToProjectDTO userToProject = new AddUserToProjectDTO(this.project.getId(), "hp", "PRODUCT_OWNER");

        this.projectController.addUserToProject("ap", userToProject);

        AddUserToUserStoryDTO userToUS = new AddUserToUserStoryDTO(this.userStoryDTOsSet.stream().findFirst().get().getId(), "hp");

        assertEquals("The user with username 'hp' has not the role to join this User Story!", (String) this.userStoryController.joinUserStory("hp", userToUS.getUserStoryId()).getBody());
    }
    
    @Test
    void addUserToUS() {
        AddUserToUserStoryDTO dto = new AddUserToUserStoryDTO(this.userStoryDTOsSet.stream().findFirst().get().getId(), "mi");
        HttpStatus status = userStoryController.addUserToUS("ap", dto).getStatusCode();
        assertEquals(200, status.value());
    }

    @Test
    void getAllReportsAsProductOwnerTest() throws UserAlreadyExistsException {
        List<String> pcRoles = new ArrayList<>();
        pcRoles.add("PRODUCT_OWNER");
        UserDTO pc = new UserDTO("Pedro Casado", "pedrocasado@gmail.com", "pc", pcRoles);
        userController.create(pc);
        AddUserToProjectDTO userToProject = new AddUserToProjectDTO(project.getId(), "pc", "PRODUCT_OWNER");
        projectController.addUserToProject("ap", userToProject);
        Object result = projectController.getAllReports("pc", project.getId()).getBody();
        assertEquals(1, ((UserStoryReportDTO) result).getStats().get("To Do").count);
        
        NewUserStoryDTO newUserStoryDTO = new NewUserStoryDTO("US1", "CRUD de usuários", this.project.getId());
        UserStoryDTO userStoryDTO = (UserStoryDTO) this.userStoryController.create("ap", newUserStoryDTO).getBody();
        this.userStoryDTOsSet.add(userStoryDTO);
        userStoryController.joinUserStory("mi", userStoryDTO.getId());
        
        result = projectController.getAllReports("pc", project.getId()).getBody();
        assertEquals(1, ((UserStoryReportDTO) result).getStats().get("Work In Progress").count);
        assertEquals(50, ((UserStoryReportDTO) result).getStats().get("Work In Progress").percent);
    }

    @Test
    void getAllReportsAsDeveloperTest() throws UserAlreadyExistsException {
        
        NewUserStoryDTO newUserStoryDTO = new NewUserStoryDTO("US3", "CRUD de User Stories", this.project.getId());
        UserStoryDTO userStoryDTO = (UserStoryDTO) this.userStoryController.create("ap", newUserStoryDTO).getBody();
        this.userStoryDTOsSet.add(userStoryDTO);
        userStoryController.joinUserStory("mi", userStoryDTO.getId());

        Object result = projectController.getAllReports("mi", project.getId()).getBody();
        assertEquals(1, ((UserStoryReportForUserDTO) result).getStats().get("Work In Progress").count);
        assertEquals(100, ((UserStoryReportForUserDTO) result).getStats().get("Work In Progress").percent);
        assertEquals(1, ((UserStoryReportForUserDTO) result).getPersonalStats().count);
        assertEquals(50d, ((UserStoryReportForUserDTO) result).getPersonalStats().percent);
    }

}
