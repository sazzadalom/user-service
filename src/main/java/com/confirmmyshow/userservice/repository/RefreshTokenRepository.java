package com.confirmmyshow.userservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.confirmmyshow.userservice.entity.RefreshToken;

import jakarta.persistence.LockModeType;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    List<RefreshToken> findByFamilyId(String familyId);

    @Modifying
    @Query("""
        UPDATE RefreshToken r
        SET r.revokedAt = CURRENT_TIMESTAMP
        WHERE r.user.id = :userId
          AND r.revokedAt IS NULL
    """)
    int revokeAllByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("""
        UPDATE RefreshToken r
        SET r.revokedAt = CURRENT_TIMESTAMP
        WHERE r.familyId = :familyId
          AND r.revokedAt IS NULL
    """)
    int revokeAllByFamilyId(@Param("familyId") String familyId);
    
    

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT rt
        FROM RefreshToken rt
        WHERE rt.tokenHash = :tokenHash
    """)
    Optional<RefreshToken> findByTokenHashForUpdate(
            @Param("tokenHash") String tokenHash
    );

    @Modifying
    @Query("""
        UPDATE RefreshToken rt
        SET rt.revokedAt = :revokedAt
        WHERE rt.familyId = :familyId
          AND rt.revokedAt IS NULL
    """)
    int revokeActiveTokensByFamilyId(
            @Param("familyId") String familyId,
            @Param("revokedAt") java.time.LocalDateTime revokedAt
    );
    
    
}