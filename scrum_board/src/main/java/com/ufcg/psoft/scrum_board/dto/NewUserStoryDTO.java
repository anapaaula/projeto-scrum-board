package com.ufcg.psoft.scrum_board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NewUserStoryDTO {

    private String title;
    private String description;
    private String projectId;
    
}
