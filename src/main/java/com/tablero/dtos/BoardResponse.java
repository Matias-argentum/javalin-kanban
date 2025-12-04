package com.tablero.dtos;

public class BoardResponse {
    private int id;
    private String name;

    public BoardResponse(){};

    

    public BoardResponse(int id, String name) {
        this.id = id;
        this.name = name;
    }



    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setId(int id){
        this.id = id;
    }
}
