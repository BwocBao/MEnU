package com.example.MEnU.service;

import com.example.MEnU.dto.request.ChangePasswordRequest;
import com.example.MEnU.dto.request.FeedbackRequest;
import com.example.MEnU.dto.request.UpdateProfileRequest;
import com.example.MEnU.dto.response.*;
import com.example.MEnU.entity.User;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

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

  void deleteUser(Long userId, User currentUser);

  void deleteMyAccount(User currentUser);
}
