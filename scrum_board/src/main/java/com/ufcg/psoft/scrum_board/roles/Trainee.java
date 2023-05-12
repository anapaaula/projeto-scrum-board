package com.ufcg.psoft.scrum_board.roles;

import com.ufcg.psoft.scrum_board.enums.ScrumRoleEnum;

public class Trainee implements ScrumRole {

    private final ScrumRoleEnum scrumRoleEnum;

    public Trainee() {
        this.scrumRoleEnum = ScrumRoleEnum.TRAINEE;
    }
    
    public ScrumRoleEnum getScrumRoleEnum() { return this.scrumRoleEnum; }
    
}
