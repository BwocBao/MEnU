package com.example.MEnU.service;

import com.example.MEnU.dto.request.LoginRequest;
import com.example.MEnU.dto.request.RegisterRequest;
import com.example.MEnU.dto.response.LoginResponse;
import com.example.MEnU.dto.response.RefreshTokenResponse;
import com.example.MEnU.dto.response.RegisterResponse;
import com.example.MEnU.entity.User;

public interface AuthService {
  RegisterResponse register(RegisterRequest registerRequest);

  LoginResponse login(LoginRequest loginRequest);

  void verify(String token);

  RefreshTokenResponse refreshToken(String refreshToken);

  void sendResetPasswordLink(String email);

  void resetPassword(String token, String newPassword, String confirmPassword);

  User getCurrentUser();
}
