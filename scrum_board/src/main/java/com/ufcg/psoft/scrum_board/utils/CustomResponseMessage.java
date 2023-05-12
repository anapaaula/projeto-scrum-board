package com.ufcg.psoft.scrum_board.utils;

public class CustomResponseMessage {
    private String message;

    private Object data;

    public CustomResponseMessage(String message){
        this.message = message;
    }

    public CustomResponseMessage(String message, Object data) {
        this.message = message;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        if (data != null) {
            return data;
        }
        return "No data provided";
    }
}
