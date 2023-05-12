package com.ufcg.psoft.scrum_board.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.ufcg.psoft.scrum_board.enums.ScrumRoleEnum;

@Getter
@Setter
@AllArgsConstructor
public class Project {

    public String id;
    public String name;
    public String description;
    public String partnerInstitution;
    public User scrumMaster;
    public User productOwner;
    public Set<User> developers;
    public Set<User> researchers;
    public Set<User> trainees;

    public Project(String name, String description, String partnerInstitution, User scrumMaster) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.partnerInstitution = partnerInstitution;
        this.scrumMaster = scrumMaster;
        this.productOwner = new User();
        this.developers = new HashSet<>();
        this.researchers = new HashSet<>();
        this.trainees = new HashSet<>();
    }

    public void addUserToProject(User user, ScrumRoleEnum sre) {
        if (sre.equals(ScrumRoleEnum.PRODUCT_OWNER)) this.productOwner = user;
        if (sre.equals(ScrumRoleEnum.DEVELOPER)) this.developers.add(user);
        if (sre.equals(ScrumRoleEnum.RESEARCHER)) this.researchers.add(user);
        if (sre.equals(ScrumRoleEnum.TRAINEE)) this.trainees.add(user);
    }


    public void removeUserFromProject(User user) {
        this.productOwner = new User();
        this.developers.remove(user);
        this.researchers.remove(user);
        this.trainees.remove(user);
    }


    public List<User> getDevelopmentTeam() {
        List<User> users = new ArrayList<>();
        this.developers.forEach(u -> users.add(u));
        this.researchers.forEach(u -> users.add(u));
        this.trainees.forEach(u -> users.add(u));
        return users;
    }


    public List<User> getProjectUsers() {
        List<User> users = new ArrayList<>();
        users.add(this.scrumMaster);
        users.add(this.productOwner);
        this.developers.forEach(u -> users.add(u));
        this.researchers.forEach(u -> users.add(u));
        this.trainees.forEach(u -> users.add(u));
        return users;
    }
}
