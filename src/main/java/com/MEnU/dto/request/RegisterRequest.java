package com.MEnU.dto.request;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

}
