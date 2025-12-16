package com.MEnU.service;

import com.MEnU.websocket.WebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RealtimeService {

    private final WebSocketHandler webSocketHandler;

    // GỬI 1 USER
    public void sendToAUser(String username, Object payload) {
        webSocketHandler.sendToUser(username, payload);
    }

    // GỬI NHIỀU USER
    public void sendToUsers(List<String> usernames, Object payload) {
        for (String username : usernames) {
            webSocketHandler.sendToUser(username, payload);
        }
    }
}



