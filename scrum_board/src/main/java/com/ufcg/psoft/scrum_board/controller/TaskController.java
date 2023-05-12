package com.ufcg.psoft.scrum_board.controller;

import com.ufcg.psoft.scrum_board.dto.NewTaskDTO;
import com.ufcg.psoft.scrum_board.dto.TaskDTO;
import com.ufcg.psoft.scrum_board.dto.UpdateTaskDTO;
import com.ufcg.psoft.scrum_board.exception.TaskAlreadyDoneException;
import com.ufcg.psoft.scrum_board.exception.TaskNotFoundException;
import com.ufcg.psoft.scrum_board.exception.UnauthorizedAccessException;
import com.ufcg.psoft.scrum_board.exception.UserNotFoundException;
import com.ufcg.psoft.scrum_board.exception.UserStoryNotFoundException;
import com.ufcg.psoft.scrum_board.service.TaskService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/task")
@CrossOrigin
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Operation(
        summary = "Initializes and saves a new task",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Returns a representation of the created task's data",
                content = {
                    @Content(schema = @Schema(implementation = TaskDTO.class)),
                }),
            @ApiResponse(responseCode = "404", description = "Not found exception", content = @Content),
            })
    @PostMapping
    public ResponseEntity<?> create(@RequestBody NewTaskDTO newTaskDTO, UriComponentsBuilder ucBuilder) {
        try{
            TaskDTO task = taskService.addTask(newTaskDTO);
            return new ResponseEntity<>(task, HttpStatus.CREATED);
        } catch(UserStoryNotFoundException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(
        summary = "Get data for a specific task",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Returns a representation of the task's current data",
                content = {
                    @Content(schema = @Schema(implementation = TaskDTO.class)),
                }),
            @ApiResponse(responseCode = "404", description = "Not found exception", content = @Content),
            })
    @RequestMapping(value = "/{idTask}", method = RequestMethod.GET)
    public ResponseEntity<?> get(@RequestParam("idTask") String id_Task){
        try{
            TaskDTO task = taskService.findTaskById(id_Task);
            return new ResponseEntity<>(task, HttpStatus.OK);
        } catch (TaskNotFoundException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(
        summary = "List all tasks",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Returns a list of representations of the projects",
                content = {
                    @Content(schema = @Schema(implementation = TaskDTO.class)),
                }),
            })
    @GetMapping
    public ResponseEntity<?> list() {
        List<TaskDTO> task = taskService.getAllTask();
        return new ResponseEntity<>(task, HttpStatus.OK);
    }

    @Operation(
        summary = "Edit a task with the provided data",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Returns a representation of the updated task's current data",
                content = {
                    @Content(schema = @Schema(implementation = TaskDTO.class)),
                }),
            @ApiResponse(responseCode = "404", description = "Not found exception", content = @Content),
            })
    @RequestMapping(value = "/{idTask}", method = RequestMethod.PUT)
    public ResponseEntity<?> edit(@RequestBody UpdateTaskDTO taskDTO, @PathVariable("idTask") String id_Task){
        try {
            TaskDTO TaskEdited = taskService.updateTask(taskDTO, id_Task);
            return new ResponseEntity<>(TaskEdited, HttpStatus.OK);
        }catch(TaskNotFoundException | UserStoryNotFoundException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(
        summary = "Delete a task",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Returns a message indicating the successful operation",
                content = {
                    @Content(schema = @Schema(defaultValue = "Project successfully deleted."))
                }),
            @ApiResponse(responseCode = "404", description = "Not found exception", content = @Content),
            })
    @RequestMapping(value = "/{idTask}", method = RequestMethod.DELETE)
    public ResponseEntity<?> remove(@PathVariable("idTask") String id_Task){
        try{
            taskService.deleteTask(id_Task);
            return new ResponseEntity<String>("The task was deleted successfully!", HttpStatus.OK);
        } catch (TaskNotFoundException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(
        summary = "Tag a task as \"Done\"",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Returns a message indicating the successful operation",
                content = {
                    @Content(schema = @Schema(defaultValue = "Task marked as Done successfully!"))
                }),
            @ApiResponse(responseCode = "404", description = "Not found exception", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict exception", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized exception", content = @Content),
            })
    @PatchMapping("/done/{taskId}/{loggedUsername}")
    public ResponseEntity<?> setTaskDone(@PathVariable("loggedUsername") String loggedUsername, @PathVariable("taskId") String taskId) {
        try {
            this.taskService.setRealizada(taskId, loggedUsername);
            return new ResponseEntity<>("Task marked as Done successfully!", HttpStatus.OK);
        } catch (TaskNotFoundException | UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (TaskAlreadyDoneException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (UnauthorizedAccessException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    
}
