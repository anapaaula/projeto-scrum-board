package com.ufcg.psoft.scrum_board.exception;

public class ProjectNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    public ProjectNotFoundException(String msg) {
        super(msg);
    }
}
