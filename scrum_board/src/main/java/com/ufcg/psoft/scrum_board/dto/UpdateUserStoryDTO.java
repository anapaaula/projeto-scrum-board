package com.ufcg.psoft.scrum_board.dto;

import java.util.List;

import lombok.Data;


@Data
public class UpdateUserStoryDTO {
    private String id;
    private String title;
    private String description;
    private List<String> devs;
    private boolean moveToNextState;

    
}
