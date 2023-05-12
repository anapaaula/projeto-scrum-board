package com.ufcg.psoft.scrum_board.controller;

import com.ufcg.psoft.scrum_board.dto.UserDTO;
import com.ufcg.psoft.scrum_board.enums.ScrumRoleEnum;
import com.ufcg.psoft.scrum_board.exception.DangerousActionException;
import com.ufcg.psoft.scrum_board.exception.UnavailableRoleException;
import com.ufcg.psoft.scrum_board.exception.UserAlreadyExistsException;
import com.ufcg.psoft.scrum_board.exception.UserNotFoundException;
import com.ufcg.psoft.scrum_board.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(
        summary = "Create a new user",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Returns a representation of the created user's data",
                content = {
                    @Content(schema = @Schema(implementation = UserDTO.class)),
                }),
            @ApiResponse(responseCode = "409", description = "Conflict exception", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request exception", content = @Content),
            })
    @PostMapping
    public ResponseEntity<?> create(@RequestBody UserDTO userDTO) throws UserAlreadyExistsException {
        try {
            UserDTO user = userService.addUser(userDTO);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (UserAlreadyExistsException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (UnavailableRoleException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
        summary = "Get data for a specific user",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Returns a representation of the user's current data",
                content = {
                    @Content(schema = @Schema(implementation = UserDTO.class)),
                }),
            @ApiResponse(responseCode = "404", description = "Not found exception", content = @Content),
            })
    @GetMapping(value = "/{username}")
    public ResponseEntity<?> get(@PathVariable("username") String username) throws UserNotFoundException {
        try {
            UserDTO user = userService.findUserByUsername(username);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(
        summary = "List all users",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Returns a list of representations of the projects",
                content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = UserDTO.class))),
                }),
            })
    @GetMapping
    public ResponseEntity<?> list() {
        List<UserDTO> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @Operation(
        summary = "Edit a user with the provided data",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Returns a representation of the updated task's current data",
                content = {
                    @Content(schema = @Schema(implementation = UserDTO.class)),
                }),
            @ApiResponse(responseCode = "404", description = "Not found exception", content = @Content),
            })
    @RequestMapping(value = "/{loggedUsername}", method = RequestMethod.PUT)
    public ResponseEntity<?> edit(@RequestBody UserDTO userDTO, @PathVariable("loggedUsername") String loggedUsername) throws UserNotFoundException {
        try {
            UserDTO userEdited = userService.updateUser(userDTO, loggedUsername);
            return new ResponseEntity<>(userEdited, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(
        summary = "Deletes a user",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Returns a message indicating the successful operation",
                content = {
                    @Content(schema = @Schema(defaultValue = "User successfully deleted."))
                }),
            @ApiResponse(responseCode = "404", description = "Not found exception", content = @Content),
            @ApiResponse(responseCode = "406", description = "Not acceptable exception", content = @Content),
            })
    @RequestMapping(value = "/{loggedUsername}", method = RequestMethod.DELETE)
    public ResponseEntity<?> removerUser( @PathVariable("loggedUsername") String loggedUsername) throws UserNotFoundException {
        try {
            userService.deleteUser(loggedUsername);
            return new ResponseEntity<String>("user successfully deleted.", HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (DangerousActionException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @Operation(
        summary = "List all available roles",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Returns a list of representations of the projects",
                content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = ScrumRoleEnum.class))),
                }),
            })
    @GetMapping("/roles")
    public ResponseEntity<?> getAvailableRoles() {
        return new ResponseEntity<>(this.userService.getAvailableRoles(), HttpStatus.OK);
    }
}
