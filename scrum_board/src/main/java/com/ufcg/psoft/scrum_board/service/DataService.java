package com.ufcg.psoft.scrum_board.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ufcg.psoft.scrum_board.dto.SummarizedUserStoryDTO;
import com.ufcg.psoft.scrum_board.dto.UserStoryReportDTO;
import com.ufcg.psoft.scrum_board.dto.UserStoryReportForScrumMaster;
import com.ufcg.psoft.scrum_board.dto.UserStoryReportForUserDTO;
import com.ufcg.psoft.scrum_board.enums.ScrumRoleEnum;
import com.ufcg.psoft.scrum_board.exception.AlreadyInUserStoryDevelopmentTeamException;
import com.ufcg.psoft.scrum_board.exception.DangerousActionException;
import com.ufcg.psoft.scrum_board.exception.InappropriateRoleException;
import com.ufcg.psoft.scrum_board.exception.ProjectAlreadyExistsException;
import com.ufcg.psoft.scrum_board.exception.ProjectNotFoundException;
import com.ufcg.psoft.scrum_board.exception.UnauthorizedAccessException;
import com.ufcg.psoft.scrum_board.exception.UserNotFoundException;
import com.ufcg.psoft.scrum_board.exception.UserStoryNotFoundException;
import com.ufcg.psoft.scrum_board.model.Project;
import com.ufcg.psoft.scrum_board.model.Task;
import com.ufcg.psoft.scrum_board.model.User;
import com.ufcg.psoft.scrum_board.model.UserStory;
import com.ufcg.psoft.scrum_board.repository.ProjectRepository;
import com.ufcg.psoft.scrum_board.repository.TaskRepository;
import com.ufcg.psoft.scrum_board.repository.UserRepository;
import com.ufcg.psoft.scrum_board.repository.UserStoryRepository;
import com.ufcg.psoft.scrum_board.utils.Stats;

