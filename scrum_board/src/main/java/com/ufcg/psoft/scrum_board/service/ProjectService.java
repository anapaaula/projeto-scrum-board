package com.ufcg.psoft.scrum_board.service;

import com.ufcg.psoft.scrum_board.dto.AddUserToProjectDTO;
import com.ufcg.psoft.scrum_board.dto.NewProjectDTO;
import com.ufcg.psoft.scrum_board.dto.ProjectDTO;
import com.ufcg.psoft.scrum_board.dto.UpdateProjectDTO;
import com.ufcg.psoft.scrum_board.enums.ScrumRoleEnum;
import com.ufcg.psoft.scrum_board.exception.InappropriateRoleException;
import com.ufcg.psoft.scrum_board.exception.ProjectNotFoundException;
import com.ufcg.psoft.scrum_board.exception.UnauthorizedAccessException;
import com.ufcg.psoft.scrum_board.exception.UnavailableRoleException;
import com.ufcg.psoft.scrum_board.exception.UserNotFoundException;
import com.ufcg.psoft.scrum_board.factory.ScrumRoleFactory;
import com.ufcg.psoft.scrum_board.model.Project;
import com.ufcg.psoft.scrum_board.model.User;
import com.ufcg.psoft.scrum_board.repository.ProjectRepository;
import com.ufcg.psoft.scrum_board.utils.ProjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRep;

    @Autowired
    private DataService dataService;


    public ProjectDTO addProject(NewProjectDTO newProjectDTO, String loggedUsername) throws UserNotFoundException {

        User scrumMaster = this.dataService.getUserByUsername(loggedUsername);

        Project project = new Project(newProjectDTO.getName(), newProjectDTO.getDescription(), newProjectDTO.getPartnerInstitution(), scrumMaster);

        this.projectRep.addProject(project);

        return ProjectMapper.convertToProjectDTO(project, this.dataService.getUserStoriesByProjectId(project.getId()));
    }


    public ProjectDTO findProjectById(String projectId) throws ProjectNotFoundException {
        Project project = this.dataService.getProjectById(projectId);

        return ProjectMapper.convertToProjectDTO(project, this.dataService.getUserStoriesByProjectId(project.getId()));
    }


    public List<ProjectDTO> findProjectsByProductOwner(String username) {
        return projectRep.getProjectsByProjectOwner(username)
                    .stream()
                    .map(p -> ProjectMapper.convertToProjectDTO(p, this.dataService.getUserStoriesByProjectId(p.getId())))
                    .collect(Collectors.toList());
    }


    public List<ProjectDTO> findProjectsByScrumMaster(String username) {
        return projectRep.getProjectsByScrumMaster(username)
                    .stream()
                    .map(p -> ProjectMapper.convertToProjectDTO(p, this.dataService.getUserStoriesByProjectId(p.getId())))
                    .collect(Collectors.toList());
    }


    public List<ProjectDTO> getAllProjects() {
        List<ProjectDTO> projectsFound = projectRep.getAll()
                                            .stream()
                                            .map(p -> ProjectMapper.convertToProjectDTO(p, this.dataService.getUserStoriesByProjectId(p.getId())))
                                            .collect(Collectors.toList());
        
        return projectsFound;
    }


    public ProjectDTO updateProject(String loggedUsername, String projectId, UpdateProjectDTO projectDTO) throws UnauthorizedAccessException, ProjectNotFoundException {
        Project project = this.dataService.getProjectById(projectId);

        this.dataService.isScrumMaster(project, loggedUsername);

        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());
        project.setPartnerInstitution(projectDTO.getPartnerInstitution());
        project.setScrumMaster(project.getScrumMaster());
        project.setProductOwner(project.getProductOwner());
        project.setDevelopers(project.getDevelopers());
        project.setDevelopers(project.getResearchers());
        project.setDevelopers(project.getTrainees());

        this.projectRep.editProject(project);

        return ProjectMapper.convertToProjectDTO(project, this.dataService.getUserStoriesByProjectId(project.getId()));
    }


    public void deleteProject(String projectId, String loggedUsername) throws ProjectNotFoundException, UnauthorizedAccessException {
        Project project = this.dataService.getProjectById(projectId);

        this.dataService.isScrumMaster(project, loggedUsername);

        this.dataService.delProject(project);
    }


    public ProjectDTO addUserToProject(String loggedUsername, AddUserToProjectDTO addUserToProjectDTO) throws ProjectNotFoundException, UnauthorizedAccessException, UserNotFoundException, InappropriateRoleException, UnavailableRoleException {
        Project project = this.dataService.getProjectById(addUserToProjectDTO.getProjectId());
        this.dataService.isScrumMaster(project, loggedUsername);
        User user = this.dataService.getUserByUsername(addUserToProjectDTO.getUsername());

        ScrumRoleFactory srf = new ScrumRoleFactory();
        srf.verifyRole(addUserToProjectDTO.getRole());
        ScrumRoleEnum sre = srf.getEnumByString(addUserToProjectDTO.getRole());

        this.dataService.verifyUserRoles(user, sre);
        project.addUserToProject(user, sre);
        this.projectRep.editProject(project);

        return ProjectMapper.convertToProjectDTO(project, this.dataService.getUserStoriesByProjectId(project.getId()));
    }

    public Object getAllReports(String loggedUsername, String projectId) throws ProjectNotFoundException, UnauthorizedAccessException, UserNotFoundException {
        return this.dataService.getAllReports(loggedUsername, projectId);
    }

}
