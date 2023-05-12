package com.ufcg.psoft.scrum_board.listener;

import com.ufcg.psoft.scrum_board.model.User;
import com.ufcg.psoft.scrum_board.model.UserStory;

public class NormalUserStoryListener extends UserStoryListenerAdapter {
    
    public NormalUserStoryListener(User user) {
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

}
