package com.ufcg.psoft.scrum_board.utils;

import com.ufcg.psoft.scrum_board.dto.NewTaskDTO;
import com.ufcg.psoft.scrum_board.dto.TaskDTO;
import com.ufcg.psoft.scrum_board.model.Task;
import com.ufcg.psoft.scrum_board.model.UserStory;

public class TaskMapper {
    
    public static TaskDTO convertToTaskDTO(Task task) {
        return new TaskDTO(task.getId(), task.getTitle(), task.getDescription(), task.getUserStory().getId(), task.isDone());
    }

    public static Task convertFromTaskDTO(TaskDTO taskDTO, UserStory associatedUserStory) {
        return new Task(taskDTO.getId(), taskDTO.getTitle(), taskDTO.getDescription(), associatedUserStory, taskDTO.isDone());
    }

    public static Task convertFromNewTaskDTO(NewTaskDTO newTaskDTO, UserStory associatedUserStory) {
        return new Task(newTaskDTO.getTitle(), newTaskDTO.getDescription(), associatedUserStory);
    }

}
