package com.MEnU.service.Impl;

import com.MEnU.dto.request.LoginRequest;
import com.MEnU.dto.request.RegisterRequest;
import com.MEnU.dto.response.LoginResponse;
import com.MEnU.dto.response.RefreshTokenResponse;
import com.MEnU.dto.response.RegisterResponse;
import com.MEnU.entity.PasswordResetToken;
import com.MEnU.entity.RefreshToken;
import com.MEnU.entity.User;
import com.MEnU.entity.VerificationToken;
import com.MEnU.entity.enums.TokenType;
import com.MEnU.exception.*;
import com.MEnU.repository.PasswordResetTokenRepository;
import com.MEnU.repository.RefreshTokenRepository;
import com.MEnU.repository.UserRepository;
import com.MEnU.repository.VerificationTokenRepository;
import com.MEnU.service.AuthService;
import com.MEnU.service.JwtService;
import com.MEnU.service.MailService;
import com.MEnU.utils.DateUtil;
import com.MEnU.utils.TokenUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;


@Service
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final VerificationTokenRepository verificationTokenRepository;
    private final UserDetailsService userDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Value("${link}")
    private String baseLink;

    @Autowired
    public AuthServiceImpl(
            AuthenticationManager authenticationManager, UserRepository userRepository, JwtService jwtService,
            PasswordEncoder passwordEncoder, MailService mailService, VerificationTokenRepository verificationTokenRepository, UserDetailsService userDetailsService, RefreshTokenRepository refreshTokenRepository, PasswordResetTokenRepository passwordResetTokenRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.verificationTokenRepository = verificationTokenRepository;
        this.userDetailsService = userDetailsService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    @Override
    @Transactional//Khi method chạy trong transaction đang mở, tất cả entity bạn lấy ra từ database
    // (bằng repository hoặc EntityManager) đều nằm trong Persistence Context — còn gọi là Hibernate session.
    //Hibernate tự động ghi (flush) tất cả thay đổi xuống database trước khi transaction commit.
    public RegisterResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ResourceConflictException("Email is already registered");
        }
        if (userRepository.existsByUsername(registerRequest.getUsername()))
        {
            throw new ResourceConflictException("Username is already registered");
        }
        if(!registerRequest.getPassword().equals(registerRequest.getConfirmPassword()))
        {
            throw new BadRequestException("Passwords do not match");
        }
        // 1. Create user
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setDisplayName(registerRequest.getDisplayName());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmailVerified(false);


        // 2. Save user first
        userRepository.save(user);

        // 3. Generate token
        VerificationToken token= TokenUtil.generateVerificationToken(user);

        // 4. Save token
        verificationTokenRepository.save(token);

        // 5. Gửi mail
        String link = baseLink+"/api/auth/verify?token=" + token.getToken();
        String html = mailService.buildVerifyEmail(user, link);
        mailService.sendMail(
                user.getEmail(),
                "Verify account",
                html,
                null
        );

        // 6. Trả response
        return new RegisterResponse(
                user.getId(),
                user.getDisplayName(),
                user.getEmail()
        );
    }

    @Override
    @Transactional
    public String verify(String tokenValue) {
        // 1. Lấy token từ database
        VerificationToken token = verificationTokenRepository
                .findByToken(tokenValue)
                .orElseThrow(() -> new BadRequestException("Invalid verification token"));

        // 2. Kiểm tra token hết hạn chưa
        if (token.isExpired()) {
            verificationTokenRepository.delete(token);
            throw new UnauthorizedException("Token has expired");
        }

        // 3. Lấy user tương ứng
        User user = token.getUser();

        if (user == null) {
            throw new BadRequestException("User not found for this token");
        }

        // 4. Nếu user đã kích hoạt rồi
        if (user.isEmailVerified()) {
            throw new ResourceConflictException("User is already verified");
        }

        // 5. Kích hoạt user
        user.setEmailVerified(true);
        userRepository.save(user);

        // 6. revoke token
        verificationTokenRepository.delete(token);

        return "Email verified successfully! You can now login.";
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(), loginRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new UnauthorizedException("Invalid email or password");// 401
        } catch (DisabledException e) {
            throw new ForbiddenException("Please verify your email before logging in");// 403
        } catch (LockedException e) {
            throw new ForbiddenException("Account locked");
        }

        User user = (User) authentication.getPrincipal();


        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        Date accessExpiredDate = jwtService.extractClaim(accessToken, Claims::getExpiration, TokenType.ACCESS);
        Date refreshExpiredDate= jwtService.extractClaim(refreshToken, Claims::getExpiration, TokenType.REFRESH);
        LocalDateTime accessExpiredLocal = DateUtil.dateToLocalDateTime(accessExpiredDate);
        LocalDateTime refreshExpiredLocal = DateUtil.dateToLocalDateTime(refreshExpiredDate);

        RefreshToken token = new RefreshToken(user, refreshToken, refreshExpiredLocal);

        refreshTokenRepository.save(token);
        return new LoginResponse(accessToken, refreshToken, accessExpiredLocal);
    }


    @Override
    @Transactional
    public RefreshTokenResponse refreshToken(String refreshToken) {
        // 1. Kiểm tra refresh token hợp lệ
        if (!jwtService.isRefreshTokenValid(refreshToken)) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        // 2. Kiểm tra refresh token có trong db ko
        RefreshToken storedToken = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new UnauthorizedException("Refresh token not found"));

        // 3. Nếu refreshtoken bị revoke thì ko cho refresh
        if (storedToken.isRevoked()) {
            throw new UnauthorizedException("Refresh token is already revoked");
        }
        // 4. Nếu refresh token hết hạn thì ko cho refresh
        LocalDateTime refreshExpiryLocal = storedToken.getExpiredAt();
        if (LocalDateTime.now().isAfter(refreshExpiryLocal)) {
            storedToken.setRevoked(true);
            refreshTokenRepository.save(storedToken);
            throw new UnauthorizedException("Refresh token expired");
        }
        // 5. Extract username từ refresh token
        String username = jwtService.extractUsernameFromRefresh(refreshToken);

        // 6. Load user
        UserDetails user = userDetailsService.loadUserByUsername(username);

        // 7. Tạo access token mới
        String newAccessToken = jwtService.generateAccessToken(user);

        // 8. Tính còn hạn bao nhiêu ngày của refresh token
        long daysLeft = ChronoUnit.DAYS.between(LocalDateTime.now(), refreshExpiryLocal);

        String newRefreshToken = refreshToken; // mặc định dùng token cũ

        // 9. Nếu refresh token sắp hết hạn → rotate (tạo token mới, revoke token cũ, lưu token mới)
        if (daysLeft < 1) {
            //tạo token mới
            newRefreshToken = jwtService.generateRefreshToken(user);
            Date refreshExpiredDate = jwtService.extractClaim(newRefreshToken, Claims::getExpiration, TokenType.REFRESH);
            LocalDateTime refreshExpiredLocal = DateUtil.dateToLocalDateTime(refreshExpiredDate);
            RefreshToken newTokenEntity = new RefreshToken(storedToken.getUser(),
                    newRefreshToken, refreshExpiredLocal);

            // revoke old refresh token
            storedToken.setRevoked(true);

            //lưu token cũ nếu ko sẽ ko thì sẽ revoke cho có
            refreshTokenRepository.save(storedToken);

            //lưu token mới
            refreshTokenRepository.save(newTokenEntity);
        }
        // 10. Lấy expiry access token
        Date accessExpiry = jwtService.extractClaim(newAccessToken, Claims::getExpiration, TokenType.ACCESS);

        LocalDateTime accessExpiryLocal = DateUtil.dateToLocalDateTime(accessExpiry);

        // 9. Trả response
        return new RefreshTokenResponse(
                newAccessToken,
                newRefreshToken,
                accessExpiryLocal
        );
    }

    @Override
    @Transactional
    public void sendResetPasswordLink(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Email ko tồn tại để gửi link reset password"));
        //xóa hết token cũ rồi mới tạo token mới để tránh hacker chặn request lấy token các token chưa sử dụng
        passwordResetTokenRepository.deleteAllByUserId(user.getId());

        String token = TokenUtil.generateSecureToken();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(10);


        PasswordResetToken resetToken = new PasswordResetToken(user, token, expiry);
        passwordResetTokenRepository.save(resetToken);


        String link = baseLink+"/api/auth/reset-redirect?token=" + token;
        String html = mailService.buildResetPasswordEmail(user, link);



        mailService.sendMail(user.getEmail(),
                "Reset your password",
                html ,null);
    }

    @Override
    public void resetPassword(String token, String newPassword, String confirmPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));


        if (resetToken.isUsed())
            throw new BadRequestException("Token already used");


        if (resetToken.getExpiredAt().isBefore(LocalDateTime.now()))
            throw new BadRequestException("Token expired");

        if(!newPassword.equals(confirmPassword)) {
            throw new BadRequestException("Passwords do not match");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);


        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }

    @Override
    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User not found"));
    }
}
