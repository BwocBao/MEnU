package com.example.MEnU.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
