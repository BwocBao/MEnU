package com.example.MEnU.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendProfileResponse {
  private Long id;
  private String username;
  private String displayName;
  private String email;
  private String avatarURL;
  private Integer status; // 1:nofriend 2:friend 3:đồng ý 4: chờ phản hồi
}
