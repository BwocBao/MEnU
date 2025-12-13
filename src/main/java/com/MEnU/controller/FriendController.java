package com.MEnU.controller;

import com.MEnU.dto.ApiResponse;
import com.MEnU.dto.request.AddFriendRequest;
import com.MEnU.service.FriendService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/friends")
public class FriendController {

    private final FriendService friendService;

    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addFriend(@RequestBody AddFriendRequest request) {
        friendService.addFriend(request.getFriendId());
        return ResponseEntity.ok(ApiResponse.success("Friend request sent successfully"));
    }

    @PostMapping("/accept/{id}")
    public ResponseEntity<?> acceptFriend(@PathVariable Long id) {
        friendService.acceptFriend(id);
        return ResponseEntity.ok(ApiResponse.success("Friend request accepted"));
    }

    @PostMapping("/reject/{id}")
    public ResponseEntity<?> rejectFriend(@PathVariable Long id) {
        friendService.rejectFriend(id);
        return ResponseEntity.ok(ApiResponse.success("Friend request rejected"));
    }
}