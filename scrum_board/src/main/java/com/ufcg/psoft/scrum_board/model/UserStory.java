package com.ufcg.psoft.scrum_board.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.ufcg.psoft.scrum_board.listener.UserStoryListener;
import com.ufcg.psoft.scrum_board.states.DevelopmentState;
import com.ufcg.psoft.scrum_board.states.ToDo;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserStory {

    private String id;
    private String title;
    private String description;
    private Project project;
    private List<User> devs;
    private DevelopmentState developmentState;
    private Set<UserStoryListener> listeners;

    public UserStory(String title, String description, Project project){
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.project = project;
        this.devs = new ArrayList<>();
        this.developmentState = new ToDo(this);
        this.listeners = new HashSet<>();
    }

    
    public void moveToNextStage(){
        this.developmentState.moveToNextStage();
        this.developmentStateUpdatedTrigger();
    }


    public void addDev(User user) {
        this.devs.add(user);

        if (this.developmentState.toString().equals("To Do")) {
            this.developmentState.moveToNextStage();
        }
    }


    public void addListener(UserStoryListener userStoryListener) {
        this.listeners.add(userStoryListener);
    }


    public void removeListener(UserStoryListener userStoryListener) {
        this.listeners.remove(userStoryListener);
    }


    private void descriptionUpdatedTrigger() {
        this.listeners.forEach(l -> l.descriptionUpdated(this));
    }


    private void developmentStateUpdatedTrigger() {
        this.listeners.forEach(l -> l.developmentStateUpdated(this));
    }


    public void setDescription(String description) {
        this.description = description;
        this.descriptionUpdatedTrigger();
    }


    public void removeDev(User user) {
        this.devs.remove(user);
    }
}