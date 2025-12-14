package com.MEnU.repository;

import com.MEnU.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);

    List<User> findByUsernameContainingIgnoreCase(String keyword);

    Optional<User> findByUsernameAndDeletedAtIsNull(String currentUsername);

    Collection<User> findByUsernameContainingIgnoreCaseAndDeletedAtIsNull(String keyword);
}