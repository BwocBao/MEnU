package com.example.MEnU.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRespone {
  private String username;

  private String displayName;

  private String email;

  private String avatarURL;
}
