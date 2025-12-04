package com.tablero.dtos;

public class CreateTaskRequest {
    private String description;
    private String state;

    public CreateTaskRequest() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

}
