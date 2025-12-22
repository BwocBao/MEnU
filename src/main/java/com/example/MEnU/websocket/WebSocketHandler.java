package com.example.MEnU.websocket;

import com.example.MEnU.dto.response.MessageResponse;
import com.example.MEnU.dto.response.NotificationResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

  private final ObjectMapper mapper;
  private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

  public WebSocketHandler(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  public void sendToUser(String username, Object payload) {
    WebSocketSession s = sessions.get(username);

    if (s != null && s.isOpen()) {
      try {
        String json = mapper.writeValueAsString(payload);
        s.sendMessage(new TextMessage(json));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) {
    String username = (String) session.getAttributes().get("username"); // từ HandshakeInterceptor
    if (username != null) {
      sessions.put(username, session);
      System.out.println("User connected: " + username + " At: " + LocalDateTime.now());
      //            new Thread(() -> {
      //                int i = 0;
      //                while (session.isOpen()) {
      //                    sendToUser(username, i++);
      //                    try {
      //                        Thread.sleep(1000);
      //                    } catch (InterruptedException e) {
      //                        break;
      //                    }
      //                }
      //            }).start();
    }
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
    sessions.values().removeIf(s -> s.getId().equals(session.getId()));
  }

  @Override
  protected void handleTextMessage(
      WebSocketSession session, TextMessage message) { // message là gói tin WebSocket
    try {
      //            Ví dụ client gửi:
      //            {
      //                "type": "chat",
      //                    "content": "hello",
      //                    "toUsername": "alice"
      //            }
      //            Thì:message.getPayload()
      //            là: "{\"type\":\"chat\",\"content\":\"hello\",\"toUsername\":\"alice\"}"
      //            Nó chỉ là STRING, chưa phải JSON object.
      //            ObjectMapper.readTree() làm gì?
      //            ObjectMapper mapper = new ObjectMapper();
      //            JsonNode json = mapper.readTree(String);
      //            Nó parse String JSON → JsonNode Không cần map vào class ngay Rất linh hoạt
      //            Sau khi parse xong, bạn có thể:
      //            json.get("type").asText();
      //            json.get("content").asText();
      //            json.get("toUsername").asText();
      JsonNode json = mapper.readTree(message.getPayload()); // message.getPayload() trả về String
      String type = json.path("type").asText(null);
      if (type == null) {
        System.out.println("Invalid message: missing type");
        return;
      }

      switch (type) {
        case "chat":
          {
            MessageResponse msg = mapper.treeToValue(json, MessageResponse.class);
            sendToUser(msg.getToUsername(), msg);
            System.out.println(json);
            break;
          }

        case "comment":
        case "reaction":
        case "friend":
          {
            NotificationResponse notif = mapper.treeToValue(json, NotificationResponse.class);
            sendToUser(notif.getToUsername(), notif);
            System.out.println(json);
            break;
          }

        default:
          System.out.println("Unknown realtime type: " + type);
      }
    } catch (Exception e) {
      // Không cho exception làm rớt socket
      e.printStackTrace();
    }
  }
}
