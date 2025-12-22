package com.example.MEnU.controller;

import com.example.MEnU.dto.ApiResponse;
import com.example.MEnU.dto.response.NotificationResponse;
import com.example.MEnU.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Notification Service")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

  private final NotificationService notificationService;

  // Lấy tất cả thông báo của user hiện tại
  @Operation(summary = "Api lấy tất cả thông báo của user hiện tại")
  @GetMapping
  public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotifications() {
    return ResponseEntity.ok(
        ApiResponse.success("All Notification", notificationService.getNotifications()));
  }

  // Đánh dấu 1 thông báo là đã xem
  @Operation(summary = "Api đánh dấu 1 thông báo là đã xem")
  @PutMapping("/{id}/seen")
  public ResponseEntity<ApiResponse<Void>> markAsSeen(@PathVariable Long id) {
    notificationService.markAsSeen(id);
    return ResponseEntity.ok(ApiResponse.success("Marked Noti" + id));
  }

  // Đánh dấu tất cả thông báo là đã xem
  @Operation(summary = "Api dấu tất cả thông báo là đã xem")
  @PutMapping("/seen-all")
  public ResponseEntity<ApiResponse<Void>> markAllAsSeen() {
    notificationService.markAllAsSeen();
    return ResponseEntity.ok(ApiResponse.success("Marked All Notifications"));
  }

  // Xóa 1 thông báo
  @Operation(summary = "Api xóa 1 thông báo")
  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteNotification(@PathVariable Long id) {
    notificationService.deleteNotification(id);
    return ResponseEntity.ok(ApiResponse.success("Deleted Notification" + id));
  }

  // Xóa tất cả thông báo
  @Operation(summary = "Api xóa tất cả thông báo")
  @DeleteMapping
  public ResponseEntity<ApiResponse<Void>> deleteAllNotifications() {
    notificationService.deleteAllNotifications();
    return ResponseEntity.ok(ApiResponse.success("Deleted All Notifications"));
  }
}
