package com.MEnU.service;

import com.MEnU.dto.request.LoginRequest;
import com.MEnU.dto.request.RegisterRequest;
import com.MEnU.dto.response.LoginResponse;
import com.MEnU.dto.response.RefreshTokenResponse;
import com.MEnU.dto.response.RegisterResponse;
import com.MEnU.entity.User;

public interface AuthService {
    RegisterResponse register(RegisterRequest registerRequest);
    LoginResponse login(LoginRequest loginRequest);
    String verify(String token);
    RefreshTokenResponse refreshToken(String refreshToken);
    void sendResetPasswordLink(String email);
    void resetPassword(String token,String newPassword,String confirmPassword);
    User getCurrentUser();
}
