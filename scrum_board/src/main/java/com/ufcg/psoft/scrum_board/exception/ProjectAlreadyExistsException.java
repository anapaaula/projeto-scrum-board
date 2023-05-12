package com.ufcg.psoft.scrum_board.exception;

public class ProjectAlreadyExistsException extends Exception {
    private static final long serialVersionUID = 1L;

    public ProjectAlreadyExistsException(String msg) {
        super(msg);
    }
}
