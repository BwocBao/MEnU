package com.MEnU.dto.response;

public class UserProfileResponse {
    private String username;
    private String displayName;
    private String email;
    private String avatarURL;

    public UserProfileResponse() {}

    public UserProfileResponse(String username, String email, String displayName, String avatarURL) {
        this.username = username;
        this.email = email;
        this.displayName = displayName;
        this.avatarURL = avatarURL;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }
}
