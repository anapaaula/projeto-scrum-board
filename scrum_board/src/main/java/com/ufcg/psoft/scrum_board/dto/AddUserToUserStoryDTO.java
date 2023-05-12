package com.ufcg.psoft.scrum_board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddUserToUserStoryDTO {
    
    private String userStoryId;
    private String username;

}
