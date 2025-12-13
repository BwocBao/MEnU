package com.MEnU.dto.response;

public class FriendResponse {
    private Long id;
    private String username;
    private String displayName;
    private String avatarURL;

    public FriendResponse() {}

    public FriendResponse(Long id, String displayName, String username, String avatarURL) {
        this.id = id;
        this.displayName = displayName;
        this.username = username;
        this.avatarURL = avatarURL;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }
}