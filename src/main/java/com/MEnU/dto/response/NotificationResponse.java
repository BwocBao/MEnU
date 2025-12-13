package com.MEnU.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationResponse {

    private Long id;

    private String type;   // reaction | friend | comment

    private String message;

    private String fromUsername;

    private String toUsername;

    private Long photoId;   // null nếu không liên quan ảnh

    private Boolean seen;

    private LocalDateTime createdAt;
}