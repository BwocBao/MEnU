package com.example.MEnU.repository;

import com.example.MEnU.entity.User;
import com.example.MEnU.entity.VerificationToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
  Optional<VerificationToken> findByToken(String token);

  void deleteAllByUser(User user);
}
