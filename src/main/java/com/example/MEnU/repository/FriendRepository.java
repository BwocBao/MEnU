package com.example.MEnU.repository;

import com.example.MEnU.entity.Friend;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {

  @Query(
      """
        SELECT f FROM Friend f
        WHERE f.status = 'accepted'
        AND (f.user.id = :userId OR f.friendUser.id = :userId)
        AND f.user.deletedAt IS NULL
        AND f.friendUser.deletedAt IS NULL
    """)
  List<Friend> findAllFriendsOfUser(Long userId);

  // Kiểm tra 2 user bất kỳ đã tồn tại record hay chưa
  @Query(
      """
            SELECT f FROM Friend f
                WHERE (
                        (f.user.id = :u1 AND f.friendUser.id = :u2)
                     OR (f.user.id = :u2 AND f.friendUser.id = :u1)
                      )
                  AND f.user.deletedAt IS NULL
                  AND f.friendUser.deletedAt IS NULL
    """)
  Optional<Friend> findRelation(Long u1, Long u2);

  @Query(
      """
    SELECT f FROM Friend f
    WHERE f.user.id = :senderId
      AND f.friendUser.id = :currentUserId
      AND f.status = 'pending'
      AND f.user.deletedAt IS NULL
      AND f.friendUser.deletedAt IS NULL
""")
  Optional<Friend> findPendingRequestOfSenderId(Long senderId, Long currentUserId);

  @Query(
      """
    SELECT f FROM Friend f
    WHERE
      (
        (f.user.id = :searchUserId AND f.friendUser.id = :currentUserId)
        OR
        (f.user.id = :currentUserId AND f.friendUser.id = :searchUserId )
      )
      AND f.status = 'accepted'
      AND f.user.deletedAt IS NULL
      AND f.friendUser.deletedAt IS NULL
""")
  Friend findAcceptRequestOfSearchUserId(Long searchUserId, Long currentUserId);

  @Query(
      """
    SELECT f FROM Friend f
    WHERE
      (
        (f.user.id = :searchUserId AND f.friendUser.id = :currentUserId)
        OR
        (f.user.id = :currentUserId AND f.friendUser.id = :searchUserId )
      )
      AND f.status = 'pending'
      AND f.user.deletedAt IS NULL
      AND f.friendUser.deletedAt IS NULL
""")
  Friend findPendingRequestOfSearchUserId(Long searchUserId, Long currentUserId);

  @Query(
      """
    SELECT
        CASE WHEN f.user.id = :userId THEN f.friendUser.id ELSE f.user.id END
    FROM Friend f
    WHERE (f.user.id = :userId OR f.friendUser.id = :userId)
      AND f.status = 'accepted'
""")
  List<Long> findAcceptedFriends(Long userId);

  @Query(
      """
    SELECT f FROM Friend f
    WHERE
      f.friendUser.id = :currentUserId
      AND f.status = 'pending'
      AND f.user.deletedAt IS NULL
      AND f.friendUser.deletedAt IS NULL
""")
  List<Friend> findPendingRequest(Long currentUserId);

  @Query(
      """
    SELECT f FROM Friend f
    WHERE
      (
        (f.user.id = :friendId AND f.friendUser.id = :id)
        OR
        (f.user.id = :id AND f.friendUser.id = :friendId )
      )
      AND f.status = 'accepted'
      AND f.user.deletedAt IS NULL
      AND f.friendUser.deletedAt IS NULL
""")
  void deleteByFriendId(Long friendId, Long id);
}
