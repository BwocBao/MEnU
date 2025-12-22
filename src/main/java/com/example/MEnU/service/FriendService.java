package com.example.MEnU.service;

import com.example.MEnU.dto.response.FriendProfileResponse;
import com.example.MEnU.dto.response.FriendResponse;
import java.util.List;

public interface FriendService {
  void addFriend(Long friendId);

  void acceptFriend(Long id);

  void rejectFriend(Long id);

  List<Long> getVisibleUserIds(Long userId);

  List<FriendResponse> getPendingFriends();

  FriendProfileResponse getFriendProfile(Long friendId);

  void deleteFriend(Long friendId);
}
