package com.ufcg.psoft.scrum_board.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ufcg.psoft.scrum_board.dto.NewTaskDTO;
import com.ufcg.psoft.scrum_board.dto.TaskDTO;
import com.ufcg.psoft.scrum_board.dto.UpdateTaskDTO;
import com.ufcg.psoft.scrum_board.exception.TaskAlreadyDoneException;
import com.ufcg.psoft.scrum_board.exception.TaskNotFoundException;
import com.ufcg.psoft.scrum_board.exception.UnauthorizedAccessException;
import com.ufcg.psoft.scrum_board.exception.UserNotFoundException;
import com.ufcg.psoft.scrum_board.exception.UserStoryNotFoundException;
import com.ufcg.psoft.scrum_board.model.Task;
import com.ufcg.psoft.scrum_board.model.User;
import com.ufcg.psoft.scrum_board.model.UserStory;
import com.ufcg.psoft.scrum_board.repository.TaskRepository;
import com.ufcg.psoft.scrum_board.repository.UserStoryRepository;
import com.ufcg.psoft.scrum_board.utils.TaskMapper;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRep;

    @Autowired
    private UserStoryRepository userStoryRepository;

    @Autowired
    private DataService dataService;


    public TaskDTO addTask(NewTaskDTO newTaskDTO) throws UserStoryNotFoundException {
        UserStory userStory = this.dataService.getUserStoryById(newTaskDTO.getId_userStory());
        Task task = TaskMapper.convertFromNewTaskDTO(newTaskDTO, userStory);
        this.taskRep.addTask(task);
        return TaskMapper.convertToTaskDTO(task);
    }


    public TaskDTO findTaskById(String id_task) throws TaskNotFoundException {
        Task task = this.getTaskById(id_task);
        return TaskMapper.convertToTaskDTO(task);
    }


    private Task getTaskById(String taskId) throws TaskNotFoundException {
        Task task = this.taskRep.findTaskById(taskId);
        if (task == null) throw new TaskNotFoundException("The task with id '" + taskId + "' was not found!");
        return task;
    }


    public TaskDTO updateTask(UpdateTaskDTO taskDTO, String taskId) throws UserStoryNotFoundException, TaskNotFoundException {
        UserStory userStory = this.dataService.getUserStoryById(taskDTO.getUserStoryId());
        Task task = this.getTaskById(taskId);

        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setUserStory(userStory);
        task.setDone(taskDTO.isDone());

        taskRep.editTask(task);
        return TaskMapper.convertToTaskDTO(task);
    }


    public void deleteTask(String id_task) throws TaskNotFoundException {
        Task task = this.getTaskById(id_task);
        this.taskRep.delTask(task.getId());
    }


    public List<TaskDTO> getAllTask() {
        List<TaskDTO> taskFound = taskRep.getAll()
                                    .stream()
                                    .map(t -> TaskMapper.convertToTaskDTO(t))
                                    .collect(Collectors.toList());
        return taskFound;
    }

    
    public void setRealizada(String id_Task, String idUsuario) throws TaskNotFoundException, UserNotFoundException, TaskAlreadyDoneException, UnauthorizedAccessException {
    	Task task = this.getTaskById(id_Task);
        User user = this.dataService.getUserByUsername(idUsuario);

        if (task.isDone()) throw new TaskAlreadyDoneException("The task is already done!");
        
        boolean isScrumMaster = task.getUserStory().getProject().getScrumMaster().equals(user);
        boolean isDevelopmentTeam = task.getUserStory().getDevs().contains(user);

        if (!(isScrumMaster || isDevelopmentTeam)) throw new UnauthorizedAccessException("The user '" + user.getUsername() + "' has no permission to modify this task!");           
            
        task.setDone(true);
        this.taskRep.editTask(task);

        if (this.taskRep.allTasksDone(task.getUserStory().getId())) {
            task.getUserStory().moveToNextStage();
            this.userStoryRepository.editUserStory(task.getUserStory());
        }
        
    }
    

}