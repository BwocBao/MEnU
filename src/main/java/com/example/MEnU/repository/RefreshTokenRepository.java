package com.example.MEnU.repository;

import com.example.MEnU.entity.RefreshToken;
import com.example.MEnU.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

  Optional<RefreshToken> findByRefreshToken(String refreshToken);

  void deleteByUser(User user);

  @Modifying
  @Transactional
  void deleteByRefreshToken(String refreshToken);

  @Modifying
  @Query("UPDATE RefreshToken r SET r.revoked = true WHERE r.user.id = :userId")
  void revokeAllByUserId(Long userId);
}
