package com.MEnU.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private Long id;
    private String type; // "chat"
    private String content;
    private String fromUsername;
    private String toUsername;
    private Long photoId;
    private LocalDateTime createdAt;
    private Boolean seen;

}
