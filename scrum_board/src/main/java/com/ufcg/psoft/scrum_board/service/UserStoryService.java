package com.ufcg.psoft.scrum_board.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ufcg.psoft.scrum_board.dto.AddUserToUserStoryDTO;
import com.ufcg.psoft.scrum_board.dto.NewUserStoryDTO;
import com.ufcg.psoft.scrum_board.dto.UpdateUserStoryDTO;
import com.ufcg.psoft.scrum_board.dto.UserStoryDTO;
import com.ufcg.psoft.scrum_board.exception.AlreadyAListenerException;
import com.ufcg.psoft.scrum_board.exception.AlreadyInUserStoryDevelopmentTeamException;
import com.ufcg.psoft.scrum_board.exception.InappropriateRoleException;
import com.ufcg.psoft.scrum_board.exception.ProjectNotFoundException;
import com.ufcg.psoft.scrum_board.exception.TaskNotFoundException;
import com.ufcg.psoft.scrum_board.exception.UnauthorizedAccessException;
import com.ufcg.psoft.scrum_board.exception.UserNotFoundException;
import com.ufcg.psoft.scrum_board.exception.UserStoryAlreadyExistsException;
import com.ufcg.psoft.scrum_board.exception.UserStoryNotFoundException;
import com.ufcg.psoft.scrum_board.exception.WrongUserStoryStateException;
import com.ufcg.psoft.scrum_board.listener.MasterUserStoryListener;
import com.ufcg.psoft.scrum_board.listener.NormalUserStoryListener;
import com.ufcg.psoft.scrum_board.listener.OwnersUserStoryListener;
import com.ufcg.psoft.scrum_board.model.Project;
import com.ufcg.psoft.scrum_board.model.Task;
import com.ufcg.psoft.scrum_board.model.User;
import com.ufcg.psoft.scrum_board.model.UserStory;
import com.ufcg.psoft.scrum_board.repository.UserRepository;
import com.ufcg.psoft.scrum_board.repository.UserStoryRepository;
import com.ufcg.psoft.scrum_board.utils.UserStoryMapper;

@Service
public class UserStoryService {

    @Autowired
    private UserStoryRepository userStoryRep;

    @Autowired
    private UserRepository userRep;

    @Autowired
    private DataService dataService;


    public UserStoryDTO addUserStory(NewUserStoryDTO newUserStoryDTO, String loggedUsername) throws UserStoryAlreadyExistsException, ProjectNotFoundException, UserNotFoundException, UnauthorizedAccessException {
        
        Project project = this.dataService.getProjectById(newUserStoryDTO.getProjectId());
        verifyUserStoryAlreadyExists(newUserStoryDTO);
        User user = this.dataService.getUserByUsername(loggedUsername);
        this.dataService.verifyParticipationInProject(project, user);

        UserStory userStory = new UserStory(newUserStoryDTO.getTitle(), newUserStoryDTO.getDescription(), project);
        userStory.addListener(new MasterUserStoryListener(project.getScrumMaster()));
        userStory.addListener(new OwnersUserStoryListener(project.getProductOwner()));

        this.userStoryRep.addUserStory(userStory);
        return UserStoryMapper.convertToUserStoryDTO(userStory, new ArrayList<Task>());
    }


    private void verifyUserStoryAlreadyExists(NewUserStoryDTO newUserStoryDTO) throws UserStoryAlreadyExistsException {
        UserStoryDTO userStoryDTO = findUserStoryByTitle(newUserStoryDTO.getTitle());
        if (userStoryDTO != null && userStoryDTO.getProjectId().equals(newUserStoryDTO.getProjectId())) { 
            throw new UserStoryAlreadyExistsException("The User Story with title '" + userStoryDTO.getTitle() + "' of the project with id '" + userStoryDTO.getProjectId() + "' already exists!");
        }
    }
    

    public UserStoryDTO getUserStoryById(String id_UserStory) throws UserStoryNotFoundException{
        return UserStoryMapper.convertToUserStoryDTO(this.dataService.getUserStoryById(id_UserStory), this.dataService.getTasksByUserStory(id_UserStory));

    }


    public UserStoryDTO updateUserStory(UpdateUserStoryDTO updateUserStoryDTO, String loggedUsername) throws UserStoryNotFoundException, UnauthorizedAccessException {
        UserStory userStory = this.dataService.getUserStoryById(updateUserStoryDTO.getId());
        this.dataService.isUserStoryDev(userStory, loggedUsername);

        userStory.setTitle(updateUserStoryDTO.getTitle());
        userStory.setDescription(updateUserStoryDTO.getDescription());
        
        List<User> devs = userRep.getUsersByUsername(updateUserStoryDTO.getDevs());
        userStory.setDevs(devs);

        if(updateUserStoryDTO.isMoveToNextState()) userStory.moveToNextStage();

        userStoryRep.editUserStory(userStory);

        return UserStoryMapper.convertToUserStoryDTO(userStory, this.dataService.getTasksByUserStory(userStory.getId()));
    }


    public void deleteUserStory(String id_UserStory, String loggedUsername) throws UserStoryNotFoundException, UnauthorizedAccessException {
        UserStory userStory = this.dataService.getUserStoryById(id_UserStory);
        this.dataService.isUserStoryDev(userStory, loggedUsername);
        this.dataService.delUserStory(userStory);
    }


