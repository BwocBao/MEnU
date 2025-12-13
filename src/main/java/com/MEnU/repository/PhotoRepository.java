package com.MEnU.repository;

import com.MEnU.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
//    @Query(value = "SELECT * FROM photos " +
//            "WHERE owner_id IN (:ownerIds) " +
//            "AND created_at > :createdAt " +
//            "ORDER BY created_at ASC " +
//            "LIMIT 1",
//            nativeQuery = true)
//    Photo findMoveUpNative(@Param("ownerIds") List<Long> ownerIds, @Param("createdAt") LocalDateTime createdAt);

    Optional<Photo> findTopByOwnerIdInAndCreatedAtGreaterThanOrderByCreatedAtAsc(
            List<Long> ownerIds, LocalDateTime createdAt
    );

//    @Query(value = """
//    SELECT * FROM photos
//    WHERE owner_id IN (:ownerIds)
//      AND created_at < :createdAt
//    ORDER BY created_at DESC
//    LIMIT 1
//""", nativeQuery = true)
//    Photo findMoveDownNative(
//            @Param("ownerIds") List<Long> ownerIds,
//            @Param("createdAt") LocalDateTime createdAt
//    );

    Optional<Photo> findTopByOwnerIdInAndCreatedAtLessThanOrderByCreatedAtDesc(
            List<Long> ownerIds, LocalDateTime createdAt
    );

}
