package com.MEnU.service;

import com.MEnU.dto.request.ChangePasswordRequest;
import com.MEnU.dto.request.FeedbackRequest;
import com.MEnU.dto.request.UpdateProfileRequest;
import com.MEnU.dto.response.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
//    UpdateProfileRespone updateProfile(UpdateProfileRequest req);
    ChangePasswordResponse changePassword(ChangePasswordRequest req);
    UserProfileResponse getUserProfile();
    void sendFeedback(FeedbackRequest request);
    void sendFeedback2(String message, MultipartFile image);
    List<FriendResponse> getFriends();
    List<SearchUserResponse> searchUsers(String keyword);
    UserProfileResponse getUserProfile(Long id);
    UpdateProfileRespone updateProfile(UpdateProfileRequest req, MultipartFile avatar);
}
