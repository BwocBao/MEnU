package com.MEnU.dto.response;

import java.time.LocalDateTime;

public class RefreshTokenResponse {
    private String refreshToken;
    private String accessToken;
    private LocalDateTime accessTokenExpireTime;

    public RefreshTokenResponse(String accessToken, String refreshToken,  LocalDateTime expireTime) {
        this.refreshToken = refreshToken;
        this.accessTokenExpireTime = expireTime;
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public LocalDateTime getExpireTime() {
        return accessTokenExpireTime;
    }

    public void setExpireTime(LocalDateTime expireTime) {
        this.accessTokenExpireTime = expireTime;
    }
}
