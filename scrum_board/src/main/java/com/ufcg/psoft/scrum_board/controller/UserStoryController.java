package com.ufcg.psoft.scrum_board.controller;

import com.ufcg.psoft.scrum_board.dto.AddUserToUserStoryDTO;
import com.ufcg.psoft.scrum_board.dto.NewUserStoryDTO;
import com.ufcg.psoft.scrum_board.dto.UpdateUserStoryDTO;
import com.ufcg.psoft.scrum_board.dto.UserStoryDTO;
import com.ufcg.psoft.scrum_board.exception.AlreadyAListenerException;
import com.ufcg.psoft.scrum_board.exception.AlreadyInUserStoryDevelopmentTeamException;
import com.ufcg.psoft.scrum_board.exception.InappropriateRoleException;
import com.ufcg.psoft.scrum_board.exception.ProjectNotFoundException;
import com.ufcg.psoft.scrum_board.exception.TaskNotFoundException;
import com.ufcg.psoft.scrum_board.exception.UnauthorizedAccessException;
import com.ufcg.psoft.scrum_board.exception.UserNotFoundException;
import com.ufcg.psoft.scrum_board.exception.UserStoryAlreadyExistsException;
import com.ufcg.psoft.scrum_board.exception.UserStoryNotFoundException;
import com.ufcg.psoft.scrum_board.exception.WrongUserStoryStateException;
import com.ufcg.psoft.scrum_board.service.UserStoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/us")
@CrossOrigin
public class UserStoryController {

    @Autowired
    private UserStoryService userStoryService;

