package com.ufcg.psoft.scrum_board.listener;

import com.ufcg.psoft.scrum_board.model.Task;
import com.ufcg.psoft.scrum_board.model.User;
import com.ufcg.psoft.scrum_board.model.UserStory;

import lombok.Data;

@Data
public class UserStoryListenerAdapter implements UserStoryListener {
    
    private User user;

    public UserStoryListenerAdapter(User user) {
        this.user = user;
    }

    public void descriptionUpdated(UserStory userStory) {}

    public void developmentStateUpdated(UserStory userStory) {}

    public void taskUpdated(Task task) {}

}
