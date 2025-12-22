package com.example.MEnU.repository;

import com.example.MEnU.entity.Photo;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
  //    @Query(value = "SELECT * FROM photos " +
  //            "WHERE owner_id IN (:ownerIds) " +
  //            "AND created_at > :createdAt " +
  //            "ORDER BY created_at ASC " +
  //            "LIMIT 1",
  //            nativeQuery = true)
  //    Photo findMoveUpNative(@Param("ownerIds") List<Long> ownerIds, @Param("createdAt")
  // LocalDateTime createdAt);

  Optional<Photo> findTopByOwnerIdInAndCreatedAtGreaterThanOrderByCreatedAtAsc(
      List<Long> ownerIds, LocalDateTime createdAt);

  //    @Query(value = """
  //    SELECT * FROM photos
  //    WHERE owner_id IN (:ownerIds)
  //      AND created_at < :createdAt
  //    ORDER BY created_at DESC
  //    LIMIT 1
  // """, nativeQuery = true)
  //    Photo findMoveDownNative(
  //            @Param("ownerIds") List<Long> ownerIds,
  //            @Param("createdAt") LocalDateTime createdAt
  //    );

  Optional<Photo> findTopByOwnerIdInAndCreatedAtLessThanOrderByCreatedAtDesc(
      List<Long> ownerIds, LocalDateTime createdAt);

  Optional<Photo> findTopByOwnerIdInOrderByCreatedAtDesc(List<Long> ownerIds);

  @Query(
      """
    SELECT p FROM Photo p
    WHERE p.owner.id IN :ownerIds
      AND (
            p.createdAt > :createdAt
         OR (p.createdAt = :createdAt AND p.id > :id)
      )
    ORDER BY p.createdAt ASC, p.id ASC
""")
  Optional<Photo> moveUp(List<Long> ownerIds, LocalDateTime createdAt, Long id);

  @Query(
      """
    SELECT p FROM Photo p
    WHERE p.owner.id IN :ownerIds
      AND (
            p.createdAt < :createdAt
         OR (p.createdAt = :createdAt AND p.id < :id)
      )
    ORDER BY p.createdAt DESC, p.id DESC
""")
  Optional<Photo> moveDown(List<Long> ownerIds, LocalDateTime createdAt, Long id);
}
