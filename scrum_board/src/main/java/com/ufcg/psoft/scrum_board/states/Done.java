package com.ufcg.psoft.scrum_board.states;

import com.ufcg.psoft.scrum_board.model.UserStory;

public class Done implements DevelopmentState{
    private UserStory userStory;

    public Done(UserStory userStory) {
        this.userStory = userStory;
    }

    @Override
    public void moveToNextStage() {
        userStory.setDevelopmentState(this);
        
    }

    @Override
    public String toString(){
        return "Done";
    }
    
}
