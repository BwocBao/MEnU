package com.MEnU.service.Impl;

import com.MEnU.entity.Friend;
import com.MEnU.entity.User;
import com.MEnU.entity.enums.FriendStatus;
import com.MEnU.exception.BadRequestException;
import com.MEnU.repository.FriendRepository;
import com.MEnU.repository.UserRepository;
import com.MEnU.service.AuthService;
import com.MEnU.service.FriendService;
import com.MEnU.service.NotificationService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FriendServiceImpl implements FriendService {

    private final NotificationService notificationService;

    private final UserRepository userRepository;

    private final FriendRepository friendRepository;

    private final AuthService authService;

    public FriendServiceImpl(NotificationService notificationService, UserRepository userRepository, FriendRepository friendRepository, AuthService authService) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
        this.friendRepository = friendRepository;
        this.authService = authService;
    }

    @Override
    public void addFriend(Long friendId) {

        // Lấy username từ Authentication
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User not found"));

        // Không được kết bạn với chính mình
        if (currentUser.getId().equals(friendId)) {
            throw new BadRequestException("You cannot send a friend request to yourself");
        }

        User friendUser = userRepository.findById(friendId)
                .orElseThrow(() -> new BadRequestException("Friend user not found"));

        // Check trạng thái đã tồn tại
        Optional<Friend> relation =
                friendRepository.findRelation(currentUser.getId(), friendId);

        if (relation.isPresent()) {
            throw new BadRequestException("Friend request already exists or you are already friends");
        }

        // Tạo request mới
        Friend friend = new Friend();
        friend.setUser(currentUser);
        friend.setFriendUser(friendUser);
        friend.setStatus(FriendStatus.pending);

        friendRepository.save(friend);

        notificationService.sendFriendRequestNotification(friendUser,friend, currentUser);

    }


    // ACCEPT FRIEND REQUEST
    @Override
    public void acceptFriend(Long requestId) {
        User currentUser = authService.getCurrentUser();

        // Tìm lời mời mà người hiện tại là người nhận
        Friend friend = friendRepository.findPendingRequest(requestId, currentUser.getId())
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
        Friend friend = friendRepository.findPendingRequest(requestId, currentUser.getId())
                .orElseThrow(() -> new BadRequestException("Friend request not found or not allowed"));

        if (friend.getStatus() != FriendStatus.pending) {
            throw new BadRequestException("Friend request already processed");
        }

        // Reject = xóa record luôn
        friendRepository.delete(friend);
    }

    public List<Long> getVisibleUserIds(Long userId) {
        List<Long> friends = friendRepository.findAcceptedFriends(userId);
        friends.add(userId); // luôn thấy ảnh của chính mình
        return friends;
    }
}