package com.ufcg.psoft.scrum_board.model;

import java.util.List;

import com.ufcg.psoft.scrum_board.roles.ScrumRole;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class User {

    private String fullName;
    private String email;
    private String username;
    private List<ScrumRole> roles;

    public User(String fullName, String email, String username, List<ScrumRole> roles) {
        this.fullName = fullName;
        this.email = email;
        this.username = username;
        this.roles = roles;
    }
}
