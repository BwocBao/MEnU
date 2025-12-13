package com.MEnU.service.Impl;

import com.MEnU.entity.RefreshToken;
import com.MEnU.entity.User;
import com.MEnU.exception.BadRequestException;
import com.MEnU.repository.RefreshTokenRepository;
import com.MEnU.service.RefreshTokenService;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public void revokeToken(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new BadRequestException("Refresh token not found"));

        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }

    @Override
    public void revokeAllTokensForUser(User user) {
        refreshTokenRepository.revokeAllByUserId(user.getId());
    }
}
