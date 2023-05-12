package com.ufcg.psoft.scrum_board.roles;

import com.ufcg.psoft.scrum_board.enums.ScrumRoleEnum;

public class Researcher implements ScrumRole {

    private final ScrumRoleEnum scrumRoleEnum;

    public Researcher() {
        this.scrumRoleEnum = ScrumRoleEnum.RESEARCHER;
    }
    
    public ScrumRoleEnum getScrumRoleEnum() { return this.scrumRoleEnum; }

}
