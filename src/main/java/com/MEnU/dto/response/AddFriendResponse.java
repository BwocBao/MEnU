package com.MEnU.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddFriendResponse {
    private Long id;
    private String username;
    private String displayName;
    private String avatarURL;
    private String status; // PENDING / ACCEPTED
}