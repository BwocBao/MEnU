package com.MEnU.dto.request;

import jakarta.validation.constraints.NotBlank;

public class LogoutRequest {
    @NotBlank(message = "refeshToken is required")
    private String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
