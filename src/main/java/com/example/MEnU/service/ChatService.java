package com.example.MEnU.service;

import com.example.MEnU.dto.response.ChatUserResponse;
import com.example.MEnU.dto.response.MessageResponse;
import com.example.MEnU.entity.User;
import java.util.List;
import org.springframework.data.domain.Page;

public interface ChatService {
  void sendChat(Long toID, String content);

  List<ChatUserResponse> getAllUserByMessage();

  boolean setSeen(Long otherUserId);

  Page<MessageResponse> getMessages(Long otherUserId, int page, int size);
}
