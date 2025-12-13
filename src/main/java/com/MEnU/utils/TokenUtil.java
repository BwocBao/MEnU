package com.MEnU.utils;



import com.MEnU.entity.User;
import com.MEnU.entity.VerificationToken;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

public class TokenUtil {
    private static final SecureRandom random = new SecureRandom();

    public static String generateSecureToken() {
        byte[] bytes = new byte[48]; // 64 ký tự Base64
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public static VerificationToken generateVerificationToken(User user) {
        String rawToken = UUID.randomUUID().toString();
        VerificationToken token = new VerificationToken();
        token.setToken(rawToken);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(30));
        token.setUser(user);
        return token;
    }
}
