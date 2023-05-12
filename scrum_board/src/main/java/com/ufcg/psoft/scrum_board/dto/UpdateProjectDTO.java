package com.ufcg.psoft.scrum_board.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateProjectDTO {
    
    private String name;
    private String description;
    private String partnerInstitution;
    private String scrumMasterUsername;
    private String projectOwnerUsername;
    private List<String> developersUsernames;
    private List<String> researchersUsernames;
    private List<String> traineesUsernames;

}
