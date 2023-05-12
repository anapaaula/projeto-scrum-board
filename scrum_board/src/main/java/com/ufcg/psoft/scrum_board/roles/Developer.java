package com.ufcg.psoft.scrum_board.roles;

import com.ufcg.psoft.scrum_board.enums.ScrumRoleEnum;

public class Developer implements ScrumRole {

    private final ScrumRoleEnum scrumRoleEnum;

    public Developer() {
        this.scrumRoleEnum = ScrumRoleEnum.DEVELOPER;
    }
    
    public ScrumRoleEnum getScrumRoleEnum() { return this.scrumRoleEnum; }

}
