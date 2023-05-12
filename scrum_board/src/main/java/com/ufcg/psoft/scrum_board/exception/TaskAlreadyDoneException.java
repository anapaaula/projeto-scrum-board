package com.ufcg.psoft.scrum_board.exception;

public class TaskAlreadyDoneException extends Exception {
    private static final long serialVersionUID = 1L;

    public TaskAlreadyDoneException(String msg) {
        super(msg);
    }
}