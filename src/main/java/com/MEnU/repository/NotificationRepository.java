package com.MEnU.repository;

import com.MEnU.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Modifying
    @Query("update Notification n set n.seen = true where n.user.id = :userId")
    void markAllAsSeen(@Param("userId") Long userId);

    void deleteByUserId(Long userId);
}