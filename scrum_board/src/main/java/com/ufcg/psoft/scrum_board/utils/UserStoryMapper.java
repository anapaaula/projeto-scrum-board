package com.ufcg.psoft.scrum_board.utils;

import java.util.List;
import java.util.stream.Collectors;

import com.ufcg.psoft.scrum_board.dto.UserStoryDTO;
import com.ufcg.psoft.scrum_board.model.Task;
import com.ufcg.psoft.scrum_board.model.UserStory;

public class UserStoryMapper {
    

    public static UserStoryDTO convertToUserStoryDTO (UserStory userStory, List<Task> tasks) {
        return new UserStoryDTO(
            userStory.getId(), userStory.getTitle(), userStory.getDescription(), 
            userStory.getProject().getId(), userStory.getDevs().stream().map(d -> d.getUsername()).collect(Collectors.toList()), 
            userStory.getDevelopmentState().toString(), tasks.stream().map(t -> TaskMapper.convertToTaskDTO(t)).collect(Collectors.toList())
        );
    }

}
