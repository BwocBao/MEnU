package com.example.MEnU.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

  private Long id;

  private String type; // reaction | friend | comment

  private String message;

  private String fromUsername;

  private String toUsername;

  private Long photoId; // null nếu không liên quan ảnh

  private Boolean seen;

  private LocalDateTime createdAt;
}
