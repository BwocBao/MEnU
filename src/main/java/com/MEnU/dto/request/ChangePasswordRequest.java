package com.MEnU.dto.request;

import jakarta.validation.constraints.NotBlank;

public class ChangePasswordRequest {
    @NotBlank(message = "OldPassword must not be blank")
    private String oldPassword;
    @NotBlank(message = "NewPassword must not be blank")
    private String newPassword;
    @NotBlank(message = "ConfirmNewPassword must not be blank")
    private String confirmNewPassword;

    public ChangePasswordRequest(String oldPassword, String confirmNewPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.confirmNewPassword = confirmNewPassword;
        this.newPassword = newPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }

    public void setConfirmNewPassword(String confirmNewPassword) {
        this.confirmNewPassword = confirmNewPassword;
    }
}