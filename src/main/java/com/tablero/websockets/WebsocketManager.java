package com.tablero.websockets;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.tablero.model.Task;

import io.javalin.websocket.WsContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WebsocketManager {
    private static final ConcurrentHashMap<String, Set<WsContext>> boardSessions = new ConcurrentHashMap<>();
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void addSession(String boardId, WsContext context) {
        System.out.println("Adding session");
        boardSessions.computeIfAbsent(boardId, (k) -> {
            return ConcurrentHashMap.newKeySet();
        });

        boardSessions.get(boardId).add(context);
        System.out.println("User: " + context.sessionId() + " connected");
        System.out.println("After addition");
        System.out.println("Users conencted to the board with id" + boardId + " " + boardSessions.get(boardId).size());
    }

    public static void removeSession(String boardId, WsContext context) {

        Set<WsContext> activeSessions = boardSessions.get(boardId);
        if (activeSessions != null && !activeSessions.isEmpty()) {
            System.out.println("removing session if set is not null");
            boardSessions.get(boardId).remove(context);
        }

        System.out.println("User: " + context.sessionId() + " disconnected");
        System.out.println("After deletion");
        System.out.println("Users conencted to the board with id" + boardId + " " + boardSessions.get(boardId).size());

        if (activeSessions != null && activeSessions.isEmpty()) {
            System.out.println("The set is empty, gonna be deleted");
            boardSessions.remove(boardId);
        }
    }

    public static Set<WsContext> getSessions(String boardId) {
        System.out.println("Returning sessions set");
        if (boardSessions.get(boardId) != null) {
            return boardSessions.get(boardId);
        }
        return Collections.emptySet();
    }

    public static void broadcast(String boardId, Task task) {
        Set<WsContext> sessions = boardSessions.get(boardId);

        String serializedTask;
        try {
            serializedTask = mapper.writeValueAsString(task);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return;
        }
        if (sessions != null && !sessions.isEmpty()) {
            for (WsContext session : sessions) {
                try {
                    session.send(serializedTask);
                } catch (Exception e) {
                    System.out.println("Error sending to session " + session.sessionId());
                    e.printStackTrace();
                }
            }
        }
    }
}
