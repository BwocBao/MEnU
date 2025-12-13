package com.MEnU.dto.response;

public class UpdateProfileRespone {
    private String username;

    private String displayName;

    private String email;

    private String avatarURL;

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

    public UpdateProfileRespone(String username, String displayName, String email, String avatarURL) {
        this.username = username;
        this.displayName = displayName;
        this.email = email;
        this.avatarURL = avatarURL;
    }

    public UpdateProfileRespone() {
    }
}
