package com.MEnU.websocket;

import com.MEnU.dto.response.MessageResponse;
import com.MEnU.dto.response.NotificationResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private static final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String username = (String) session.getAttributes().get("username");
        if (username != null) {
            sessions.put(username, session);
            System.out.println("User connected: " + username);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.values().removeIf(s -> s.getId().equals(session.getId()));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        JsonNode json = mapper.readTree(message.getPayload());
        String type = json.get("type").asText();

        switch (type) {
            case "chat": {
                MessageResponse msg = mapper.treeToValue(json, MessageResponse.class);
                sendToUser(msg.getToUsername(), msg);
                break;
            }

            case "comment":
            case "reaction":
            case "friend": {
                NotificationResponse notif = mapper.treeToValue(json, NotificationResponse.class);
                sendToUser(notif.getToUsername(), notif);
                break;
            }

            default:
                System.out.println("Unknown realtime type: " + type);
        }
    }

    public static void sendToUser(String username, Object payload) throws IOException {

        WebSocketSession s = sessions.get(username);

        if (s != null && s.isOpen()) {
            s.sendMessage(new TextMessage(mapper.writeValueAsString(payload)));
        }
    }
}