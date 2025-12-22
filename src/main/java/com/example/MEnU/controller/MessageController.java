package com.example.MEnU.controller;

import com.example.MEnU.dto.ApiResponse;
import com.example.MEnU.dto.request.MessageRequest;
import com.example.MEnU.dto.request.RefreshTokenRequest;
import com.example.MEnU.dto.response.ChatUserResponse;
import com.example.MEnU.dto.response.MessageResponse;
import com.example.MEnU.entity.User;
import com.example.MEnU.service.AuthService;
import com.example.MEnU.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Chat Service")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/message")
public class MessageController {
  private final ChatService chatService;
  private final AuthService authService;

  public MessageController(ChatService chatService, AuthService authService) {
    this.chatService = chatService;
      this.authService = authService;
  }

  @Operation(
      summary = "Api load lên list friend trong chat",
      description = "load lên theo thứ tự tin nhắn mới nhất đến cũ nhất")
  @GetMapping("/chats")
  public ResponseEntity<ApiResponse<List<ChatUserResponse>>> getChatList() {
    return ResponseEntity.ok(ApiResponse.success("Chat list", chatService.getAllUserByMessage()));
  }

  @Operation(summary = "Api đánh dấu tin nhắn đã xem")
  @PutMapping("/{userId}/seen")
  public ResponseEntity<ApiResponse<Boolean>> setSeen(@PathVariable Long userId) {
    boolean result = chatService.setSeen(userId);
    return ResponseEntity.ok(ApiResponse.success("Seen updated", result));
  }

  @Operation(summary = "Api lấy lên tin nhắn chat với user id ")
  @GetMapping("/{userId}/messages")
  public ResponseEntity<ApiResponse<Page<MessageResponse>>> getMessages(
      @PathVariable Long userId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    return ResponseEntity.ok(
        ApiResponse.success("Messages", chatService.getMessages(userId, page, size)));
  }

  @Operation(summary = "Api chat với friendId")
  @PostMapping("/chat/{friendId}")
    public ResponseEntity<ApiResponse<Void>> addFriend(@RequestBody MessageRequest req) {
      chatService.sendChat(req.getFriendId(), req.getMessage());
      return ResponseEntity.ok(ApiResponse.success("Send message successfully"));
  }
}
