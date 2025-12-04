package com.tablero.utils;

import com.tablero.model.TaskState;

public class StateMapper {
    public static TaskState stringToState(String stateString){
        return TaskState.valueOf(stateString);
    }

    public static String stateToString(TaskState state){
        return state.name();
    }
}
