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
    @GetMapping("/verify")
    public ResponseEntity<String> verify(@RequestParam String token) {
        try {
            authService.verify(token);
            return htmlResponse(successHtml());
        } catch (ResourceConflictException e) {
            return htmlResponse(alreadyVerifiedHtml());
        } catch (UnauthorizedException e) {
            return htmlResponse(expiredHtml());
        } catch (BadRequestException e) {
            return htmlResponse(invalidHtml());
        }
    }

    private ResponseEntity<String> htmlResponse(String html) {
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(html);
    }

    private String successHtml() {
        return """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Verification Success</title>
</head>
<body style="
    margin:0;
    height:100vh;
    display:flex;
    justify-content:center;
    align-items:center;
    background:linear-gradient(135deg, #e8f5e9, #c8e6c9);
    font-family: Arial, sans-serif;
">

    <div style="
        background:white;
        padding:40px 50px;
        border-radius:12px;
        text-align:center;
        box-shadow:0 10px 25px rgba(0,0,0,0.15);
        max-width:420px;
        width:100%;
    ">
        <div style="
            font-size:60px;
            color:#4caf50;
            margin-bottom:20px;
        ">
            ✔
        </div>

        <h2 style="
            margin:0 0 12px 0;
            color:#2e7d32;
        ">
            Verify account Successfully
        </h2>

        <p style="
            margin:0 0 25px 0;
            color:#555;
            font-size:15px;
            line-height:1.5;
        ">
            You can now return to the application and log in.
        </p>
    </div>

</body>
</html>

    """;
    }

    private String alreadyVerifiedHtml() {
        return """
    <html>
    <body style="display:flex;justify-content:center;align-items:center;text-align:center;font-family:Arial">
        <h2 style="color:blue">Account already verified</h2>
        <p>You can login directly.</p>
    </body>
    </html>
    """;
    }

    private String expiredHtml() {
        return """
    <html>
    <body style="text-align:center;font-family:Arial">
        <h2 style="color:orange">Verification link expired</h2>
        <p>Please request a new verification email.</p>
    </body>
    </html>
    """;
    }

    private String invalidHtml() {
        return """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Account Verified</title>
</head>
<body style="
    margin:0;
    height:100vh;
    display:flex;
    justify-content:center;
    align-items:center;
    background:linear-gradient(135deg, #e3f2fd, #bbdefb);
    font-family: Arial, sans-serif;
">

    <div style="
        background:white;
        padding:40px 50px;
        border-radius:12px;
        text-align:center;
        box-shadow:0 10px 25px rgba(0,0,0,0.15);
        max-width:400px;
        width:100%;
    ">
        <div style="
            font-size:60px;
            color:#4caf50;
            margin-bottom:20px;
        ">
            ✓
        </div>

        <h2 style="
            margin:0 0 10px 0;
            color:#1976d2;
        ">
            Account has already been verified
        </h2>

        <p style="
            margin:0 0 25px 0;
            color:#555;
            font-size:15px;
        ">
            You can now return to the application and log in.
        </p>

    </div>

</body>
</html>

    """;
    }

//    @GetMapping("/verify")
//    public ResponseEntity<?> verify(@RequestParam String token) {
//        String result = authService.verify(token);
//
//        if (result!=null) {
//            String html = """
//            <html>
//            <head>
//                <title>Verification Success</title>
//                <style>
//                    body {
//                        font-family: Arial;
//                        display: flex;
//                        justify-content: center;
//                        align-items: center;
//                        height: 100vh;
//                        background-color: #f0fff0;
//                    }
//                    .box {
//                        background: white;
//                        padding: 30px;
//                        border-radius: 10px;
//                        box-shadow: 0 0 10px rgba(0,0,0,0.1);
//                        text-align: center;
//                    }
//                    h2 { color: green; }
//                </style>
//            </head>
//            <body>
//                <div class='box'>
//                    <h2>Account verified successfully</h2>
//                    <p>You can now return to the application and login.</p>
//                </div>
//            </body>
//            </html>
//        """;
//
//            return ResponseEntity.ok()
//                    .header("Content-Type", "text/html")
//                    .body(html);
//        }

//        return ResponseEntity.badRequest().body("""
//       <html>
//       <body>
//       <h3>Token invalid or expired</h3>
//       </body>
//       </html>
//    """);
//    }


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
