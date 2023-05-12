package com.ufcg.psoft.scrum_board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateTaskDTO {
    
    private String title;
    private String description;
    private String userStoryId;
    private boolean done;

}