@Service
class DataService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserStoryRepository userStoryRepository;

    @Autowired
    private TaskRepository taskRepository;


    protected User getUserByUsername(String username) throws UserNotFoundException {
        User user = this.userRepository.getUserByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("The user with username '" + username + "' was not found!");
        }
        return user;
    }


    protected void delUser(User user) throws DangerousActionException {
        List<Project> projects = this.projectRepository.getProjectsByUser(user);

        for (Project p : projects) {
            if (p.getScrumMaster().equals(user)) { 
                throw new DangerousActionException("The user '" + 
                    user.getUsername() + 
                    "' can not be deleted, because he is the Scrum Master of the project '" + 
                    p.getName() + 
                    "'! Find a substitute!"
                );
            }
            p.removeUserFromProject(user);
            this.projectRepository.editProject(p);
        }

        this.userStoryRepository.findUserStoriesByUser(user).forEach(us -> {
            us.removeDev(user);
            this.userStoryRepository.editUserStory(us);
        });

        this.userRepository.delUser(user.getUsername());
    }


    protected void delProject(Project project) {
        List<UserStory> userStories = this.userStoryRepository.findUserStoriesByProject(project.getId());
        userStories.stream().map(us -> us.toString()).forEach(usid -> this.userStoryRepository.delUserStory(usid));
        this.projectRepository.delProject(project.getId());
    }


    protected void delUserStory(UserStory userStory) {
        List<Task> tasks = this.taskRepository.getTasksByUserStoryId(userStory.getId());
        tasks.stream().map(t -> t.toString()).forEach(tid -> this.taskRepository.delTask(tid));
        this.userStoryRepository.delUserStory(userStory.getId());
    }

    protected void verifyProjectAlreadyExists(String projectId) throws ProjectAlreadyExistsException {
        Project project = this.projectRepository.getProjectById(projectId);
        if (project != null) throw new ProjectAlreadyExistsException("Project already exists!");
    }


    protected void isScrumMaster(Project project, String username) throws UnauthorizedAccessException {
        if (!project.getScrumMaster().getUsername().equals(username)) { 
            throw new UnauthorizedAccessException("The user '" + username + "' is not authorized to peform this operation in this project!");
        }
    }


    protected Project getProjectById(String projectId) throws ProjectNotFoundException {
        Project project = this.projectRepository.getProjectById(projectId);
        if (project == null) throw new ProjectNotFoundException("The project with id '" + projectId + "' was not found!");
        return project;
    }


    protected void verifyUserRoles(User user, ScrumRoleEnum sre) throws InappropriateRoleException {
        if (!user.getRoles().stream().anyMatch(r -> r.getScrumRoleEnum().equals(sre))) {
            throw new InappropriateRoleException("The user with username '" + user.getUsername() + "' does not have the role "+ sre.name() +"!" );
        }
    }


    protected UserStory getUserStoryById(String id_UserStory) throws UserStoryNotFoundException {
        UserStory userStory = this.userStoryRepository.findUserStoryById(id_UserStory);
        if (userStory == null) throw new UserStoryNotFoundException("The User Story with id '" + id_UserStory + "' was not found!");
        return userStory;
    }


    protected void isUserStoryDev(UserStory userStory, String loggedUsername) throws UnauthorizedAccessException {
        if (userStory.getDevs().stream().noneMatch(d -> d.getUsername().equals(loggedUsername))) throw new UnauthorizedAccessException("The user '" + loggedUsername + "' is not authorized to peform this operation in this User Story!");
    }


    protected void alreadyIsUserStoryDev(UserStory userStory, String loggedUsername) throws AlreadyInUserStoryDevelopmentTeamException {
        if (userStory.getDevs().stream().anyMatch(d -> d.getUsername().equals(loggedUsername))) throw new AlreadyInUserStoryDevelopmentTeamException("The user '" + loggedUsername + "' is not authorized to peform this operation in this User Story!");
    }


    protected void isDevelopmentUser(Project project, User user) throws InappropriateRoleException {
        if (
            project.getDevelopers().stream().noneMatch(u -> u.equals(user)) &&
            project.getResearchers().stream().noneMatch(u -> u.equals(user)) &&
            project.getTrainees().stream().noneMatch(u -> u.equals(user))
        ) throw new InappropriateRoleException("The user with username '" + user.getUsername() + "' is not in the development team of the project associated with this User Story!");
    }


    protected void verifyParticipationInProject(Project project, User user) throws UnauthorizedAccessException {
        if (
            project.getDevelopers().stream().noneMatch(u -> u.equals(user)) &&
            project.getResearchers().stream().noneMatch(u -> u.equals(user)) &&
            project.getTrainees().stream().noneMatch(u -> u.equals(user)) &&
            !project.getScrumMaster().equals(user) &&
            !project.getProductOwner().equals(user)
        ) throw new UnauthorizedAccessException("The user with username '" + user.getUsername() + "' has not the permission to perform this action!");
    }


    protected Object getAllReports(String loggedUsername, String projectId) throws ProjectNotFoundException, UnauthorizedAccessException, UserNotFoundException {
        User loggedUser = this.getUserByUsername(loggedUsername);

        Project project = projectRepository.getProjectById(projectId);

        boolean isPO = loggedUser.getUsername().equals(project.getProductOwner().getUsername());
        boolean isDev = project.getDevelopers().stream().anyMatch((dev) -> dev.getUsername().equals(loggedUser.getUsername()));
        boolean isScrumMaster = loggedUser.getUsername().equals(project.getScrumMaster().getUsername());

        if (isPO) { return this.buildReportForPO(project); }
        else if (isScrumMaster) { return this.buildReportForScrumMaster(project); } 
        else if (isDev) { return this.buildReportForUser(project, loggedUsername); } 
        else { throw new UnauthorizedAccessException("This user isn't in the project."); }
    }


    private UserStoryReportDTO buildReportForPO(Project project) {

        List<UserStory> projectUserStories = userStoryRepository.findUserStoriesByProject(project.getId());

        Map<String, Stats> stats = new HashMap<>();
        List<SummarizedUserStoryDTO> userStories = projectUserStories.stream()
            .map(us -> {
                return new SummarizedUserStoryDTO(us.getId(), us.getTitle(), us.getDevelopmentState().toString());
            })
            .collect(Collectors.toList());

        userStories.stream().forEach(us -> {
            if (stats.containsKey(us.getStatus())) {
                Stats currentValue = stats.get(us.getStatus());
                currentValue.count++;
                stats.put(us.getStatus(), currentValue);
            } else {
                stats.put(us.getStatus(), new Stats(1, 0));
            }
        });

        stats.keySet().stream().forEach(key -> {
            Stats value = stats.get(key);
            value.percent = (double) value.count / (double) userStories.size() * 100;
            stats.put(key, value);
        });

        UserStoryReportDTO result = new UserStoryReportDTO(project.getId(), project.getName(), stats, userStories);

        return result;
    }


    private UserStoryReportForUserDTO buildReportForUser(Project project, String loggedUsername) {

        List<UserStory> projectUserStories = userStoryRepository.findUserStoriesByProject(project.getId());

        Map<String, Stats> stats = new HashMap<>();
        Stats personalStats = new Stats(0, 0);

        List<SummarizedUserStoryDTO> userStories = projectUserStories.stream()
            .filter(us -> us.getDevs().stream().anyMatch(dev -> dev.getUsername().equals(loggedUsername)))
            .map(us -> {
                return new SummarizedUserStoryDTO(us.getId(), us.getTitle(), us.getDevelopmentState().toString());
            })
            .collect(Collectors.toList());

        userStories.stream().forEach(us -> {
            if (stats.containsKey(us.getStatus())) {
                Stats currentValue = stats.get(us.getStatus());
                currentValue.count++;
                stats.put(us.getStatus(), currentValue);
            } else {
                stats.put(us.getStatus(), new Stats(1, 0));
            }
        });

        stats.keySet().stream().forEach(key -> {
            Stats value = stats.get(key);
            value.percent = (double) value.count / (double) userStories.size() * 100;
            stats.put(key, value);
        });

        personalStats.count = userStories.size();
        personalStats.percent = (float) personalStats.count / (float) projectUserStories.size() * 100;

        UserStoryReportForUserDTO result = new UserStoryReportForUserDTO(project.getId(), project.getName(), personalStats, stats, userStories);

        return result;
    }

    private UserStoryReportForScrumMaster buildReportForScrumMaster(Project project) {
        UserStoryReportDTO reportByState = this.buildReportForPO(project);
        List<User> developmentTeam = project.getDevelopmentTeam();
        List<UserStoryReportForUserDTO> reportByUser = new ArrayList<>();
        developmentTeam.forEach(u -> reportByUser.add(this.buildReportForUser(project, u.getUsername())));
        return new UserStoryReportForScrumMaster(project.getId(), project.getName(), reportByUser, reportByState);
    }


    protected List<Task> getTasksByUserStory(String userStoryId) {
        return this.taskRepository.getTasksByUserStoryId(userStoryId);
    }
    

    protected List<UserStory> getUserStoriesByProjectId(String projectId) {
        return this.userStoryRepository.findUserStoriesByProject(projectId);
    }

}
