package com.example.MEnU.service.Impl;

import com.example.MEnU.dto.response.ChatUserResponse;
import com.example.MEnU.dto.response.MessageResponse;
import com.example.MEnU.entity.Message;
import com.example.MEnU.entity.User;
import com.example.MEnU.repository.MessageRepository;
import com.example.MEnU.repository.UserRepository;
import com.example.MEnU.service.AuthService;
import com.example.MEnU.service.ChatService;
import com.example.MEnU.service.RealtimeService;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChatServiceImpl implements ChatService {
  private final MessageRepository messageRepository;
  private final RealtimeService realtimeService;
  private final UserRepository userRepository;
  private final AuthService authService;

  public ChatServiceImpl(
          MessageRepository messageRepository,
          RealtimeService realtimeService,
          UserRepository userRepository, AuthService authService) {
    this.messageRepository = messageRepository;
    this.realtimeService = realtimeService;
    this.userRepository = userRepository;
      this.authService = authService;
  }

  public void sendChat(Long toID, String content) {
      User from=authService.getCurrentUser();
      User to=userRepository.findById(toID).orElse(null);

    // 1. Lưu database
    Message chat = new Message();
    chat.setSender(from);
    chat.setReceiver(to);
    chat.setText(content);

    messageRepository.save(chat);

    // 2. Convert DTO
    MessageResponse dto = new MessageResponse();
    dto.setId(chat.getId());
    dto.setFromUsername(from.getUsername());
    dto.setToUsername(to.getUsername());
    dto.setContent(content);
    dto.setCreatedAt(chat.getCreatedAt());
    dto.setType("chat");

    // 4. Realtime send
    realtimeService.sendToAUser(to.getUsername(), dto);
  }

  @Override
  public List<ChatUserResponse> getAllUserByMessage() {

    // User hiện tại
    User currentUser=authService.getCurrentUser();

    // Query DB
    List<Object[]> rows = messageRepository.findChatList(currentUser.getId());

    if(rows.isEmpty()){
        return null;
    }

    // Map sang DTO
    List<ChatUserResponse> result = new ArrayList<>();
      for (Object[] row : rows) {
          ChatUserResponse dto =
                  new ChatUserResponse(
                          ((Number) row[0]).longValue(),
                          (String) row[1],
                          (String) row[2],
                          (String) row[3],
                          row[4] != null
                                  ? ((Timestamp) row[4]).toLocalDateTime()
                                  : null, ((Number) row[5]).intValue() == 1
      );
          result.add(dto);
      }

    return result;
  }

  @Override
  @Transactional
  public boolean setSeen(Long otherUserId) {

    String username = SecurityContextHolder.getContext().getAuthentication().getName();

    User me =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

    int updated = messageRepository.markSeen(me.getId(), otherUserId);

    return updated > 0;
  }

  @Override
  public Page<MessageResponse> getMessages(Long otherUserId, int page, int size) {

    String username = SecurityContextHolder.getContext().getAuthentication().getName();

    User me =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

    Pageable pageable = PageRequest.of(page, size);

    Page<Message> messages = messageRepository.findConversation(me.getId(), otherUserId, pageable);

    return messages.map(
        m -> {
          MessageResponse dto = new MessageResponse();
          dto.setId(m.getId());
          dto.setFromUsername(m.getSender().getUsername());
          dto.setToUsername(m.getReceiver().getUsername());
          dto.setContent(m.getText());
          dto.setPhotoId(m.getPhotoId());
          dto.setCreatedAt(m.getCreatedAt());
          dto.setType("chat");
          dto.setSeen(m.getSeen());
          return dto;
        });
  }
}
