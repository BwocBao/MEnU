package com.example.MEnU.repository;

import com.example.MEnU.entity.User;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);

  Optional<User> findByUsername(String username);

  List<User> findByUsernameContainingIgnoreCase(String keyword);

  Optional<User> findByUsernameAndDeletedAtIsNull(String currentUsername);

  Collection<User> findByUsernameContainingIgnoreCaseAndDeletedAtIsNull(String keyword);
}
