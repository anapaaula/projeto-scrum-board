package com.ufcg.psoft.scrum_board.controller;

import com.ufcg.psoft.scrum_board.dto.AddUserToProjectDTO;
import com.ufcg.psoft.scrum_board.dto.NewProjectDTO;
import com.ufcg.psoft.scrum_board.dto.ProjectDTO;
import com.ufcg.psoft.scrum_board.dto.UpdateProjectDTO;
import com.ufcg.psoft.scrum_board.dto.UserStoryReportDTO;
import com.ufcg.psoft.scrum_board.dto.UserStoryReportForScrumMaster;
import com.ufcg.psoft.scrum_board.dto.UserStoryReportForUserDTO;
import com.ufcg.psoft.scrum_board.exception.InappropriateRoleException;
import com.ufcg.psoft.scrum_board.exception.ProjectNotFoundException;
import com.ufcg.psoft.scrum_board.exception.UnauthorizedAccessException;
import com.ufcg.psoft.scrum_board.exception.UnavailableRoleException;
import com.ufcg.psoft.scrum_board.exception.UserNotFoundException;
import com.ufcg.psoft.scrum_board.service.ProjectService;

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
@RequestMapping("/project")
@CrossOrigin
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Operation(
        summary = "Initialize and saves a new project",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Returns a representation of the created project's data",
                content = {
                    @Content(schema = @Schema(implementation = ProjectDTO.class)),
                }),
            @ApiResponse(responseCode = "404", description = "Not found exception", content = @Content),
            })
    @PostMapping(value = "/{loggedUsername}")
    public ResponseEntity<?> create(@RequestBody NewProjectDTO newProjectDTO, @PathVariable("loggedUsername") String loggedUsername) {
        try {
            ProjectDTO project = projectService.addProject(newProjectDTO, loggedUsername);
            return new ResponseEntity<>(project, HttpStatus.CREATED);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(
        summary = "Get data for a specific project",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Returns a representation of the project's current data",
                content = {
                    @Content(schema = @Schema(implementation = ProjectDTO.class)),
                }),
            @ApiResponse(responseCode = "404", description = "Not found exception", content = @Content),
            })
    @RequestMapping(value = "/{projectId}", method = RequestMethod.GET)
    public ResponseEntity<?> get(@PathVariable("projectId") String projectId) throws ProjectNotFoundException {
        try {
            ProjectDTO project = projectService.findProjectById(projectId);
            return new ResponseEntity<>(project, HttpStatus.OK);
        } catch (ProjectNotFoundException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(
        summary = "List all projects",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Returns a list of representations of the projects",
                content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = ProjectDTO.class)))
                })
            })
    @GetMapping
    public ResponseEntity<?> list() {
        List<ProjectDTO> projects = projectService.getAllProjects();
        return new ResponseEntity<>(projects, HttpStatus.OK);
    }

    @Operation(
        summary = "Edit a project with provided data",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Returns a representation of the updated project's current data",
                content = {
                    @Content(schema = @Schema(implementation = ProjectDTO.class))
                }),
            @ApiResponse(responseCode = "404", description = "Not found exception", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized exception", content = @Content),
        })
    @RequestMapping(value = "/{projectId}/{loggedUsername}", method = RequestMethod.PUT)
    public ResponseEntity<?> edit(@RequestBody UpdateProjectDTO projectDTO, @PathVariable("loggedUsername") String loggedUsername, @PathVariable("projectId") String projectId) throws ProjectNotFoundException {
        try {
            ProjectDTO projectEdited = projectService.updateProject(loggedUsername, projectId, projectDTO);
            return new ResponseEntity<>(projectEdited, HttpStatus.OK);
        } catch (ProjectNotFoundException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (UnauthorizedAccessException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @Operation(
        summary = "Delete a project",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Returns a message indicating the successful operation",
                content = {
                    @Content(schema = @Schema(defaultValue = "Project successfully deleted."))
                }),
            @ApiResponse(responseCode = "404", description = "Not found exception", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized exception", content = @Content),
        })
    @RequestMapping(value = "/{loggedUsername}/{projectId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> remove(@PathVariable("loggedUsername") String loggedUsername, @PathVariable("projectId") String projectId) {
        try {
            projectService.deleteProject(projectId, loggedUsername);
            return new ResponseEntity<String>("Project successfully deleted.", HttpStatus.OK);
        } catch (ProjectNotFoundException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (UnauthorizedAccessException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @Operation(
        summary = "Add a user to a project",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Returns a representation of the updated project's current data",
                content = {
                    @Content(schema = @Schema(implementation = ProjectDTO.class)),
                 }),
            @ApiResponse(responseCode = "400", description = "Bad request exception", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found exception", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized exception", content = @Content),
        })
    @PatchMapping(value = "/user/{loggedUsername}")
    public ResponseEntity<?> addUserToProject(@PathVariable("loggedUsername") String loggedUsername, @RequestBody AddUserToProjectDTO addUserToProjectDTO) {
        try {
            ProjectDTO projectDTO = this.projectService.addUserToProject(loggedUsername, addUserToProjectDTO);
            return new ResponseEntity<>(projectDTO, HttpStatus.OK);
        } catch (ProjectNotFoundException | UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (UnauthorizedAccessException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (InappropriateRoleException | UnavailableRoleException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
        summary = "Get reports for all user stories the user is participating",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Returns a report for all User Stories related to the user",
                content = {
                    @Content(schema = @Schema(anyOf = {
                        UserStoryReportForUserDTO.class, UserStoryReportDTO.class, UserStoryReportForScrumMaster.class
                    })),
                 }),
            @ApiResponse(responseCode = "404", description = "Not found exception", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized exception", content = @Content),
        })
    @GetMapping("/getReports/{loggedUsername}/{projectId}")
    public ResponseEntity<?> getAllReports(@PathVariable("loggedUsername") String loggedUsername, @PathVariable("projectId") String projectId) {
        try {
            return new ResponseEntity<>(this.projectService.getAllReports(loggedUsername, projectId), HttpStatus.OK);
        } catch (ProjectNotFoundException | UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (UnauthorizedAccessException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

}
