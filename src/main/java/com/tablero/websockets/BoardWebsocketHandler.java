package com.tablero.websockets;

import com.tablero.utils.JwtUtil;

import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsMessageContext;
import io.jsonwebtoken.JwtException;

public class BoardWebsocketHandler {
    public static void onConnect(WsConnectContext ctx) {
        String boardId = ctx.pathParam("boardId");
        String token = ctx.queryParam("token");

        if (token == null || token.isBlank()) {
            System.out.println("WS Auth Failed: missing token");
            ctx.closeSession(1008, "token requerido");
            return;
        }

        try {
            JwtUtil.validateToken(token);
            WebsocketManager.addSession(boardId, ctx);
            ctx.attribute("boardId", boardId); // importante para onClose
            System.out.println("WS Connect: User connected to Board " + boardId);

        } catch (JwtException e) {

            System.out.println("WS Auth Failed for token: " + e.getMessage());
            ctx.closeSession(1008, "token ivalido");
        }
    }

    public static void onClose(WsCloseContext ctx) {
        String boardId = ctx.attribute("boardId");
        WebsocketManager.removeSession(boardId, ctx);
    }

    public static void onMessage(WsMessageContext ctx) {
        System.out.println("Mensaje: " + ctx.message());
    }
}
