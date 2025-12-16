package com.MEnU.service;

import com.MEnU.dto.response.ChatUserResponse;
import com.MEnU.dto.response.MessageResponse;
import com.MEnU.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ChatService {
    void sendChat(User from, User to, String content);
    List<ChatUserResponse> getAllUserByMessage();
    boolean setSeen(Long otherUserId);
    Page<MessageResponse> getMessages(Long otherUserId, int page, int size);
}
