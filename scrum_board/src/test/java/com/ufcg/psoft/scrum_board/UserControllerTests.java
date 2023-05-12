package com.ufcg.psoft.scrum_board;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ufcg.psoft.scrum_board.controller.UserController;
import com.ufcg.psoft.scrum_board.dto.UserDTO;
import com.ufcg.psoft.scrum_board.exception.UserAlreadyExistsException;
import com.ufcg.psoft.scrum_board.exception.UserNotFoundException;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserControllerTests {
    
    @Autowired
    private UserController userController;

    private Set<UserDTO> userDTOsSet;

    @BeforeAll
    void setUp() throws UserAlreadyExistsException {
        List<String> apRoles = new ArrayList<>();
        List<String> miRoles = new ArrayList<>();

        apRoles.add("PRODUCT_OWNER");
        apRoles.add("DEVELOPER");
        miRoles.add("DEVELOPER");
        miRoles.add("RESEARCHER");

        UserDTO ap = new UserDTO("Ana Paula", "anapaula@gmail.com", "ap", apRoles);
        UserDTO mi = new UserDTO("Marcus Ideao", "marcusideao@gmail.com", "mi", miRoles);

        this.userDTOsSet = new HashSet<>();

        this.userDTOsSet.add(ap);
        this.userDTOsSet.add(mi);

        this.userController.create(ap);
        this.userController.create(mi);
    }


    @Test
    void createNewUserTest() throws UserAlreadyExistsException {
        List<String> hpRoles = new ArrayList<>();

        hpRoles.add("PRODUCT_OWNER");
        hpRoles.add("RESEARCHER");

        UserDTO userDTO = new UserDTO("Huandrey Pontes", "huandreypontes@gmail.com", "hp", hpRoles);
        assertEquals(userDTO, (UserDTO) this.userController.create(userDTO).getBody());
        this.userDTOsSet.add(userDTO);
    }

    @Test
    void createNewUserExceptionTest() throws UserAlreadyExistsException {
        List<String> roles = new ArrayList<>();

        roles.add("TRAINEE");

        UserDTO userDTO1 = new UserDTO("Hebert Laurentino", "hebertlaurentino@gmail.com", "hl", roles);
        UserDTO userDTO2 = new UserDTO("Homer Lourenzo", "homerlourenzo@gmail.com", "hl", roles);

        this.userController.create(userDTO1);
        this.userDTOsSet.add(userDTO1);

        String message = "User '" + userDTO2.getUsername() + "' already exists!";

        assertEquals(message, (String) this.userController.create(userDTO2).getBody());
    }

    @Test
    void getUserTest() throws UserNotFoundException, UserAlreadyExistsException {
        List<String> roles = new ArrayList<>();

        roles.add("PRODUCT_OWNER");
        roles.add("DEVELOPER");

        UserDTO userModel = new UserDTO("Vitor Emanuel", "vitoremanuel@gmail.com", "velds", roles);
        this.userController.create(new UserDTO("Vitor Emanuel", "vitoremanuel@gmail.com", "velds", roles));

        UserDTO user = (UserDTO) this.userController.get("velds").getBody();
        this.userDTOsSet.add(user);

        assertEquals(userModel, user);
    }

    @Test
    void getAllUsersTest() {
        Set<UserDTO> result = ((ArrayList<UserDTO>) this.userController.list().getBody()).stream().collect(Collectors.toSet());
        assertEquals(this.userDTOsSet, result);
    }

}
