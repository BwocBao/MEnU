package com.MEnU.dto.response;

import lombok.Data;

@Data
public class AddFriendResponse {
    private Long id;
    private String username;
    private String displayName;
    private String avatarURL;
    private String status; // PENDING / ACCEPTED
}