package com.example.MEnU.service;

import com.example.MEnU.websocket.WebSocketHandler;
import java.util.List;


public interface RealtimeService {

  // GỬI 1 USER
  void sendToAUser(String username, Object payload) ;

  // GỬI NHIỀU USER
  void sendToUsers(List<String> usernames, Object payload);

}

