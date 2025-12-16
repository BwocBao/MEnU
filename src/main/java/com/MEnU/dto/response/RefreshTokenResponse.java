package com.MEnU.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenResponse {
    private String refreshToken;
    private String accessToken;
    private LocalDateTime accessTokenExpireTime;
}
