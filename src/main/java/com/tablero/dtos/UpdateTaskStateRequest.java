package com.tablero.dtos;

public class UpdateTaskStateRequest {
    private String state;
    

    public UpdateTaskStateRequest() {
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
