package com.confirmmyshow.userservice.filter;

import java.io.IOException;
import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final KeyPair keyPair;

	public JwtAuthenticationFilter(KeyPair keyPair) {
		this.keyPair = keyPair;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String authorizationHeader = request.getHeader("Authorization");

		// No Bearer token: continue the filter chain.
		// Spring Security will reject protected endpoints.
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {

			filterChain.doFilter(request, response);
			return;
		}

		String token = authorizationHeader.substring(7);

		try {

			// Verify JWT signature using RSA public key.
			Claims claims = Jwts.parser().verifyWith((RSAPublicKey) keyPair.getPublic()).build()
					.parseSignedClaims(token).getPayload();

			// Extract user UUID from JWT subject.
			String userUuid = claims.getSubject();

			// Extract roles from JWT.
			List<?> roles = claims.get("roles", List.class);

			if (userUuid == null || userUuid.isBlank() || roles == null) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid access token");
				return;
			}

			// Convert roles into Spring Security authorities.
			List<SimpleGrantedAuthority> authorities = roles.stream().filter(String.class::isInstance)
					.map(String.class::cast).map(role -> new SimpleGrantedAuthority("ROLE_" + role)).toList();

			if (authorities.size() != roles.size()) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid access token roles");
				return;
			}

			// Create authenticated principal.
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userUuid, null,
					authorities);

			SecurityContextHolder.getContext().setAuthentication(authentication);

			filterChain.doFilter(request, response);

		} catch (JwtException | IllegalArgumentException ex) {

			SecurityContextHolder.clearContext();

			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired access token");
		}
	}
}
