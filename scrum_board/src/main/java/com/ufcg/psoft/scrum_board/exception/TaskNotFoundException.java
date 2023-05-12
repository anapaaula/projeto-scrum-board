package com.ufcg.psoft.scrum_board.exception;

public class TaskNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    public TaskNotFoundException(String msg) {
        super(msg);
    }
}