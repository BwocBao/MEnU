package com.MEnU.service;

import com.MEnU.entity.User;

public interface RefreshTokenService {
    void revokeToken(String refreshToken);
    void revokeAllTokensForUser(User user);
}
