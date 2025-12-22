package com.example.MEnU.service;

import com.example.MEnU.entity.enums.TokenType;
import io.jsonwebtoken.Claims;
import java.util.function.Function;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
  String generateAccessToken(UserDetails userDetails);

  String generateRefreshToken(UserDetails userDetails);

  String extractUsernameFromAccess(String token);

  String extractUsernameFromRefresh(String token);

  boolean isAccessTokenValid(String token, UserDetails userDetails);

  boolean isRefreshTokenValid(String token);

  <T> T extractClaim(String token, Function<Claims, T> claimsResolver, TokenType tokenType);
}
