package com.example.MEnU.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {
  @NotBlank(message = "OldPassword must not be blank")
  private String oldPassword;

  @NotBlank(message = "NewPassword must not be blank")
  private String newPassword;

  @NotBlank(message = "ConfirmNewPassword must not be blank")
  private String confirmNewPassword;
}
