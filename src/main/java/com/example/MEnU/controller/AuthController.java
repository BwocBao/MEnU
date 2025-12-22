package com.example.MEnU.controller;

import com.example.MEnU.dto.ApiResponse;
import com.example.MEnU.dto.request.*;
import com.example.MEnU.dto.response.LoginResponse;
import com.example.MEnU.dto.response.RefreshTokenResponse;
import com.example.MEnU.dto.response.RegisterResponse;
import com.example.MEnU.service.AuthService;
import com.example.MEnU.service.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth Service")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @Operation(summary = "User login")
  @PostMapping("/login")
  public ResponseEntity<ApiResponse<LoginResponse>> login(
      @Valid @RequestBody LoginRequest loginRequest) {
    LoginResponse loginResponse = authService.login(loginRequest);
    return ResponseEntity.ok(ApiResponse.success("Login successful", loginResponse));
  }

  @Operation(summary = "User register")
  @PostMapping("/register")
  public ResponseEntity<ApiResponse<RegisterResponse>> register(
      @Valid @RequestBody RegisterRequest registerRequest) {
    RegisterResponse res = authService.register(registerRequest);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success("Registration successful", res));
  }

  @Operation(
      summary = "new access token",
      description = "Api gửi refresh token để cấp new access token")
  @PostMapping("/refresh")
  public ResponseEntity<ApiResponse<RefreshTokenResponse>> refreshToken(
      @Valid @RequestBody RefreshTokenRequest req) {
    return ResponseEntity.ok(
        ApiResponse.success(
            "Generated access token succesfully", authService.refreshToken(req.getRefreshToken())));
  }

  @Operation(
      summary = "new forgot password token",
      description = "quên mật khẩu xong gửi về mail link chứa token")
  @PostMapping("/forgot-password")
  public ResponseEntity<ApiResponse<Void>> forgotPassword(
      @Valid @RequestBody ForgotPasswordRequest req) {
    authService.sendResetPasswordLink(req.getEmail());
    return ResponseEntity.ok(ApiResponse.success("Forgot password link has been sent"));
  }

  @Operation(
      summary = "chuyển hướng khi bấm link trong mail từ api forgot-password",
      description =
          "chuyển hướng sang trang web mới và chạy script mở win form cho nhập mật khẩu mới")
  @GetMapping("/reset-redirect")
  public ResponseEntity<String> redirect(@RequestParam String token) {
    String schemeUrl = "menuapp://reset-password?token=" + token;

    String html =
        """
        <html>
        <body>
            <script>
                window.location.href = '%s';
            </script>
            <p>Đang mở ứng dụng... Nếu không được, hãy mở thủ công: %s</p>
        </body>
        </html>
        """
            .formatted(schemeUrl, schemeUrl);

    return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
  }

  @Operation(
      summary = "winform sài",
      description = "nhập mật khẩu mới trong winform xong gọi api này")
  @PostMapping("/reset-password")
  public ResponseEntity<ApiResponse<Void>> resetPassword(
      @Valid @RequestBody ResetPasswordRequest req) {
    authService.resetPassword(req.getToken(), req.getNewPassword(), req.getConfirmPassword());
    return ResponseEntity.ok(ApiResponse.success("Password updated successfully"));
  }
}
