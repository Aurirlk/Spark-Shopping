package com.shopping.controller;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/ws/notify")
public class WebSocketController {

    private static final ConcurrentHashMap<Long, Session> SESSIONS = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Long userId) {
        if (userId != null) SESSIONS.put(userId, session);
    }

    @OnClose
    public void onClose(@PathParam("userId") Long userId) {
        if (userId != null) SESSIONS.remove(userId);
    }

    public static void sendToUser(Long userId, String message) {
        Session session = SESSIONS.get(userId);
        if (session != null && session.isOpen()) {
            try { session.getBasicRemote().sendText(message); }
            catch (IOException e) { /* ignore */ }
        }
    }
}
