package com.MEnU.controller;

import com.MEnU.dto.ApiResponse;
import com.MEnU.dto.response.ChatUserResponse;
import com.MEnU.dto.response.MessageResponse;
import com.MEnU.service.ChatService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/message")
public class MessageController {
    private final ChatService  chatService;

    public MessageController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/chats")
    public ResponseEntity<ApiResponse<List<ChatUserResponse>>> getChatList() {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Chat list",
                        chatService.getAllUserByMessage()
                )
        );
    }

    @PutMapping("/{userId}/seen")
    public ResponseEntity<ApiResponse<Boolean>> setSeen(
            @PathVariable Long userId
    ) {
        boolean result = chatService.setSeen(userId);
        return ResponseEntity.ok(
                ApiResponse.success("Seen updated", result)
        );
    }

    @GetMapping("/{userId}/messages")
    public ResponseEntity<ApiResponse<Page<MessageResponse>>> getMessages(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Messages",
                        chatService.getMessages(userId, page, size)
                )
        );
    }
}
