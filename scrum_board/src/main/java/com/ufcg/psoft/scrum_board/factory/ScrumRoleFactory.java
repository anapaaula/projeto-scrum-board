package com.ufcg.psoft.scrum_board.factory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ufcg.psoft.scrum_board.enums.ScrumRoleEnum;
import com.ufcg.psoft.scrum_board.exception.UnavailableRoleException;
import com.ufcg.psoft.scrum_board.roles.Developer;
import com.ufcg.psoft.scrum_board.roles.ProductOwner;
import com.ufcg.psoft.scrum_board.roles.Researcher;
import com.ufcg.psoft.scrum_board.roles.ScrumRole;
import com.ufcg.psoft.scrum_board.roles.Trainee;

public class ScrumRoleFactory {

    private Map<ScrumRoleEnum, ScrumRole> scrumRoles;
    private Map<String, ScrumRoleEnum> scrumRolesEnums;

    public ScrumRoleFactory() {
        this.scrumRoles = new HashMap<>();
        this.scrumRolesEnums = new HashMap<>();
        scrumRoles.put(ScrumRoleEnum.PRODUCT_OWNER, new ProductOwner());
        scrumRoles.put(ScrumRoleEnum.DEVELOPER, new Developer());
        scrumRoles.put(ScrumRoleEnum.RESEARCHER, new Researcher());
        scrumRoles.put(ScrumRoleEnum.TRAINEE, new Trainee());
        scrumRolesEnums.put("PRODUCT_OWNER", ScrumRoleEnum.PRODUCT_OWNER);
        scrumRolesEnums.put("DEVELOPER", ScrumRoleEnum.DEVELOPER);
        scrumRolesEnums.put("RESEARCHER", ScrumRoleEnum.RESEARCHER);
        scrumRolesEnums.put("TRAINEE", ScrumRoleEnum.TRAINEE);
    }

    public ScrumRole getRoleByEnum(ScrumRoleEnum scrumRoleEnum) {//throws UnavailableRoleException {
        
        ScrumRole sre = this.scrumRoles.get(scrumRoleEnum);
        //if (sre == null) throw new UnavailableRoleException("O role desejado não existe!");
        return sre;

    }


    public ScrumRoleEnum getEnumByString(String scrumRoleString) {
        return this.scrumRolesEnums.get(scrumRoleString);
    }


    public List<ScrumRoleEnum> getAvailableRoles() {
        return Arrays.asList(ScrumRoleEnum.values());
    } 


    public void verifyRoles(List<String> roles) throws UnavailableRoleException {
        for (String role : roles) {
            this.verifyRole(role);
        }
    }


    public void verifyRole(String role) throws UnavailableRoleException {
        if (!this.getAvailableRoles().stream().anyMatch(r -> r.name().equals(role))) throw new UnavailableRoleException("The role '" + role + "' is not available!");
    }
}
