package com.MEnU.service.Impl;

import com.MEnU.dto.request.ChangePasswordRequest;
import com.MEnU.dto.request.FeedbackRequest;
import com.MEnU.dto.request.UpdateProfileRequest;
import com.MEnU.dto.response.*;
import com.MEnU.entity.Friend;
import com.MEnU.entity.User;
import com.MEnU.exception.BadRequestException;
import com.MEnU.exception.NotFoundException;
import com.MEnU.exception.ResourceConflictException;
import com.MEnU.repository.FriendRepository;
import com.MEnU.repository.UserRepository;
import com.MEnU.repository.VerificationTokenRepository;
import com.MEnU.service.CloudinaryService;
import com.MEnU.service.MailService;
import com.MEnU.service.UserService;
import com.MEnU.utils.TokenUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;
    private final FriendRepository friendRepository;
    private final CloudinaryService cloudinaryService;
    @Value("${link}")
    private String baseLink;

    public UserServiceImpl(UserRepository userRepository, MailService mailService,
                           PasswordEncoder passwordEncoder,
                           VerificationTokenRepository verificationTokenRepository,
                           FriendRepository friendRepository, CloudinaryService cloudinaryService) {
        this.userRepository = userRepository;
        this.mailService = mailService;
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.friendRepository = friendRepository;
        this.cloudinaryService = cloudinaryService;
    }

