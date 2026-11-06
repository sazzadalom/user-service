package com.confirmmyshow.userservice.service.impl;

import java.security.KeyPair;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.confirmmyshow.userservice.service.JwtService;

import io.jsonwebtoken.Jwts;

@Service
public class JwtServiceImpl implements JwtService {

	private final KeyPair keyPair;
	private final String keyId;
	private final long accessTokenExpiration;
	
	public JwtServiceImpl(KeyPair keyPair,@Value("${security.jwt.key-id}") String keyId,@Value("${security.jwt.access-token-expiration}") long accessTokenExpiration) {
		this.keyPair = keyPair;
		this.keyId = keyId;
		this.accessTokenExpiration = accessTokenExpiration;
		
	}
	
	
	/**
	 * 1. Headers keyId.
	 * 
	 * 2. JWT claims with subject,email and roles.
	 * 
	 * 3. Token validity/ expiration time stamp.
	 * 
	 * 4. Sign with SRA private key and SHA-256.
	 * 
	 * 5. Generate compact JWT token.
	 * 
	 * */ 
	@Override
	public String generateAccessToken(String userId, String email, List<String> roles) {
		Instant issuedAt = Instant.now();
		Instant expiration = issuedAt.plusSeconds(accessTokenExpiration);
		
		return Jwts.builder()
				.header()      // 1
				.keyId(keyId) // 1
				.and()
				.subject(userId)   // 2
				.claim("email", email)  // 2
				.claim("roles", roles)   // 2
				.issuedAt(Date.from(issuedAt)) // 3 
				.expiration(Date.from(expiration)) // 3
				.signWith(keyPair.getPrivate(), Jwts.SIG.RS256) // 4  
				.compact(); // 5 
	}

	
}
