package com.tablero.dtos;

public class UpdateTaskRequest {
    
    private String description;
    private String state;

    public UpdateTaskRequest() {
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