//    @Override
//    public UpdateProfileRespone updateProfile(UpdateProfileRequest req) {
//
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new NotFoundException("User not found"));
//
//        // 1. Update username
//        if (req.getUsername() != null && !req.getUsername().equals(user.getUsername())) {
//
//            if (userRepository.existsByUsername(req.getUsername())) {
//                throw new ResourceConflictException("Username already exists");
//            }
//
//            user.setUsername(req.getUsername());
//        }
//
//        // 2. Update displayName
//        if (req.getDisplayName() != null) {
//            user.setDisplayName(req.getDisplayName());
//        }
//
//        // 3. Update avatarURL
//        if (req.getAvatarURL() != null) {
//            user.setAvatarURL(req.getAvatarURL());
//        }
//
//        // 4. Update email
//        if (req.getEmail() != null && !req.getEmail().equals(user.getEmail())) {
//
//            if (userRepository.existsByEmail(req.getEmail())) {
//                throw new ResourceConflictException("Email already exists");
//            }
//
//            // Xóa token cũ của user
//            verificationTokenRepository.deleteAllByUser(user);
//
//            // Set email mới và đánh dấu chưa verify
//            user.setEmail(req.getEmail());
//            user.setEmailVerified(false);
//
//            // Tạo token mới
//            var token = TokenUtil.generateVerificationToken(user);
//            verificationTokenRepository.save(token);
//
//            // Gửi mail verify
//            String link = "http://localhost:8080/api/auth/verify?token=" + token.getToken();
//            String html = mailService.buildVerifyEmail(user, link);
//
//            mailService.sendMail(
//                    user.getEmail(),
//                    "Verify your new email",
//                    html,
//                    null
//            );
//        }
//
//        // 5. Save user
//        userRepository.save(user);
//
//        // 6. Tạo response
//        UpdateProfileRespone res = new UpdateProfileRespone();
//        res.setUsername(user.getUsername());
//        res.setDisplayName(user.getDisplayName());
//        res.setEmail(user.getEmail());
//        res.setAvatarURL(user.getAvatarURL());
//
//        return res;
//    }

    @Override
    public UpdateProfileRespone updateProfile(UpdateProfileRequest req, MultipartFile avatar) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // 1. Update username
        if (req.getUsername() != null && !req.getUsername().equals(user.getUsername())) {

            if (userRepository.existsByUsername(req.getUsername())) {
                throw new ResourceConflictException("Username already exists");
            }

            user.setUsername(req.getUsername());
        }

        // 2. Update displayName
        if (req.getDisplayName() != null) {
            user.setDisplayName(req.getDisplayName());
        }


        // 3. Upload avatar nếu có
        if (avatar != null && !avatar.isEmpty()) {
            String avatarUrl = cloudinaryService.upload(avatar);
            user.setAvatarURL(avatarUrl);
        }

        // 4. Update email
        if (req.getEmail() != null && !req.getEmail().equals(user.getEmail())) {

            if (userRepository.existsByEmail(req.getEmail())) {
                throw new ResourceConflictException("Email already exists");
            }

            // Xóa token cũ của user
            verificationTokenRepository.deleteAllByUser(user);

            // Set email mới và đánh dấu chưa verify
            user.setEmail(req.getEmail());
            user.setEmailVerified(false);

            // Tạo token mới
            var token = TokenUtil.generateVerificationToken(user);
            verificationTokenRepository.save(token);

            // Gửi mail verify
            String link = baseLink+ "/api/auth/verify?token=" + token.getToken();
            String html = mailService.buildVerifyEmail(user, link);

            mailService.sendMail(
                    user.getEmail(),
                    "Verify your new email",
                    html,
                    null
            );
        }

        // 5. Save user
        userRepository.save(user);

        // 6. Tạo response
        UpdateProfileRespone res = new UpdateProfileRespone();
        res.setUsername(user.getUsername());
        res.setDisplayName(user.getDisplayName());
        res.setEmail(user.getEmail());
        res.setAvatarURL(user.getAvatarURL());

        return res;






    }

    @Override
    public ChangePasswordResponse changePassword(ChangePasswordRequest req) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        String newPassword = req.getNewPassword();
        String confirmNewPassword = req.getConfirmNewPassword();
        String oldPassword = req.getOldPassword();

        //newpassword và newconfirmpassword ko trùng nhau
        if (!newPassword.equals(confirmNewPassword)) {
            throw new BadRequestException("Confirm password does not match");
        }
        //password cũ mình nhập vào ko trùng với password trong database
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadRequestException("Old password is incorrect");
        }
        //password mới lại trùng với password cũ nên ko cho đổi
        if (newPassword.equals(oldPassword)) {
            throw new BadRequestException("New password must be different from old password");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return new ChangePasswordResponse(user.getUsername());
    }

    @Override
    public UserProfileResponse getUserProfile() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        UserProfileResponse res = new UserProfileResponse();
        res.setUsername(user.getUsername());
        res.setDisplayName(user.getDisplayName());
        res.setEmail(user.getEmail());
        res.setAvatarURL(user.getAvatarURL());

        return res;
    }

    @Override
    public void sendFeedback(FeedbackRequest req) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        String subject = "[User Feedback] From " + user.getUsername();

        String content = """
            <h3>Feedback from user</h3>
            <p><b>Username:</b> %s</p>
            <p><b>Email:</b> %s</p>
            <p><b>Message:</b></p>
            <p>%s</p>
            """.formatted(
                user.getUsername(),
                user.getEmail(),
                req.getMessage()
        );

        // mail cố định
        String adminMail = "foxgcute@gmail.com";

        mailService.sendMail(
                adminMail,
                subject,
                content,
                null  // nếu bạn muốn gửi file thì tui thêm multipart
        );
    }

    @Override
    public void sendFeedback2(String message, MultipartFile image) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        String subject = "[User Feedback] From " + user.getUsername();

        String html = """
            <h3>Feedback from user</h3>
            <p><b>Username:</b> %s</p>
            <p><b>Email:</b> %s</p>
            <p><b>Message:</b></p>
            <p>%s</p>
            """.formatted(
                user.getUsername(),
                user.getEmail(),
                message
        );

        // feedback gửi đến email cố định
        String adminEmail = "foxgcute@gmail.com";

        MultipartFile[] files = new MultipartFile[]{image};

        mailService.sendMail(
                adminEmail,
                subject,
                html,
                files   // file đính kèm
        );
    }

    @Override
    public List<FriendResponse> getFriends() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<Friend> friends = friendRepository.findAllFriendsOfUser(user.getId());

        return friends.stream()
                .map(f -> {
                    User friend;

                    if (f.getUser().getId().equals(user.getId())) {
                        friend = f.getFriendUser();
                    } else {
                        friend = f.getUser();
                    }

                    FriendResponse res = new FriendResponse();
                    res.setId(friend.getId());
                    res.setUsername(friend.getUsername());
                    res.setDisplayName(friend.getDisplayName());
                    res.setAvatarURL(friend.getAvatarURL());

                    return res;
                })
                .toList();
    }

    @Override
    public List<SearchUserResponse> searchUsers(String keyword) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return userRepository.findByUsernameContainingIgnoreCase(keyword)
                .stream()
                .filter(u -> !u.getId().equals(currentUser.getId()))// CHẶN CHÍNH MÌNH
                .map(u -> new SearchUserResponse(
                        u.getId(),
                        u.getDisplayName(),
                        u.getAvatarURL()
                ))
                .toList();
    }

    @Override
    public UserProfileResponse getUserProfile(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("User not found"));

        UserProfileResponse res = new UserProfileResponse();
        res.setUsername(user.getUsername());
        res.setEmail(user.getEmail());
        res.setDisplayName(user.getDisplayName());
        res.setAvatarURL(user.getAvatarURL());

        return res;
    }
}
