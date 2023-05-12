package com.ufcg.psoft.scrum_board.utils;

import java.util.stream.Collectors;

import com.ufcg.psoft.scrum_board.dto.UserDTO;
import com.ufcg.psoft.scrum_board.factory.ScrumRoleFactory;
import com.ufcg.psoft.scrum_board.model.User;

public class UserMapper {
    
    public static UserDTO convertToUserDTO(User user) {
        return new UserDTO(
            user.getFullName(), user.getEmail(), user.getUsername(), 
            user.getRoles().stream().map(r -> r.getScrumRoleEnum().name()).collect(Collectors.toList())
        );
    }

    public static User convertFromUserDTO(UserDTO userDTO) {
        ScrumRoleFactory srf = new ScrumRoleFactory();
        return new User(
            userDTO.getFullName(), userDTO.getEmail(), userDTO.getUsername(), 
            userDTO.getRoles().stream().map(t -> srf.getRoleByEnum(srf.getEnumByString(t))).collect(Collectors.toList())
        );
    }

}
