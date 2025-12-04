package com.tablero.model;

public class TaskBoard {
    private int id;
    private String name;

    public TaskBoard(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public TaskBoard(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "TaskBoard [id=" + id + ", name=" + name + "]";
    }

}
