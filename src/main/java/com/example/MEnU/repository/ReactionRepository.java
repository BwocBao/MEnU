package com.example.MEnU.repository;

import com.example.MEnU.entity.Reaction;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {
  @Query("""
        SELECT r
        FROM Reaction r
        WHERE r.photo.id = :photoId
    """)
  List<Reaction> findAllByPhotoId(@Param("photoId") Long photoId);
}
