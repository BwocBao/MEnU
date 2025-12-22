package com.example.MEnU.repository;

import com.example.MEnU.entity.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

  List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

  @Modifying
  @Query("update Notification n set n.seen = true where n.user.id = :userId")
  void markAllAsSeen(@Param("userId") Long userId);

  void deleteByUserId(Long userId);
}
