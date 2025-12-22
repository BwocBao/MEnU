package com.example.MEnU.service;

import com.example.MEnU.entity.User;

public interface RefreshTokenService {
  void revokeToken(String refreshToken);

  void revokeAllTokensForUser(User user);
}
