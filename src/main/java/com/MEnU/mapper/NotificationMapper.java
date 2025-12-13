package com.MEnU.mapper;

import com.MEnU.dto.response.NotificationResponse;
import com.MEnU.entity.Notification;

public class NotificationMapper {
    public static NotificationResponse toResponse(Notification n) {

        NotificationResponse res = new NotificationResponse();

        res.setId(n.getId());
        res.setMessage(n.getMessage());
        res.setPhotoId(n.getPhoto() != null ? n.getPhoto().getId() : null);
        res.setSeen(n.getSeen());
        res.setCreatedAt(n.getCreatedAt());

        String from = n.getFromUser() != null
                ? n.getFromUser().getUsername()
                : null;

        res.setFromUsername(from);

        return res;
    }
}
