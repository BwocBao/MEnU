package com.MEnU.controller;

import com.MEnU.dto.ApiResponse;
import com.MEnU.dto.request.ChangePasswordRequest;
import com.MEnU.dto.request.FeedbackRequest;
import com.MEnU.dto.request.UpdateProfileRequest;
import com.MEnU.dto.response.*;
import com.MEnU.entity.User;
import com.MEnU.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

//    @PutMapping("/update-profile")
//    public ResponseEntity<?> updateProfile(@Valid @RequestBody UpdateProfileRequest request){
//
//        UpdateProfileRespone updatedUser = userService.updateProfile(request);
//
//        return ResponseEntity.ok(
//                ApiResponse.success( "User profile updated successfully", updatedUser)
//        );
//    }
@PutMapping(
        path = "/update-profile",
        consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }
)
public ResponseEntity<?> updateProfile(
        @RequestPart("profile") String profileJson,
        @RequestPart(value = "avatar", required = false) MultipartFile avatar
) throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    UpdateProfileRequest request = objectMapper.readValue(profileJson, UpdateProfileRequest.class);

    UpdateProfileRespone updatedUser = userService.updateProfile(request, avatar);
    return ResponseEntity.ok(
            ApiResponse.success("User profile updated successfully", updatedUser)
    );
}

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<ChangePasswordResponse>> changePassword(
            @RequestBody ChangePasswordRequest changePasswordRequest) {
        ChangePasswordResponse response = userService.changePassword(changePasswordRequest);
        return ResponseEntity.ok().body(ApiResponse.success(
                "Change Password Successfully",response));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile() {

        UserProfileResponse profile = userService.getUserProfile();

        return ResponseEntity.ok(
                ApiResponse.success("Get profile successfully", profile)
        );
    }

    @PostMapping("/feedback")
    public ResponseEntity<?> sendFeedback(@RequestBody FeedbackRequest req) {
        userService.sendFeedback(req);
        return ResponseEntity.ok(ApiResponse.success("Feedback sent successfully"));
    }


    @PostMapping(value = "/feedback2", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> sendFeedback(
            @RequestPart("message") String message,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        userService.sendFeedback2(message, image);
        return ResponseEntity.ok(ApiResponse.success("Feedback sent successfully"));
    }

    @GetMapping("/friends")
    public ResponseEntity<?> getFriendList() {
        List<FriendResponse> friends = userService.getFriends();
        return ResponseEntity.ok(
                ApiResponse.success("Friend list loaded", friends)
        );
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<SearchUserResponse>>> searchUser(
            @RequestParam String keyword) {

        List<SearchUserResponse> users = userService.searchUsers(keyword);

        return ResponseEntity.ok(ApiResponse.success("Search user", users));
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserProfile(
            @PathVariable Long userId) {

        UserProfileResponse profile = userService.getUserProfile(userId);

        return ResponseEntity.ok(ApiResponse.success("User profile", profile));
    }

    // để gọi tới xem coi có đăng nhập chưa rồi chuyển qua home thôi
    @GetMapping("/verify")
    public ResponseEntity<?> verifyUser() {
        return  ResponseEntity.ok().body(ApiResponse.success("Verify User"));
    }

    @DeleteMapping("/me")
    public ResponseEntity<?> deleteMyAccount(
            @AuthenticationPrincipal User currentUser
    ) {
        userService.deleteMyAccount(currentUser);

        return ResponseEntity.ok(
                ApiResponse.success("Your account has been deleted")
        );
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal User currentUser
    ) {
        userService.deleteUser(userId, currentUser);

        return ResponseEntity.ok(
                ApiResponse.success("User deleted successfully")
        );
    }
}
