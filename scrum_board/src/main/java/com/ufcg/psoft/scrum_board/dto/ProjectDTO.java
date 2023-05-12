package com.ufcg.psoft.scrum_board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ProjectDTO {

    private String id;
    private String name;
    private String description;
    private String partnerInstitution;
    private String scrumMasterUsername;
    private String productOwnerUsername;
    private List<String> developersUsernames;
    private List<String> researchersUsernames;
    private List<String> traineesUsernames;
    private List<String> userStoriesId;
}
