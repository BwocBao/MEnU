package com.example.MEnU.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendResponse {
  private Long id;
  private String username;
  private String displayName;
  private String avatarURL;
}
