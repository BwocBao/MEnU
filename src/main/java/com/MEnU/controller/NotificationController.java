package com.MEnU.controller;

import com.MEnU.dto.ApiResponse;
import com.MEnU.dto.response.NotificationResponse;
import com.MEnU.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // Lấy tất cả thông báo của user hiện tại
    @GetMapping
    public ResponseEntity<?> getNotifications() {
        return ResponseEntity.ok(ApiResponse.success("All Notification",notificationService.getNotifications()));
    }

    // Đánh dấu 1 thông báo là đã xem
    @PutMapping("/{id}/seen")
    public ResponseEntity<?> markAsSeen(@PathVariable Long id) {
        notificationService.markAsSeen(id);
        return ResponseEntity.ok(ApiResponse.success("Marked Noti" + id));
    }

    // Đánh dấu tất cả thông báo là đã xem
    @PutMapping("/seen-all")
    public ResponseEntity<?> markAllAsSeen() {
        notificationService.markAllAsSeen();
        return ResponseEntity.ok(ApiResponse.success("Marked All Notifications"));
    }

    // Xóa 1 thông báo
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted Notification"+id));
    }

    // Xóa tất cả thông báo
    @DeleteMapping
    public ResponseEntity<?> deleteAllNotifications() {
        notificationService.deleteAllNotifications();
        return ResponseEntity.ok(ApiResponse.success("Deleted All Notifications"));
    }
}