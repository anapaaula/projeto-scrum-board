package com.ufcg.psoft.scrum_board.listener;

import com.ufcg.psoft.scrum_board.model.Task;
import com.ufcg.psoft.scrum_board.model.UserStory;

public interface UserStoryListener {
    
    public void descriptionUpdated(UserStory userStory);

    public void developmentStateUpdated(UserStory userStory);

    public void taskUpdated(Task task);
    
}
