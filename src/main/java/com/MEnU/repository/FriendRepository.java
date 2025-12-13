package com.MEnU.repository;

import com.MEnU.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {

    @Query("""
        SELECT f FROM Friend f
        WHERE f.status = 'accepted'
        AND (f.user.id = :userId OR f.friendUser.id = :userId)
    """)
    List<Friend> findAllFriendsOfUser(Long userId);

    // Kiểm tra 2 user bất kỳ đã tồn tại record hay chưa
    @Query("""
        SELECT f FROM Friend f 
        WHERE 
            (f.user.id = :u1 AND f.friendUser.id = :u2)
         OR (f.user.id = :u2 AND f.friendUser.id = :u1)
    """)
    Optional<Friend> findRelation(Long u1, Long u2);

    @Query("""
    SELECT f FROM Friend f
    WHERE f.user.id = :senderId
      AND f.friendUser.id = :currentUserId
      AND f.status = 'pending'
""")
    Optional<Friend> findPendingRequest(Long senderId, Long currentUserId);

    @Query("""
    SELECT 
        CASE WHEN f.user.id = :userId THEN f.friendUser.id ELSE f.user.id END
    FROM Friend f
    WHERE (f.user.id = :userId OR f.friendUser.id = :userId)
      AND f.status = 'accepted'
""")
    List<Long> findAcceptedFriends(Long userId);

}