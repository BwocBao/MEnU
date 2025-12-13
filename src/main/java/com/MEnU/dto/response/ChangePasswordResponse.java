package com.MEnU.dto.response;

public class ChangePasswordResponse {
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ChangePasswordResponse(String username) {
        this.username = username;
    }
}
