package com.MEnU.repository;

import com.MEnU.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query(value = """
    SELECT 
        u.id,
        u.display_name,
        u.avatarURL,
        m.text AS lastMessage,
        m.created_at AS lastMessageTime,
        EXISTS (
            SELECT 1
            FROM messages m2
            WHERE m2.sender_id = u.id
              AND m2.receiver_id = :userId
              AND m2.seen = false
        ) AS hasUnread
    FROM messages m
    JOIN users u 
      ON u.id = 
        CASE 
            WHEN m.sender_id = :userId THEN m.receiver_id
            ELSE m.sender_id
        END
    WHERE m.id IN (
        SELECT MAX(id)
        FROM messages
        WHERE sender_id = :userId OR receiver_id = :userId
        GROUP BY 
            CASE 
                WHEN sender_id = :userId THEN receiver_id
                ELSE sender_id
            END
    )
    ORDER BY m.created_at DESC
""", nativeQuery = true)
    List<Object[]> findChatList(@Param("userId") Long userId);


    @Modifying
    @Query("""
    UPDATE Message m
    SET m.seen = true
    WHERE m.sender.id = :otherUserId
      AND m.receiver.id = :meId
      AND m.seen = false
""")
    int markSeen(
            @Param("meId") Long meId,
            @Param("otherUserId") Long otherUserId
    );


    @Query("""
    SELECT m
    FROM Message m
    WHERE 
        (m.sender.id = :meId AND m.receiver.id = :otherId)
        OR
        (m.sender.id = :otherId AND m.receiver.id = :meId)
    ORDER BY m.createdAt DESC
""")
    Page<Message> findConversation(
            @Param("meId") Long meId,
            @Param("otherId") Long otherId,
            Pageable pageable
    );

}
