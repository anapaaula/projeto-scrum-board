package com.ufcg.psoft.scrum_board.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserStoryDTO {

    private String id;
    private String title;
    private String description;
    private String projectId;
    private List<String> devs;
    private String developmentState;
    private List<TaskDTO> tasks;
    
}
