package com.ufcg.psoft.scrum_board.utils;

import java.util.List;
import java.util.stream.Collectors;

import com.ufcg.psoft.scrum_board.dto.ProjectDTO;
import com.ufcg.psoft.scrum_board.model.Project;
import com.ufcg.psoft.scrum_board.model.UserStory;

public class ProjectMapper {


    public static ProjectDTO convertToProjectDTO(Project project, List<UserStory> userStories) {
        return new ProjectDTO(
            project.getId(), project.getName(), project.getDescription(),
            project.getPartnerInstitution(), project.getScrumMaster().getUsername(), 
            project.getProductOwner().getUsername(), 
            project.getDevelopers().stream().map(d -> d.getUsername()).collect(Collectors.toList()), 
            project.getResearchers().stream().map(r -> r.getUsername()).collect(Collectors.toList()), 
            project.getTrainees().stream().map(t -> t.getUsername()).collect(Collectors.toList()),
            userStories.stream().map(us -> us.getId()).collect(Collectors.toList())
        );
    }
    
}
