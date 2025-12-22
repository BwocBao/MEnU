package com.example.MEnU.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PhotoResponse {
    private String photoId;
    private LocalDateTime createdAt;
    private Long ownerId;
    private String ownerDisplayName;
    private String ownerAvatarURL;
    private String caption;
    private String photoURL;
}