    private UserStoryDTO findUserStoryByTitle(String title) {
        UserStoryDTO userStoryFound = null;
        for (UserStoryDTO userStory : getAllUserStories()) {
            if(userStory.getTitle().equals(title)){
                userStoryFound = userStory;
            }
        }
        return userStoryFound;
    }


    public List<UserStoryDTO> getAllUserStories() {
        return this.userStoryRep.getAll().stream().map(us -> UserStoryMapper.convertToUserStoryDTO(us, this.dataService.getTasksByUserStory(us.getId()))).collect(Collectors.toList());
    }


    public UserStoryDTO joinUserStory(String loggedUsername, String userStoryId) throws UserNotFoundException, UserStoryNotFoundException, InappropriateRoleException, AlreadyInUserStoryDevelopmentTeamException {
        User user = this.dataService.getUserByUsername(loggedUsername);
        UserStory userStory = this.dataService.getUserStoryById(userStoryId);

        this.dataService.isDevelopmentUser(userStory.getProject(), user);
        this.dataService.alreadyIsUserStoryDev(userStory, user.getUsername());

        userStory.addDev(user);

        this.userStoryRep.editUserStory(userStory);
        return UserStoryMapper.convertToUserStoryDTO(userStory, this.dataService.getTasksByUserStory(userStoryId));
    }


    public UserStoryDTO addUserToUS(String loggedUsername, AddUserToUserStoryDTO dto) throws UserNotFoundException, UserStoryNotFoundException, InappropriateRoleException, UnauthorizedAccessException, AlreadyInUserStoryDevelopmentTeamException {
        UserStory userStory = this.dataService.getUserStoryById(dto.getUserStoryId());
        User loggedUser = this.dataService.getUserByUsername(loggedUsername);
        User user = this.dataService.getUserByUsername(dto.getUsername());

        this.dataService.isScrumMaster(userStory.getProject(), loggedUser.getUsername());
        this.dataService.isDevelopmentUser(userStory.getProject(), user);
        this.dataService.alreadyIsUserStoryDev(userStory, user.getUsername());

        userStory.addDev(user);

        this.userStoryRep.editUserStory(userStory);
        return UserStoryMapper.convertToUserStoryDTO(userStory, this.dataService.getTasksByUserStory(userStory.getId()));
    }


    public UserStoryDTO setToVerify(String userStoryId, String idUsuario) throws TaskNotFoundException, UserNotFoundException, UnauthorizedAccessException, UserStoryNotFoundException, WrongUserStoryStateException{
        UserStory us = this.dataService.getUserStoryById(userStoryId);
        User user = this.dataService.getUserByUsername(idUsuario);

        if (!us.getDevelopmentState().toString().equals("Work In Progress")) throw new WrongUserStoryStateException("This user story can not be moved to To Verify stage!");
        if (!(us.getDevs().contains(user) || us.getProject().getScrumMaster().equals(user))) throw new UnauthorizedAccessException("The user '" + user.getUsername() + "' has no permission to modify this User Story!");
        
        us.moveToNextStage();
        this.userStoryRep.editUserStory(us);
        return UserStoryMapper.convertToUserStoryDTO(us, this.dataService.getTasksByUserStory(us.getId()));
    }


    public UserStoryDTO setDone(String userStoryId, String idUsuario) throws TaskNotFoundException, UserNotFoundException, UnauthorizedAccessException, UserStoryNotFoundException, WrongUserStoryStateException{
        UserStory us = this.dataService.getUserStoryById(userStoryId);
        User user = this.dataService.getUserByUsername(idUsuario);

        if (!us.getDevelopmentState().toString().equals("To Verify")) throw new WrongUserStoryStateException("This user story can not be moved to Done stage!");
        if (!(us.getDevs().contains(user) || us.getProject().getScrumMaster().equals(user))) throw new UnauthorizedAccessException("The user '" + user.getUsername() + "' has no permission to modify this User Story!");
        
        us.moveToNextStage();
        this.userStoryRep.editUserStory(us);
        return UserStoryMapper.convertToUserStoryDTO(us, this.dataService.getTasksByUserStory(us.getId()));
    }


    public void subscribeToUserStory(String loggedUsername, String userStoryId) throws UserNotFoundException, UserStoryNotFoundException, UnauthorizedAccessException, AlreadyAListenerException {

        User user = this.dataService.getUserByUsername(loggedUsername);
        UserStory userStory = this.dataService.getUserStoryById(userStoryId);
        
        this.dataService.isUserStoryDev(userStory, loggedUsername);

        if (userStory.getProject().getScrumMaster().equals(user)) { throw new AlreadyAListenerException("The user '" + user.getUsername() + "' is already a listener, because he is the Scrum Master!"); }
        else if (userStory.getProject().getProductOwner().equals(user)) { throw new AlreadyAListenerException("The user '" + user.getUsername() + "' is already a listener, because he is the Product Owner!"); }
        else { userStory.addListener(new NormalUserStoryListener(user)); }

        this.userStoryRep.editUserStory(userStory);
    }

}
