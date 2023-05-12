package com.ufcg.psoft.scrum_board.service;

import com.ufcg.psoft.scrum_board.dto.UserDTO;
import com.ufcg.psoft.scrum_board.enums.ScrumRoleEnum;
import com.ufcg.psoft.scrum_board.exception.DangerousActionException;
import com.ufcg.psoft.scrum_board.exception.UnavailableRoleException;
import com.ufcg.psoft.scrum_board.exception.UserAlreadyExistsException;
import com.ufcg.psoft.scrum_board.exception.UserNotFoundException;
import com.ufcg.psoft.scrum_board.factory.ScrumRoleFactory;
import com.ufcg.psoft.scrum_board.model.User;
import com.ufcg.psoft.scrum_board.repository.UserRepository;
import com.ufcg.psoft.scrum_board.utils.UserMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRep;
    
    @Autowired
    private DataService dataService;

    public UserDTO addUser(UserDTO userDTO) throws UserAlreadyExistsException, UnavailableRoleException {
        if(this.userAlreadyExists(userDTO.getUsername())) throw new UserAlreadyExistsException("User '" + userDTO.getUsername() + "' already exists!");
        (new ScrumRoleFactory()).verifyRoles(userDTO.getRoles());

        User user = UserMapper.convertFromUserDTO(userDTO);
        this.userRep.addUser(user);

        return UserMapper.convertToUserDTO(user);
    }


    private boolean userAlreadyExists(String username) {
        User user = userRep.getUserByUsername(username);
        if (user == null) return false;
        return true;
    }


    public UserDTO findUserByUsername(String username) throws UserNotFoundException {
        User user = userRep.getUserByUsername(username);
        if (user == null) throw new UserNotFoundException("The user '" + username + "' wasn't found!");
        return(UserMapper.convertToUserDTO(user));
    }


    public List<UserDTO> getAllUsers() {
        List<UserDTO> usersFound = userRep.getAll()
                                    .stream()
                                    .map(u -> UserMapper.convertToUserDTO(u))
                                    .collect(Collectors.toList());

        return usersFound;
    }


    public UserDTO updateUser(UserDTO userDTO, String username) throws UserNotFoundException {
        if (!this.userAlreadyExists(username)) throw new UserNotFoundException("The user '" + username + "' doesn't exist!");
        if (this.userAlreadyExists(userDTO.getUsername())) throw new UserNotFoundException("The username '" + userDTO.getUsername() + "' is already in use!");

        User user = this.userRep.getUserByUsername(username);

        user.setFullName(userDTO.getFullName());
        user.setEmail(userDTO.getEmail());
        user.setUsername(userDTO.getUsername());
        this.userRep.editUser(user);

        return UserMapper.convertToUserDTO(user);
    }


    public void deleteUser(String username) throws UserNotFoundException, DangerousActionException {
        User user = this.dataService.getUserByUsername(username);
        this.dataService.delUser(user);
    }


    public List<ScrumRoleEnum> getAvailableRoles() {
        return (new ScrumRoleFactory()).getAvailableRoles();
    } 

}
