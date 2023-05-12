package com.ufcg.psoft.scrum_board.model;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class Task {

    private String id;
    private String title;
    private String description;
    private UserStory userStory;
    private boolean done;



    public Task(String title, String description, UserStory userStory) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.userStory = userStory;
        this.done = false;
    }

    public void setDone(boolean valor) {
        this.done = valor;
        this.userStory.getListeners().forEach(l-> l.taskUpdated(this));
    }
    
}