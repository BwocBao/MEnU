package com.MEnU.controller;

import com.MEnU.dto.ApiResponse;
import com.MEnU.service.ReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reactions")
@RequiredArgsConstructor
public class ReactionController {

    private final ReactionService reactionService;

    // POST /api/reactions/{photoId}
    @PostMapping("/{photoId}")
    public ResponseEntity<ApiResponse<String>> reactToPhoto(
            @PathVariable Long photoId,
            @RequestParam String emoji
    ) {

        reactionService.reactToPhoto(photoId, emoji);

        return ResponseEntity.ok(ApiResponse.success("Reaction added successfully"));
    }
}