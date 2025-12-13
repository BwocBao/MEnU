package com.MEnU.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ForgotPasswordRequest {
    @NotBlank(message = "Invalid email format")
    @Pattern(regexp = "^[\\w\\.-]+@[\\w\\.-]+\\.\\w{2,4}$", message = "Invalid email format")
    private String email;
    // getters/setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}