    @Operation(
        summary = "Create a new user story",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Returns a representation of the created user story's data",
                content = {
                    @Content(schema = @Schema(implementation = UserStoryDTO.class)),
                }),
            @ApiResponse(responseCode = "401", description = "Unauthorized exception", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict exception", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found exception", content = @Content),
            })
    @PostMapping("/{loggedUsername}")
    public ResponseEntity<?> create(@PathVariable("loggedUsername") String loggedUsername, @RequestBody NewUserStoryDTO newUserStoryDTO) {
        try{
            UserStoryDTO userStoryDTO = userStoryService.addUserStory(newUserStoryDTO, loggedUsername);
            return new ResponseEntity<>(userStoryDTO, HttpStatus.CREATED);
        } catch(UnauthorizedAccessException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (UserStoryAlreadyExistsException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (ProjectNotFoundException | UserNotFoundException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @Operation(
        summary = "Get data for a specific user story",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Returns a representation of the user's current data",
                content = {
                    @Content(schema = @Schema(implementation = UserStoryDTO.class)),
                }),
            @ApiResponse(responseCode = "404", description = "Not found exception", content = @Content),
            })
    @RequestMapping(value = "/{idUserStory}", method = RequestMethod.GET)
    public ResponseEntity<?> get(@RequestParam("idUserStory") String id_UserStory){
        try{
            UserStoryDTO userStoryDTO = userStoryService.getUserStoryById(id_UserStory);
            return new ResponseEntity<>(userStoryDTO, HttpStatus.OK);
        } catch (UserStoryNotFoundException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(
        summary = "List all user stories",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Returns a list of representations of the projects",
                content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = UserStoryDTO.class))),
                }),
            })
    @GetMapping
    public ResponseEntity<?> list() {
        List<UserStoryDTO> userStoriesDTO = userStoryService.getAllUserStories();
        return new ResponseEntity<>(userStoriesDTO, HttpStatus.OK);
    }

    @Operation(
        summary = "Edit a user story with the provided data",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Returns a representation of the updated user story's current data",
                content = {
                    @Content(schema = @Schema(implementation = UserStoryDTO.class)),
                }),
            @ApiResponse(responseCode = "404", description = "Not found exception", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized exception", content = @Content),
            })
    @RequestMapping(value = "/{loggedUsername}", method = RequestMethod.PUT)
    public ResponseEntity<?> edit(@RequestBody UpdateUserStoryDTO updateUserStoryDTO, @PathVariable("loggedUsername") String loggedUsername){
        try{
            UserStoryDTO userStoryEdited = userStoryService.updateUserStory(updateUserStoryDTO, loggedUsername);
            return new ResponseEntity<>(userStoryEdited, HttpStatus.OK);
        }catch(UserStoryNotFoundException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (UnauthorizedAccessException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @Operation(
        summary = "Delete a user story",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Returns a message indicating the successful operation",
                content = {
                    @Content(schema = @Schema(defaultValue = "UserStory successfully deleted."))
                }),
            @ApiResponse(responseCode = "404", description = "Not found exception", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized exception", content = @Content),
            })
    @RequestMapping(value = "/{loggedUsername}/{idUserStory}", method = RequestMethod.DELETE)
    public ResponseEntity<?> remove(@PathVariable("idUserStory") String id_UserStory, @PathVariable("loggedUsername") String loggedUsername){
        try{
            this.userStoryService.deleteUserStory(id_UserStory, loggedUsername);
            return new ResponseEntity<>("UserStory successfully deleted.", HttpStatus.OK);
        } catch (UserStoryNotFoundException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (UnauthorizedAccessException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @Operation(
        summary = "Join a user story as an user",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Returns a representation of the updated user story's current data",
                content = {
                    @Content(schema = @Schema(implementation = UserStoryDTO.class)),
                }),
            @ApiResponse(responseCode = "404", description = "Not found exception", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized exception", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict exception", content = @Content),
            })
    @PatchMapping(value = "/user/{userStoryId}/{loggedUsername}")
    public ResponseEntity<?> joinUserStory(@PathVariable("loggedUsername") String loggedUsername, @PathVariable String userStoryId) {
        try {
            UserStoryDTO userStoryDTO = this.userStoryService.joinUserStory(loggedUsername, userStoryId);
            return new ResponseEntity<>(userStoryDTO, HttpStatus.OK);
        } catch (UserNotFoundException | UserStoryNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (InappropriateRoleException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (AlreadyInUserStoryDevelopmentTeamException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @Operation(
        summary = "Add a user to an user story",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Returns a representation of the updated user story's current data",
                content = {
                    @Content(schema = @Schema(implementation = UserStoryDTO.class)),
                }),
            @ApiResponse(responseCode = "404", description = "Not found exception", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized exception", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request exception", content = @Content),
            })
    @PatchMapping(value = "/user/{loggedUsername}")
    public ResponseEntity<?> addUserToUS(@PathVariable("loggedUsername") String loggedUsername, @RequestBody AddUserToUserStoryDTO body) {
        try {
            UserStoryDTO userStoryDTO = this.userStoryService.addUserToUS(loggedUsername, body);
            return new ResponseEntity<>(userStoryDTO, HttpStatus.OK);
        } catch (InappropriateRoleException | AlreadyInUserStoryDevelopmentTeamException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (UnauthorizedAccessException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (UserNotFoundException | UserStoryNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }  

    @Operation(
        summary = "Subscribe to an user story as an user",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Returns a message indicating the successful operation",
                content = {
                    @Content(schema = @Schema(defaultValue = "User subscribed to User Story!"))
                }),
            @ApiResponse(responseCode = "404", description = "Not found exception", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized exception", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict exception", content = @Content),
            })
    @PatchMapping(value = "/subscribe/{userStoryId}/{loggedUsername}")
    public ResponseEntity<?> subscribeToUserStory(@PathVariable("loggedUsername") String loggedUsername, @PathVariable("userStoryId") String userStoryId) {
        try {
            this.userStoryService.subscribeToUserStory(loggedUsername, userStoryId);
            return new ResponseEntity<>("User subscribed to User Story!", HttpStatus.OK);
        } catch (UserNotFoundException | UserStoryNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (UnauthorizedAccessException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (AlreadyAListenerException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @Operation(
        summary = "Tag user story as \"Done\"",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Returns a representation of the updated user story's current data",
                content = {
                    @Content(schema = @Schema(implementation = UserStoryDTO.class)),
                }),
            @ApiResponse(responseCode = "404", description = "Not found exception", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized exception", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict exception", content = @Content),
            })
    @PatchMapping("/done/{userStoryId}/{loggedUsername}")
    public ResponseEntity<?> setUserStoryAsDone(@PathVariable("loggedUsername") String loggedUsername, @PathVariable("userStoryId") String userStoryId) {
        try {
            UserStoryDTO usDTO = this.userStoryService.setDone(userStoryId, loggedUsername);
            return new ResponseEntity<>(usDTO, HttpStatus.OK);
        } catch (TaskNotFoundException | UserNotFoundException | UserStoryNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (UnauthorizedAccessException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (WrongUserStoryStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }    
    }

    @Operation(
        summary = "Tag user story as \"To Verify\"",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Returns a representation of the updated user story's current data",
                content = {
                    @Content(schema = @Schema(implementation = UserStoryDTO.class)),
                }),
            @ApiResponse(responseCode = "404", description = "Not found exception", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized exception", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict exception", content = @Content),
            })
    @PatchMapping("/to-verify/{userStoryId}/{loggedUsername}")
    public ResponseEntity<?> setUserStoryAsToVerify(@PathVariable("loggedUsername") String loggedUsername, @PathVariable("userStoryId") String userStoryId) {
        try {
            UserStoryDTO usDTO = this.userStoryService.setToVerify(userStoryId, loggedUsername);
            return new ResponseEntity<>(usDTO, HttpStatus.OK);
        } catch (TaskNotFoundException | UserNotFoundException | UserStoryNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (UnauthorizedAccessException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (WrongUserStoryStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }       
    }
    
}
