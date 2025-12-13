package com.MEnU.service;

import com.MEnU.websocket.WebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RealtimeService {

    private final ObjectMapper mapper;

    // GỬI 1 USER
    public void sendNotificationToUser(String username, Object payload) {

        try {
            String json = mapper.writeValueAsString(payload);
            WebSocketHandler.sendToUser(username, json);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessageToUser(String username, Object payload) {

        try {
            String json = mapper.writeValueAsString(payload);
            WebSocketHandler.sendToUser(username, json);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // gửi cho nhiều user
    public void sendToUsers(List<String> usernames, Object payload) {

        try {
            String json = mapper.writeValueAsString(payload);

            for (String username : usernames) {
                WebSocketHandler.sendToUser(username, json);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
