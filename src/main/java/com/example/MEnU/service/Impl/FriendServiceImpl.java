package com.example.MEnU.service.Impl;

import com.example.MEnU.dto.response.FriendProfileResponse;
import com.example.MEnU.dto.response.FriendResponse;
import com.example.MEnU.entity.Friend;
import com.example.MEnU.entity.User;
import com.example.MEnU.entity.enums.FriendStatus;
import com.example.MEnU.exception.BadRequestException;
import com.example.MEnU.repository.FriendRepository;
import com.example.MEnU.repository.UserRepository;
import com.example.MEnU.service.AuthService;
import com.example.MEnU.service.FriendService;
import com.example.MEnU.service.NotificationService;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class FriendServiceImpl implements FriendService {

  private final NotificationService notificationService;

  private final UserRepository userRepository;

  private final FriendRepository friendRepository;

  private final AuthService authService;

  public FriendServiceImpl(
      NotificationService notificationService,
      UserRepository userRepository,
      FriendRepository friendRepository,
      AuthService authService) {
    this.notificationService = notificationService;
    this.userRepository = userRepository;
    this.friendRepository = friendRepository;
    this.authService = authService;
  }

  @Override
  public void addFriend(Long friendId) {

    User currentUser = authService.getCurrentUser();

    // Không được kết bạn với chính mình
    if (currentUser.getId().equals(friendId)) {
      throw new BadRequestException("You cannot send a friend request to yourself");
    }

    User friendUser =
        userRepository
            .findById(friendId)
            .orElseThrow(() -> new BadRequestException("Friend user not found"));

    // Check trạng thái đã tồn tại
    Optional<Friend> relation = friendRepository.findRelation(currentUser.getId(), friendId);

    if (relation.isPresent()) {
      throw new BadRequestException("Friend request already exists or you are already friends");
    }

    // Tạo request mới
    Friend friend = new Friend();
    friend.setUser(currentUser);
    friend.setFriendUser(friendUser);
    friend.setStatus(FriendStatus.pending);

    friendRepository.save(friend);

    notificationService.sendFriendRequestNotification(friendUser, friend, currentUser);
  }

  // ACCEPT FRIEND REQUEST
  @Override
  public void acceptFriend(Long requestId) {
    User currentUser = authService.getCurrentUser();

    // Tìm lời mời mà người hiện tại là người nhận
    Friend friend =
        friendRepository
            .findPendingRequestOfSenderId(requestId, currentUser.getId())
            .orElseThrow(() -> new BadRequestException("Friend request not found or not allowed"));

    if (friend.getStatus() != FriendStatus.pending) {
      throw new BadRequestException("Friend request already processed");
    }

    friend.setStatus(FriendStatus.accepted);
    friendRepository.save(friend);

    notificationService.sendAcceptFriendNotification(friend.getUser(), currentUser);
  }

  // REJECT FRIEND REQUEST
  @Override
  public void rejectFriend(Long requestId) {
    User currentUser = authService.getCurrentUser();

    // Tìm lời mời mà người hiện tại là người nhận
    Friend friend =
        friendRepository
            .findPendingRequestOfSenderId(requestId, currentUser.getId())
            .orElseThrow(() -> new BadRequestException("Friend request not found or not allowed"));

    if (friend.getStatus() != FriendStatus.pending) {
      throw new BadRequestException("Friend request already processed");
    }

    // Reject = xóa record luôn
    friendRepository.delete(friend);
  }

  @Override
  public List<Long> getVisibleUserIds(Long userId) {
    List<Long> friends = friendRepository.findAcceptedFriends(userId);
    friends.add(userId); // luôn thấy ảnh của chính mình
    return friends;
  }

  @Override
  public List<FriendResponse> getPendingFriends() {
    User currentUser = authService.getCurrentUser();
    List<Friend> friends = friendRepository.findPendingRequest(currentUser.getId());
    return friends.stream()
        .map(
            f -> {
              User sender = f.getUser(); // người gửi
              FriendResponse res = new FriendResponse();
              res.setId(sender.getId());
              res.setUsername(sender.getUsername());
              res.setDisplayName(sender.getDisplayName());
              res.setAvatarURL(sender.getAvatarURL());
              return res;
            })
        .toList();
  }

  @Override
  public FriendProfileResponse getFriendProfile(Long friendId) {
    User targetUser =
        userRepository
            .findById(friendId)
            .orElseThrow(() -> new BadRequestException("User not found"));

    User currentUser = authService.getCurrentUser();

    // 2. Check accepted
    Friend accepted =
        friendRepository.findAcceptRequestOfSearchUserId(friendId, currentUser.getId());
    if (accepted != null) {
      return new FriendProfileResponse(
          targetUser.getId(),
          targetUser.getUsername(),
          targetUser.getDisplayName(),
          targetUser.getEmail(),
          targetUser.getAvatarURL(),
          2);
    }

    // 3. Check pending
    Friend pending =
        friendRepository.findPendingRequestOfSearchUserId(friendId, currentUser.getId());
    if (pending != null) {

      boolean isCurrentUserReceiver = pending.getFriendUser().getId().equals(currentUser.getId());
      // status= 3: current user là người nhận → "chấp nhận"
      // status= 4: current user là người gửi -> "chờ phản hồi"
      int status = isCurrentUserReceiver ? 3 : 4;

      return new FriendProfileResponse(
          targetUser.getId(),
          targetUser.getUsername(),
          targetUser.getDisplayName(),
          targetUser.getEmail(),
          targetUser.getAvatarURL(),
          status);
    }

    // 4. If no record → chưa là bạn
    return new FriendProfileResponse(
        targetUser.getId(),
        targetUser.getUsername(),
        targetUser.getDisplayName(),
        targetUser.getEmail(),
        targetUser.getAvatarURL(),
        1);
  }

  @Override
  public void deleteFriend(Long friendId) {
    User currentUser = authService.getCurrentUser();
    friendRepository.deleteByFriendId(friendId, currentUser.getId());
  }
}
