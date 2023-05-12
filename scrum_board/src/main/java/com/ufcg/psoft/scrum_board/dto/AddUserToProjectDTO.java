package com.ufcg.psoft.scrum_board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddUserToProjectDTO {
    
    private String projectId;
    private String username;
    private String role;

}
