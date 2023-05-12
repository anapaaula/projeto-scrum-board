package com.ufcg.psoft.scrum_board.repository;

import org.springframework.stereotype.Repository;

import com.ufcg.psoft.scrum_board.model.Project;
import com.ufcg.psoft.scrum_board.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ProjectRepository {
    private Map<String, Project> projects;

    public ProjectRepository() {
        this.projects = new HashMap<String, Project>();
    }

    public Project addProject(Project project) {
        this.projects.put(project.getId(), project);
        return project;
    }

    public Project getProjectById(String id) {
        return this.projects.get(id);
    }

    public List<Project> getProjectsByProjectOwner(String projectOwnerUsername) {
        List<Project> projectsBelongsProjectOwner = new ArrayList<Project>();
        for (Project project : this.projects.values()) {
            if (project.getProductOwner().getUsername().equals(projectOwnerUsername)) {
                projectsBelongsProjectOwner.add(project);
            }
        }

        return projectsBelongsProjectOwner;
    }

    public List<Project> getProjectsByScrumMaster(String scrumMasterUsername) {
        List<Project> projectsBelongsScrumMaster = new ArrayList<Project>();
        for (Project project : this.projects.values()) {
            if (project.getScrumMaster().getUsername().equals(scrumMasterUsername)) {
                projectsBelongsScrumMaster.add(project);
            }
        }

        return projectsBelongsScrumMaster;
    }

    public Collection<Project> getAll() {
        return projects.values();
    }

    public void editProject(Project project) {
        this.projects.replace(project.getId(), project);
    }

    public void delProject(String projectId) {
        this.projects.remove(projectId);
    }


    public List<Project> getProjectsByUser(User user) {
        List<Project> projects = this.projects.values().stream()
            .filter(p -> p.getProjectUsers().contains(user))
            .collect(Collectors.toList());

        return projects;
    }
}
