package com.MEnU.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchUserResponse {
    private Long id;
    private String displayName;
    private String avatarURL;
}