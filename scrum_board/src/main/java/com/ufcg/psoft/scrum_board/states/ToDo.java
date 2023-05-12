package com.ufcg.psoft.scrum_board.states;

import com.ufcg.psoft.scrum_board.model.UserStory;

public class ToDo implements DevelopmentState{
    private UserStory userStory;

    public ToDo(UserStory userStory) {
        this.userStory = userStory;
    }

    @Override
    public void moveToNextStage() {
        userStory.setDevelopmentState(new WorkInProgress(userStory));
        
    }

    @Override
    public String toString(){
        return "To Do";
    }
    
}
