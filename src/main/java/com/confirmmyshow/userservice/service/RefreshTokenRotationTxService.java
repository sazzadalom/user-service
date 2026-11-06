package com.confirmmyshow.userservice.service;

import com.confirmmyshow.userservice.entity.RefreshToken;
import com.confirmmyshow.userservice.entity.User;
import com.confirmmyshow.userservice.enums.UserStatus;
import com.confirmmyshow.userservice.repository.RefreshTokenRepository;
import com.confirmmyshow.userservice.service.impl.TokenHashService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.List;

@Service
public class RefreshTokenRotationTxService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenHashService tokenHashService;
    private final JwtService jwtService;
    private final long refreshTokenExpiration;

    private final SecureRandom secureRandom = new SecureRandom();

    public RefreshTokenRotationTxService(
            RefreshTokenRepository refreshTokenRepository,
            TokenHashService tokenHashService,
            JwtService jwtService,
            @Value("${security.jwt.refresh-token-expiration}")
            long refreshTokenExpiration
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenHashService = tokenHashService;
        this.jwtService = jwtService;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    @Transactional
    public RefreshTokenResult rotate(String rawRefreshToken) {

        String tokenHash = tokenHashService.hashToken(rawRefreshToken);

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        RefreshToken oldToken = refreshTokenRepository
                .findByTokenHashForUpdate(tokenHash)
                .orElse(null);

        if (oldToken == null) {
            return RefreshTokenResult.invalid();
        }

        // A revoked token is being reused.
        // Revoke every still-active token belonging to its family.
        if (oldToken.getRevokedAt() != null) {

            refreshTokenRepository.revokeActiveTokensByFamilyId(
                    oldToken.getFamilyId(),
                    now
            );

            return RefreshTokenResult.invalid();
        }

        if (!oldToken.getExpiresAt().isAfter(now)) {
            return RefreshTokenResult.invalid();
        }

        User user = oldToken.getUser();

        if (user.getStatus() != UserStatus.ACTIVE) {
            return RefreshTokenResult.invalid();
        }

        // Generate the replacement refresh token.
        String newRawToken = generateRawRefreshToken();

        RefreshToken newToken = new RefreshToken();
        newToken.setUser(user);
        newToken.setTokenHash(tokenHashService.hashToken(newRawToken));

        // Important: preserve the original token family.
        newToken.setFamilyId(oldToken.getFamilyId());

        newToken.setExpiresAt(
                now.plusSeconds(refreshTokenExpiration)
        );

        RefreshToken savedNewToken =
                refreshTokenRepository.save(newToken);

        // Revoke the old token and link it to its replacement.
        oldToken.setRevokedAt(now);
        oldToken.setReplacedById(savedNewToken.getId());

        refreshTokenRepository.save(oldToken);

        List<String> roles = user.getRoles()
                .stream()
                .map(role -> role.getName())
                .toList();

        String accessToken = jwtService.generateAccessToken(
                user.getUserUuid(),
                user.getEmail(),
                roles
        );

        return RefreshTokenResult.success(
                accessToken,
                newRawToken
        );
    }

    private String generateRawRefreshToken() {

        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }
}