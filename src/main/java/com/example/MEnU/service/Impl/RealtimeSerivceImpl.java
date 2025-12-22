package com.example.MEnU.service.Impl;

import com.example.MEnU.service.ReactionService;
import com.example.MEnU.service.RealtimeService;
import com.example.MEnU.websocket.WebSocketHandler;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RealtimeSerivceImpl implements RealtimeService {
    private final WebSocketHandler webSocketHandler;

    public RealtimeSerivceImpl(WebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    // GỬI 1 USER
    @Override
    public void sendToAUser(String username, Object payload) {
        webSocketHandler.sendToUser(username, payload);
    }

    // GỬI NHIỀU USER
    @Override
    public void sendToUsers(List<String> usernames, Object payload) {
        for (String username : usernames) {
            webSocketHandler.sendToUser(username, payload);
        }
    }
}
