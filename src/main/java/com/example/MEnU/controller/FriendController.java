package com.example.MEnU.controller;

import com.example.MEnU.dto.ApiResponse;
import com.example.MEnU.dto.request.AddFriendRequest;
import com.example.MEnU.dto.response.FriendProfileResponse;
import com.example.MEnU.dto.response.FriendResponse;
import com.example.MEnU.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Friend Service")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/friends")
public class FriendController {

  private final FriendService friendService;

  public FriendController(FriendService friendService) {
    this.friendService = friendService;
  }

  @Operation(summary = "Api gửi lời mời kết bạn")
  @PostMapping("/add")
  public ResponseEntity<ApiResponse<Void>> addFriend(@RequestBody AddFriendRequest request) {
    friendService.addFriend(request.getFriendId());
    return ResponseEntity.ok(ApiResponse.success("Friend request sent successfully"));
  }

  @Operation(summary = "Api chấp nhận mời kết bạn")
  @PostMapping("/accept/{id}")
  public ResponseEntity<ApiResponse<Void>> acceptFriend(@PathVariable Long id) {
    friendService.acceptFriend(id);
    return ResponseEntity.ok(ApiResponse.success("Friend request accepted"));
  }

  @Operation(summary = "Api từ chối mời kết bạn")
  @PostMapping("/reject/{id}")
  public ResponseEntity<ApiResponse<Void>> rejectFriend(@PathVariable Long id) {
    friendService.rejectFriend(id);
    return ResponseEntity.ok(ApiResponse.success("Friend request rejected"));
  }

  @Operation(summary = "Api tìm user đang gửi kết bạn đến mình")
  @GetMapping("/pending")
  public ResponseEntity<ApiResponse<List<FriendResponse>>> getPendingFriends() {
    List<FriendResponse> list = friendService.getPendingFriends();
    return ResponseEntity.ok(ApiResponse.success("List of friends requested", list));
  }

  @Operation(summary = "Api trả về profile của user kèm status")
  @GetMapping("/profile/{friendId}")
  public ResponseEntity<ApiResponse<FriendProfileResponse>> getFriend(@PathVariable Long friendId) {
    FriendProfileResponse res = friendService.getFriendProfile(friendId);
    return ResponseEntity.ok(ApiResponse.success("Friend Profile: ", res));
  }

  @DeleteMapping("/{friendId}")
  public ResponseEntity<ApiResponse<FriendResponse>> deleteFriend(@PathVariable Long friendId) {
    friendService.deleteFriend(friendId);
    return ResponseEntity.ok(ApiResponse.success("Friend deleted successfully"));
  }
}
