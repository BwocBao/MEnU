package com.MEnU.service.Impl;

import com.MEnU.entity.enums.TokenType;
import com.MEnU.service.JwtService;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.io.Decoders;
import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {
    private final SecretKey accessKey;
    private final SecretKey refreshKey;

    private final long accessExpMinutes;
    private final long refreshExpDays;

    public JwtServiceImpl(
            @Value("${jwt.access-key}") String accessKeyBase64,
            @Value("${jwt.refresh-key}") String refreshKeyBase64,
            @Value("${jwt.access-exp-minutes:15}") long accessExpMinutes,
            @Value("${jwt.refresh-exp-days:30}") long refreshExpDays
    ) {
        this.accessKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessKeyBase64));
        this.refreshKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshKeyBase64));
        this.accessExpMinutes = accessExpMinutes;
        this.refreshExpDays = refreshExpDays;
    }

    @Override
    public String generateAccessToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, TokenType.ACCESS, true);
    }

    @Override
    public String generateRefreshToken(UserDetails userDetails) {
        // refresh thường không cần put roles nhưng vẫn có subject
        return buildToken(new HashMap<>(), userDetails, TokenType.REFRESH, false);
    }

    @Override
    public String extractUsernameFromAccess(String token) {
        return extractClaim(token, Claims::getSubject, TokenType.ACCESS);
    }

    @Override
    public String extractUsernameFromRefresh(String token) {
        return extractClaim(token, Claims::getSubject, TokenType.REFRESH);
    }
    /**
     * Trích claim với rõ loại token (đảm bảo dùng đúng key để verify chữ ký).
     */
    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver, TokenType tokenType) {
        final Claims claims = extractAllClaims(token, tokenType);
        return claimsResolver.apply(claims);//claims.getSubject()
    }


    // kiễm tra token access hợp lệ
    @Override
    public boolean isAccessTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsernameFromAccess(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token, TokenType.ACCESS);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // refresh token chỉ kiểm tra expiry + chữ ký (không cần đối chiếu username ở đây)
    @Override
    public boolean isRefreshTokenValid(String token) {
        try {
            return !isTokenExpired(token, TokenType.REFRESH);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // ---------------- Internal helpers ----------------

    /**
     * Xây token chung cho cả access và refresh. Chỉ định TokenType để chọn key + expiry.
     *
     * @param extraClaims   claims bổ sung
     * @param userDetails   user details để đặt subject
     * @param tokenType     ACCESS hoặc REFRESH
     * @param includeRoles  nếu true thêm "role" claim (thường cho access)
     * @return token JWS (compact)
     */
    private String buildToken(Map<String, Object> extraClaims,
                              UserDetails userDetails,
                              TokenType tokenType,
                              boolean includeRoles) {

        Instant now = Instant.now();
        Map<String, Object> claims = new HashMap<>(extraClaims);

        if (includeRoles) {
            String role = userDetails.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .orElse("ROLE_USER");
            claims.put("role", role);
        }

        Duration expiry = (tokenType == TokenType.ACCESS) ? Duration.ofMinutes(accessExpMinutes) : Duration.ofDays(refreshExpDays);
        SecretKey key = selectKey(tokenType);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(expiry)))
                .signWith(key)
                .compact();
    }


    /**
     * Lấy tất cả claims và đồng thời verify chữ ký bằng key tương ứng.
     * Sử dụng parserBuilder().setSigningKey(...).build().parseClaimsJws(token).getBody()
     * — đây là cách phổ biến để parse JWS claims (trường hợp token là signed JWS).
     */
    private Claims extractAllClaims(String token, TokenType tokenType) {
        SecretKey key = selectKey(tokenType);
        // parseClaimsJws sẽ throw nếu chữ ký không hợp lệ hoặc token bị sửa
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Kiểm tra token expired theo type (sử dụng claim expiration)
     */
    private boolean isTokenExpired(String token, TokenType tokenType) {
        Date expiration = extractClaim(token, Claims::getExpiration, tokenType);
        return expiration.before(Date.from(Instant.now()));
    }

    /**
     * Chọn key tương ứng với TokenType
     */
    private SecretKey selectKey(TokenType tokenType) {
        return (tokenType == TokenType.ACCESS) ? accessKey : refreshKey;
    }

}
