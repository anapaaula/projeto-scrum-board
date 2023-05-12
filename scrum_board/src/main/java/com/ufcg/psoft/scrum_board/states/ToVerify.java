package com.ufcg.psoft.scrum_board.states;

import com.ufcg.psoft.scrum_board.model.UserStory;

public class ToVerify implements DevelopmentState{
    private UserStory userStory;

    public ToVerify(UserStory userStory) {
        this.userStory = userStory;
    }

    @Override
    public void moveToNextStage() {
        userStory.setDevelopmentState(new Done(userStory));
        
    }

    @Override
    public String toString(){
        return "To Verify";
    }
    
}
