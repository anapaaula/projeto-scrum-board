package com.ufcg.psoft.scrum_board.listener;

import com.ufcg.psoft.scrum_board.model.Task;
import com.ufcg.psoft.scrum_board.model.User;

public class OwnersUserStoryListener extends UserStoryListenerAdapter {
    public OwnersUserStoryListener(User user) {
        super(user);
    }

    @Override
    public void taskUpdated(Task task) {
        System.out.println(
            super.getUser().getUsername() + 
            ": The Task with title '" + 
            task.getTitle() + 
            "' from the User Story '" + 
            task.getUserStory().getTitle() + 
            "' has moved to the stage of Done"
        );
    }
}