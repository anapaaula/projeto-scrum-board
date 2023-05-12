package com.ufcg.psoft.scrum_board.exception;

public class UserStoryAlreadyExistsException extends Exception{
    private static final long serialVersionUID = 1L;

    public UserStoryAlreadyExistsException(String msg) {
        super(msg);
    }
    
}
