package com.MEnU.dto.response;

public class SearchUserResponse {
    private Long id;
    private String displayName;
    private String avatarURL;

    public SearchUserResponse() {}

    public SearchUserResponse(Long id, String displayName, String avatarURL) {
        this.id = id;
        this.displayName = displayName;
        this.avatarURL = avatarURL;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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