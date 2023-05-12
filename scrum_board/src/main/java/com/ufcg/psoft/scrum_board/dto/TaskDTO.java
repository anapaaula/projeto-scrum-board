package com.ufcg.psoft.scrum_board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskDTO {

    private String id;
    private String title;
    private String description;
    private String id_userStory;
    private boolean done;

}
