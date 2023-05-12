package com.ufcg.psoft.scrum_board.states;

import com.ufcg.psoft.scrum_board.model.UserStory;

public class WorkInProgress implements DevelopmentState{
    private UserStory userStory;

    public WorkInProgress(UserStory userStory) {
        this.userStory = userStory;
    }

    @Override
    public void moveToNextStage() {
        userStory.setDevelopmentState(new ToVerify(userStory));
        
    }

    @Override
    public String toString(){
        return "Work In Progress";
    }

}
    
