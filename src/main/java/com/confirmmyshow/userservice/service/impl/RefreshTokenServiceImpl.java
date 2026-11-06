
package com.confirmmyshow.userservice.service.impl;

import com.confirmmyshow.userservice.dto.response.RefreshTokenResponse;
import com.confirmmyshow.userservice.entity.RefreshToken;
import com.confirmmyshow.userservice.entity.User;
import com.confirmmyshow.userservice.exception.InvalidRefreshTokenException;
import com.confirmmyshow.userservice.repository.RefreshTokenRepository;
import com.confirmmyshow.userservice.service.RefreshTokenResult;
import com.confirmmyshow.userservice.service.RefreshTokenRotationTxService;
import com.confirmmyshow.userservice.service.RefreshTokenService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenHashService tokenHashService;
    private final long refreshTokenExpiration;
    private final RefreshTokenRotationTxService rotationTxService;

    private final SecureRandom secureRandom = new SecureRandom();

    public RefreshTokenServiceImpl(
            RefreshTokenRepository refreshTokenRepository,
            TokenHashService tokenHashService,
            @Value("${security.jwt.refresh-token-expiration}")
            long refreshTokenExpiration,
            RefreshTokenRotationTxService rotationTxService
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenHashService = tokenHashService;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.rotationTxService = rotationTxService;
    }

    @Override
    @Transactional
    public String createRefreshToken(User user) {

        // Generate 256 bits of crypto-graphically secure random data
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        // Return URL-safe token without padding
        String rawToken = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        // Store only the SHA-256 hash
        String tokenHash = tokenHashService.hashToken(rawToken);

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(user);
        refreshToken.setTokenHash(tokenHash);
        refreshToken.setFamilyId(UUID.randomUUID().toString());

        refreshToken.setExpiresAt(LocalDateTime.now(ZoneOffset.UTC).plusSeconds(refreshTokenExpiration));

        refreshTokenRepository.save(refreshToken);

        // Return raw token to client; never store it in the database
        return rawToken;
    }
    
	@Override
	public RefreshTokenResponse refresh(String rawRefreshToken) {

		RefreshTokenResult result = rotationTxService.rotate(rawRefreshToken);

		if (!result.valid()) {
			throw new InvalidRefreshTokenException("Invalid refresh token");
		}

		return new RefreshTokenResponse(result.accessToken(), result.refreshToken(), "Bearer", 1800);
	}
}
