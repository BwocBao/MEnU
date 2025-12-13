package com.MEnU.service;

import java.util.List;

public interface FriendService {
    void addFriend(Long friendId);
    void acceptFriend(Long id);
    void rejectFriend(Long id);
    List<Long> getVisibleUserIds(Long userId);
}
