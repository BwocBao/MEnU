package com.example.MEnU.controller;

import com.example.MEnU.dto.ApiResponse;
import com.example.MEnU.dto.request.ChangePasswordRequest;
import com.example.MEnU.dto.request.FeedbackRequest;
import com.example.MEnU.dto.request.LogoutRequest;
import com.example.MEnU.dto.request.UpdateProfileRequest;
import com.example.MEnU.dto.response.*;
import com.example.MEnU.entity.User;
import com.example.MEnU.exception.BadRequestException;
import com.example.MEnU.service.RefreshTokenService;
import com.example.MEnU.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "User Service")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/user")
public class UserController {

  private final UserService userService;
  private final RefreshTokenService refreshTokenService;

  public UserController(UserService userService, RefreshTokenService refreshTokenService) {
    this.userService = userService;
    this.refreshTokenService = refreshTokenService;
  }

  //    @PutMapping("/update-profile")
  //    public ResponseEntity<?> updateProfile(@Valid @RequestBody UpdateProfileRequest request){
  //
  //        UpdateProfileRespone updatedUser = userService.updateProfile(request);
  //
  //        return ResponseEntity.ok(
  //                ApiResponse.success( "User profile updated successfully", updatedUser)
  //        );
  //    }

  @Operation(summary = "Api update profile trong setting")
  @PutMapping(
      path = "/update-profile",
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<ApiResponse<UpdateProfileRespone>> updateProfile(
      @RequestPart("profile") String profileJson,
      @RequestPart(value = "avatar", required = false) MultipartFile avatar) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      UpdateProfileRequest request =
          objectMapper.readValue(profileJson, UpdateProfileRequest.class);

      UpdateProfileRespone updatedUser = userService.updateProfile(request, avatar);
      return ResponseEntity.ok(
          ApiResponse.success("User profile updated successfully", updatedUser));

    } catch (JsonProcessingException e) {
      throw new BadRequestException("Invalid JSON format for profile!");

    } catch (IOException e) {
      throw new BadRequestException("Invalid file upload!");
    }
  }

  @Operation(summary = "Api đăng xuất")
  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody LogoutRequest request) {

    refreshTokenService.revokeToken(request.getRefreshToken());

    return ResponseEntity.ok(ApiResponse.success("Logout successful"));
  }

  @Operation(summary = "Api đổi mật khẩu trong cài đặt")
  @PostMapping("/change-password")
  public ResponseEntity<ApiResponse<ChangePasswordResponse>> changePassword(
      @RequestBody ChangePasswordRequest changePasswordRequest) {
    ChangePasswordResponse response = userService.changePassword(changePasswordRequest);
    return ResponseEntity.ok().body(ApiResponse.success("Change Password Successfully", response));
  }

  @Operation(summary = "Api xem profile của chính mình")
  @GetMapping("/profile")
  public ResponseEntity<ApiResponse<UserProfileResponse>> getUserProfile() {

    UserProfileResponse profile = userService.getUserProfile();

    return ResponseEntity.ok(ApiResponse.success("Get profile successfully", profile));
  }

  @Operation(summary = "Api gửi feedback cho admin")
  @PostMapping("/feedback")
  public ResponseEntity<ApiResponse<Void>> sendFeedback(@Valid @RequestBody FeedbackRequest req) {
    userService.sendFeedback(req);
    return ResponseEntity.ok(ApiResponse.success("Feedback sent successfully"));
  }

  @Hidden
  @PostMapping(value = "/feedback2", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> sendFeedback(
      @RequestPart("message") String message,
      @RequestPart(value = "image", required = false) MultipartFile image) {
    userService.sendFeedback2(message, image);
    return ResponseEntity.ok(ApiResponse.success("Feedback sent successfully"));
  }

  @Operation(summary = "Api lấy danh sách bạn bè")
  @GetMapping("/friends")
  public ResponseEntity<ApiResponse<List<FriendResponse>>> getFriendList() {
    List<FriendResponse> friends = userService.getFriends();
    return ResponseEntity.ok(ApiResponse.success("Friend list loaded", friends));
  }

  @Operation(summary = "Api tìm kiếm user")
  @GetMapping("/search")
  public ResponseEntity<ApiResponse<List<SearchUserResponse>>> searchUser(
      @RequestParam String keyword) {
    List<SearchUserResponse> users = userService.searchUsers(keyword);

    return ResponseEntity.ok(ApiResponse.success("Search user", users));
  }

  @Hidden
  @GetMapping("/profile/{userId}")
  public ResponseEntity<ApiResponse<UserProfileResponse>> getUserProfile(
      @PathVariable Long userId) {

    UserProfileResponse profile = userService.getUserProfile(userId);

    return ResponseEntity.ok(ApiResponse.success("User profile", profile));
  }

  // để gọi tới xem coi có đăng nhập chưa rồi chuyển qua home thôi
  @Operation(summary = "Api để gọi tới xem coi có đăng nhập chưa rồi chuyển qua home thôi")
  @GetMapping("/verify")
  public ResponseEntity<ApiResponse<Void>> verifyUser() {
    return ResponseEntity.ok().body(ApiResponse.success("Verify User"));
  }

  @Operation(summary = "delete account")
  @DeleteMapping("/me")
  public ResponseEntity<ApiResponse<Void>> deleteMyAccount(
      @AuthenticationPrincipal User currentUser) {
    userService.deleteMyAccount(currentUser);

    return ResponseEntity.ok(ApiResponse.success("Your account has been deleted"));
  }

  @Hidden
  @PreAuthorize("hasRole('ADMIN') and #userId == authentication.principal.id")
  @DeleteMapping("/{userId}")
  public ResponseEntity<?> deleteUser(
      @PathVariable Long userId, @AuthenticationPrincipal User currentUser) {
    userService.deleteUser(userId, currentUser);

    return ResponseEntity.ok(ApiResponse.success("User deleted successfully"));
  }
}
