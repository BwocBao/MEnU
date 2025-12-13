package com.MEnU.service.Impl;

import com.MEnU.dto.response.NotificationResponse;
import com.MEnU.entity.*;
import com.MEnU.exception.BadRequestException;
import com.MEnU.exception.NotFoundException;
import com.MEnU.mapper.NotificationMapper;
import com.MEnU.repository.*;
import com.MEnU.service.AuthService;
import com.MEnU.service.ReactionService;
import com.MEnU.service.RealtimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ReactionServiceImpl implements ReactionService {

    private final FriendRepository friendRepository;
    private final PhotoRepository photoRepository;
    private final ReactionRepository reactionRepository;
    private final NotificationRepository notificationRepository;
    private final AuthService authService;
    private final RealtimeService realtimeService;

    @Autowired
    public ReactionServiceImpl(
            FriendRepository friendRepository,
            PhotoRepository photoRepository,
            ReactionRepository reactionRepository,
            NotificationRepository notificationRepository,
            AuthService authService,
            RealtimeService realtimeService
    ) {
        this.friendRepository = friendRepository;
        this.photoRepository = photoRepository;
        this.reactionRepository = reactionRepository;
        this.notificationRepository = notificationRepository;
        this.authService = authService;
        this.realtimeService = realtimeService;
    }

    @Override
    public void reactToPhoto(Long photoId, String emoji) {

        User currentUser = authService.getCurrentUser();

        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new NotFoundException("Photo not found"));

        // 1. Lưu reaction
        Reaction reaction = new Reaction();
        reaction.setUser(currentUser);
        reaction.setPhoto(photo);
        reaction.setEmoji(emoji);
        reactionRepository.save(reaction);

        // 2. Notify chủ bài post
        if (!photo.getOwner().getId().equals(currentUser.getId())) {

            Notification noti = createNotification(
                    photo.getOwner(),
                    currentUser,   // fromUser
                    "reacted " + emoji + " to your post",
                    photo
            );

            // Convert entity → DTO
            NotificationResponse response = NotificationMapper.toResponse(noti);
            response.setType("reaction");

            // Gửi realtime
            realtimeService.sendNotificationToUser(
                    photo.getOwner().getUsername(),
                    response
            );
        }

        // 3. Notify bạn bè
        List<Friend> friends = friendRepository.findAllFriendsOfUser(currentUser.getId());

        for (Friend f : friends) {

            User friend = f.getUser().equals(currentUser)
                    ? f.getFriendUser()
                    : f.getUser();

            // Không gửi cho:
            // - chính mình
            // - chủ bài post (đã gửi ở trên)
            if (friend.getId().equals(currentUser.getId())
                    || friend.getId().equals(photo.getOwner().getId())) {
                continue;
            }

            Notification n = createNotification(
                    friend,
                    currentUser,
                    "reacted " + emoji + " to a post",
                    photo
            );

            NotificationResponse response = NotificationMapper.toResponse(n);
            response.setType("reaction");

            realtimeService.sendNotificationToUser(
                    friend.getUsername(),
                    response
            );
        }
    }


    //  trả về Notification để dùng push realtime luôn
    private Notification createNotification(User receiver,
                                            User sender,
                                            String message,
                                            Photo photo) {

        Notification n = new Notification();
        n.setUser(receiver);
        n.setFromUser(sender);
        n.setMessage(message);
        n.setPhoto(photo);
        n.setSeen(false);

        return notificationRepository.save(n);
    }

}
