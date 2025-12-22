package com.example.MEnU.controller;

import com.example.MEnU.dto.ApiResponse;
import com.example.MEnU.dto.request.CommentRequest;
import com.example.MEnU.dto.response.PhotoReactionResponse;
import com.example.MEnU.dto.response.PhotoResponse;
import com.example.MEnU.entity.Photo;
import com.example.MEnU.entity.User;
import com.example.MEnU.service.PhotoService;
import com.example.MEnU.service.ReactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URLConnection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Photo Service")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/photos")
@RequiredArgsConstructor
public class PhotoController {

  private final PhotoService photoService;
  private final ReactionService reactionService;

  @Operation(summary = "Api post ảnh")
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse<Photo>> createPhoto(
      @RequestPart("caption") String caption, @RequestPart("image") MultipartFile image) {

    Photo photo = photoService.createPhoto(caption, image);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success("Created Photo", photo));
  }

  @Operation(summary = "Api lấy 1 photo")
  @GetMapping("/{photoId}")
  public ResponseEntity<ApiResponse<String>> getPhoto(@PathVariable Long photoId) {
    return ResponseEntity.ok().body(ApiResponse.success("Get Photo success",photoService.getAPhoto(photoId)));
  }

  @Operation(summary = "Api khi bấm vào home sẽ load ra bức ảnh đầu tiên")
  @GetMapping("/home")
  public ResponseEntity<ApiResponse<PhotoResponse>> getTopPhoto() {

    PhotoResponse photo = photoService.getTopPhoto();

    if (photo == null) {
        return ResponseEntity.ok(
                ApiResponse.success("No photo available")
        );
    }

    return ResponseEntity.ok(ApiResponse.success("Get Top Photo success", photo));
  }

  @Operation(summary = "Api di chuyển lên xuống các post")
  @GetMapping("/move")
  public ResponseEntity<ApiResponse<PhotoResponse>> move(
      @RequestParam Long currentPhotoId,
      @RequestParam String direction,
      @AuthenticationPrincipal User currentUser) {
    //        @AuthenticationPrincipal User currentUser
    //        tương đương
    //        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    //        User currentUser = (User) auth.getPrincipal();
    PhotoResponse result = photoService.move(currentUser, currentPhotoId, direction);

    if (result == null) {
      return ResponseEntity.ok(ApiResponse.success("No more photos"));
    }

    return ResponseEntity.ok(ApiResponse.success("Moved Photo", result));
  }

  @Operation(summary = "Api delete photo")
  @DeleteMapping("/photos/{id}")
  public ResponseEntity<ApiResponse<Void>> deletePhoto(@PathVariable Long id) {
    photoService.deletePhoto(id);
    return ResponseEntity.ok().body(ApiResponse.success("Deleted Photo" + id));
  }

  @Operation(summary = "Api react photo")
  @GetMapping("reaction/{photoId}")
  public ResponseEntity<ApiResponse<List<PhotoReactionResponse>>> getReactionPhoto(
      @PathVariable Long photoId) {
    List<PhotoReactionResponse> photoReactionResponse = photoService.getReactionPhoto(photoId);
    return ResponseEntity.ok()
        .body(ApiResponse.success("Get Reaction Photo" + photoId, photoReactionResponse));
  }

  @Operation(summary = "Api download photo")
  @GetMapping("/{photoId}/download")
  public ResponseEntity<byte[]> downloadPhoto(@PathVariable Long photoId) throws Exception {

    Photo photo = photoService.getPhotoById(photoId);

    byte[] data = photoService.downloadPhoto(photoId);

    // đoán content-type dựa trên URL
    String contentType = URLConnection.guessContentTypeFromName(photo.getImageURL());

    return ResponseEntity.ok()
        .header(
            HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"photo_" + photoId + ".jpg\"")
        .contentType(MediaType.parseMediaType(contentType != null ? contentType : "image/jpeg"))
        .body(data);
  }

  @Operation(summary = "Api comment vào post")
  @PostMapping("/{photoId}/comment")
  public ResponseEntity<ApiResponse<Void>> comment(
      @PathVariable Long photoId, @RequestBody CommentRequest request) {
    photoService.commentOnPhoto(photoId, request);
    return ResponseEntity.ok(ApiResponse.success("Comment on Photo" + photoId));
  }

  @Operation(summary = "Api react vào post")
  @PostMapping("/reactions/{photoId}")
  public ResponseEntity<ApiResponse<Void>> reactToPhoto(
      @PathVariable Long photoId, @RequestParam String emoji) {

    reactionService.reactToPhoto(photoId, emoji);

    return ResponseEntity.ok(ApiResponse.success("Reaction added successfully"));
  }
}
