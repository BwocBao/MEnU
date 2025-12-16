package com.MEnU.controller;

import com.MEnU.dto.ApiResponse;
import com.MEnU.dto.request.CommentRequest;
import com.MEnU.dto.response.PhotoReactionResponse;
import com.MEnU.entity.Photo;
import com.MEnU.entity.User;
import com.MEnU.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.net.URLConnection;
import java.util.List;

@RestController
@RequestMapping("/api/photos")
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createPhoto(
            @RequestPart("caption") String caption,
            @RequestPart("image") MultipartFile image
    ) {

        Photo photo = photoService.createPhoto(caption, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Created Photo", photo));
    }

    @GetMapping("/{photoId}")
    public ResponseEntity<?> getPhoto(@PathVariable Long photoId) {
        Photo photo=photoService.getAPhoto(photoId);
        return ResponseEntity.ok().body(ApiResponse.success("Get Photo success",photo));
    }


    @GetMapping("/home")
    public ResponseEntity<?> getTopPhoto() {

        Photo photo = photoService.getTopPhoto();

        if (photo == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(photo);
    }

    @GetMapping("/move")
    public ResponseEntity<?> move(
            @RequestParam Long currentPhotoId,
            @RequestParam String direction,
            @AuthenticationPrincipal User currentUser
    ) {
//        @AuthenticationPrincipal User currentUser
//        tương đương
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        User currentUser = (User) auth.getPrincipal();
        Photo result = photoService.move(currentUser, currentPhotoId, direction);

        if (result == null) {
            return ResponseEntity.ok().body("No more photos");
        }

        return ResponseEntity.ok(ApiResponse.success("Moved Photo", result));
    }

    @DeleteMapping("/photos/{id}")
    public ResponseEntity<?> deletePhoto(@PathVariable Long id) {
        photoService.deletePhoto(id);
        return ResponseEntity.ok().body(ApiResponse.success("Deleted Photo"+id));
    }

    @GetMapping("reaction/{photoId}")
    public ResponseEntity<?> getReactionPhoto(@PathVariable Long photoId) {
            List<PhotoReactionResponse> photoReactionResponse= photoService.getReactionPhoto(photoId);
        return ResponseEntity.ok().body(ApiResponse.success("Get Reaction Photo"+photoId, photoReactionResponse));
    }

    @GetMapping("/{photoId}/download")
    public ResponseEntity<byte[]> downloadPhoto(
            @PathVariable Long photoId) throws Exception {

        Photo photo = photoService.getPhotoById(photoId);

        byte[] data = photoService.downloadPhoto(photoId);

        // đoán content-type dựa trên URL
        String contentType = URLConnection
                .guessContentTypeFromName(photo.getImageURL());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"photo_" + photoId + ".jpg\"")
                .contentType(MediaType.parseMediaType(
                        contentType != null ? contentType : "image/jpeg"
                ))
                .body(data);
    }

    @PostMapping("/{photoId}/comment")
    public ResponseEntity<?> comment(
            @PathVariable Long photoId,
            @RequestBody CommentRequest request
    ) {
        photoService.commentOnPhoto(photoId, request);
        return ResponseEntity.ok("Comment sent");
    }
}