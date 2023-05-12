package com.ufcg.psoft.scrum_board.repository;

import com.ufcg.psoft.scrum_board.model.User;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class UserRepository {
    private Map<String, User> users;

    public UserRepository() {
        this.users = new HashMap<String, User>();
    }

    public User addUser(User user) {
        this.users.put(user.getUsername(), user);
        return user;
    }

    public User getUserByUsername(String username) {
        return this.users.get(username);
    }

    public List<User> getUsersByUsername(List<String> usernames) {
        List<User> devs = new ArrayList<User>();

        for (String username : usernames) {
            devs.add(this.getUserByUsername(username));
        }

        return devs;
    }

    public Collection<User> getAll() {
        return users.values();
    }

    public void editUser(User user) {
        this.users.replace(user.getUsername(), user);
    }

    public void delUser(String username) {
        this.users.remove(username);
    }

}
