package com.tablero.model;

public class Task {
    private int id;
    private String description;
    private TaskState state;
    private int boardId;

    public Task(){

    }

    public Task(int id, String description, TaskState state, int boardId){
        this.id = id;
        this.description = description;
        this.state = state;
        this.boardId = boardId;
    }

    public Task(String description, TaskState state, int boardId){
        this.description = description;
        this.state = state;
        this.boardId = boardId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskState getState() {
        return state;
    }

    public void setState(TaskState state) {
        this.state = state;
    }

    public void setBoardId(int boardId){
        this.boardId = boardId;
    }

    public int getBoardId(){
        return boardId;
    }

    @Override
    public String toString() {
        return "Task [id=" + id + ", description=" + description + ", state=" + state + "]";
    }
}
