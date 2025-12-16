package com.MEnU.controller;



import com.MEnU.dto.ApiResponse;
import com.MEnU.dto.request.*;
import com.MEnU.dto.response.LoginResponse;
import com.MEnU.dto.response.RefreshTokenResponse;
import com.MEnU.dto.response.RegisterResponse;
import com.MEnU.exception.BadRequestException;
import com.MEnU.exception.ResourceConflictException;
import com.MEnU.exception.UnauthorizedException;
import com.MEnU.service.AuthService;
import com.MEnU.service.RefreshTokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    public AuthController(AuthService authService, RefreshTokenService refreshTokenService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
    }
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.success("Login successful",loginResponse));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
            @Valid @RequestBody RegisterRequest registerRequest) {
        RegisterResponse res = authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Registration successful", res));
    }
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<RefreshTokenResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest req) {
        return ResponseEntity.ok(ApiResponse.success("Generated access token succesfully",
                authService.refreshToken(req.getRefreshToken())));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest req)
    {
        authService.sendResetPasswordLink(req.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Forgot password link has been sent",null));
    }


    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest req) {
        authService.resetPassword(req.getToken(), req.getNewPassword(), req.getConfirmPassword());
        return ResponseEntity.ok(ApiResponse.success("Password updated successfully"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@Valid @RequestBody LogoutRequest request) {

        refreshTokenService.revokeToken(request.getRefreshToken());

        return ResponseEntity.ok(ApiResponse.success("Logout successful"));
    }

    @GetMapping("/reset-redirect")
    public ResponseEntity<String> redirect(@RequestParam String token) {
        String schemeUrl = "menuapp://reset-password?token=" + token;

        String html = """
        <html>
        <body>
            <script>
                window.location.href = '%s';
            </script>
            <p>Đang mở ứng dụng... Nếu không được, hãy mở thủ công: %s</p>
        </body>
        </html>
        """.formatted(schemeUrl, schemeUrl);

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(html);
    }
}
