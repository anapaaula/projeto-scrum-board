package com.ufcg.psoft.scrum_board.dto;

import lombok.Data;

@Data
public class NewTaskDTO {
    
    private String title;
    private String description;
    private String id_userStory;

    public NewTaskDTO(String title, String description, String id_userStory) {
        this.title = title;
        this.description = description;
        this.id_userStory = id_userStory;
    }
    
}
