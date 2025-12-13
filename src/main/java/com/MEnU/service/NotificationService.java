package com.MEnU.service;

import com.MEnU.dto.response.NotificationResponse;
import com.MEnU.entity.Friend;
import com.MEnU.entity.User;

import java.util.List;

public interface NotificationService {

    List<NotificationResponse> getNotifications();

    void markAsSeen(Long id);

    void markAllAsSeen();

    void deleteNotification(Long id);

    void deleteAllNotifications();

    void sendFriendRequestNotification(User receiver, Friend friendEntity, User sender);

    void sendAcceptFriendNotification(User receiver, User accepter);


}