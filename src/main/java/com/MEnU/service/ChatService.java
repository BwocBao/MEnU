package com.MEnU.service;

import com.MEnU.dto.response.MessageResponse;
import com.MEnU.entity.Message;
import com.MEnU.entity.User;
import com.MEnU.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final MessageRepository messageRepository;
    private final RealtimeService realtimeService;

    public void sendChat(User from, User to, String content) throws IOException {

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
        realtimeService.sendMessageToUser(to.getUsername(), dto);
    }
}
