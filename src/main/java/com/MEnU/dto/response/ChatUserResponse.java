package com.MEnU.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatUserResponse {
    private Long id;
    private String displayName;
    private String avatarURL;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private Boolean hasUnread;
}
