package com.ufcg.psoft.scrum_board.roles;

import com.ufcg.psoft.scrum_board.enums.ScrumRoleEnum;

public class ProductOwner implements ScrumRole {

    private final ScrumRoleEnum scrumRoleEnum;

    public ProductOwner() {
        this.scrumRoleEnum = ScrumRoleEnum.PRODUCT_OWNER;
    }
    
    public ScrumRoleEnum getScrumRoleEnum() { return this.scrumRoleEnum; }

}
