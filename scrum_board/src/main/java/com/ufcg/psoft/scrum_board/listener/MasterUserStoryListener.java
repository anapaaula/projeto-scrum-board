package com.ufcg.psoft.scrum_board.listener;

import com.ufcg.psoft.scrum_board.model.Task;
import com.ufcg.psoft.scrum_board.model.User;
import com.ufcg.psoft.scrum_board.model.UserStory;

public class MasterUserStoryListener extends UserStoryListenerAdapter {
    public MasterUserStoryListener(User user) {
        super(user);
    }

    @Override
    public void descriptionUpdated(UserStory userStory) {
        System.out.println(
            super.getUser().getUsername() + 
            ": The description of the User Story with title '" + 
            userStory.getTitle() + 
            "' from the project '" + 
            userStory.getProject().getName() + 
            "' was updated!"
        );
    }


    @Override
    public void developmentStateUpdated(UserStory userStory) {
        System.out.println(
            super.getUser().getUsername() + 
            ": The User Story with title '" + 
            userStory.getTitle() + 
            "' from the project '" + 
            userStory.getProject().getName() + 
            "' has moved to the stage of " + 
            userStory.getDevelopmentState().toString() + "!"
        );
    }

    @Override
    public void taskUpdated(Task task) {
        System.out.println(
            super.getUser().getUsername() + 
            ": The Task with title '" + 
            task.getTitle() + 
            "' from the User Story '" + 
            task.getUserStory().getTitle() + 
            "' has been done !"
        );
    }
}