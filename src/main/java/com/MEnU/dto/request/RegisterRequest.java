package com.MEnU.dto.request;


import jakarta.validation.constraints.*;

public class RegisterRequest {
    @NotBlank(message = "UserName is required")
    private String username;

    @NotBlank(message = "Display Name is requried")
    private String displayName;

    @NotBlank(message = "Invalid email format")
    @Pattern(regexp = "^[\\w\\.-]+@[\\w\\.-]+\\.\\w{2,4}$", message = "Invalid email format")
    private String email;
    @NotBlank(message = "Password must be at least 8 characters and contain both letters and numbers")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
            message = "Password must be at least 8 characters and contain both letters and numbers"
    )
    private String password;

    private String confirmPassword;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
