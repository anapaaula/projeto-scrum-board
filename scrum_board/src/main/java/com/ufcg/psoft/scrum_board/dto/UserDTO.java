package com.ufcg.psoft.scrum_board.dto;

import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTO {

    private String fullName;
    private String email;
    private String username;
    private List<String> roles;

}

