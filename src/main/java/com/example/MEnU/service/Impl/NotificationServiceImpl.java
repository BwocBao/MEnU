package com.example.MEnU.service.Impl;

import com.example.MEnU.dto.response.NotificationResponse;
import com.example.MEnU.entity.Friend;
import com.example.MEnU.entity.Notification;
import com.example.MEnU.entity.Photo;
import com.example.MEnU.entity.User;
import com.example.MEnU.mapper.NotificationMapper;
import com.example.MEnU.repository.FriendRepository;
import com.example.MEnU.repository.NotificationRepository;
import com.example.MEnU.repository.PhotoRepository;
import com.example.MEnU.repository.UserRepository;
import com.example.MEnU.service.AuthService;
import com.example.MEnU.service.NotificationService;
import com.example.MEnU.service.RealtimeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

  private final NotificationRepository notificationRepo;
  private final UserRepository userRepo;
  private final FriendRepository friendRepo;
  private final PhotoRepository photoRepo;
  private final RealtimeService realtimeService;
  private final AuthService authService;

  // Lấy danh sách thông báo
  @Override
  public List<NotificationResponse> getNotifications() {
    User currentUser = authService.getCurrentUser();

    List<Notification> list =
        notificationRepo.findByUserIdOrderByCreatedAtDesc(currentUser.getId());

    return list.stream().map(NotificationMapper::toResponse).toList();
  }

  // Đánh dấu đã xem 1 thông báo
  @Override
  public void markAsSeen(Long id) {
    User currentUser = authService.getCurrentUser();

    Notification noti =
        notificationRepo
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Notification not found"));

    if (!noti.getUser().getId().equals(currentUser.getId())) {
      throw new RuntimeException("Unauthorized");
    }

    noti.setSeen(true);
    notificationRepo.save(noti);
  }

  // Đánh dấu tất cả đã xem
  @Override
  public void markAllAsSeen() {
    User currentUser = authService.getCurrentUser();
    notificationRepo.markAllAsSeen(currentUser.getId());
  }

  // Xóa 1 thông báo
  @Override
  public void deleteNotification(Long id) {
    User currentUser = authService.getCurrentUser();

    Notification n =
        notificationRepo
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Notification not found"));

    if (!n.getUser().getId().equals(currentUser.getId())) {
      throw new RuntimeException("Unauthorized");
    }

    notificationRepo.delete(n);
  }

  // Xóa toàn bộ
  @Override
  public void deleteAllNotifications() {
    User currentUser = authService.getCurrentUser();
    notificationRepo.deleteByUserId(currentUser.getId());
  }

  // ===============  LOGIC TẠO THÔNG BÁO  ===============

  // Gửi thông báo khi có lời mời kết bạn
  @Override
  public void sendFriendRequestNotification(User receiver, Friend friendEntity, User sender) {

    Notification noti = createNotification(receiver, sender, "sent you a friend request", null);
    NotificationResponse response = NotificationMapper.toResponse(noti);
    response.setType("friend");
    realtimeService.sendToAUser(receiver.getUsername(), response);
  }

  // Khi accept lời mời kết bạn → gửi thông báo cho người gửi
  @Override
  public void sendAcceptFriendNotification(User receiver, User accepter) {

    Notification noti =
        createNotification(receiver, accepter, "accepted your friend request", null);
    NotificationResponse response = NotificationMapper.toResponse(noti);
    response.setType("friend");
    realtimeService.sendToAUser(receiver.getUsername(), noti);
  }

  // ================== HELPER METHODS ===================

  private Notification createNotification(User receiver, User sender, String message, Photo photo) {

    Notification n = new Notification();
    n.setUser(receiver);
    n.setFromUser(sender);
    n.setMessage(message);
    n.setPhoto(photo);
    n.setSeen(false);

    return notificationRepo.save(n);
  }
}
