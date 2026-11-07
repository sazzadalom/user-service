package com.confirmmyshow.userservice.service.impl;

import java.util.Set;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.confirmmyshow.userservice.entity.Role;
import com.confirmmyshow.userservice.entity.User;
import com.confirmmyshow.userservice.enums.RoleName;
import com.confirmmyshow.userservice.service.AuthService;
import com.confirmmyshow.userservice.service.JwtService;
import com.confirmmyshow.userservice.service.RefreshTokenService;
import com.confirmmyshow.userservice.repository.UserRepository;
import com.confirmmyshow.userservice.repository.RoleRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import com.confirmmyshow.userservice.dto.request.RegisterRequest;
import com.confirmmyshow.userservice.dto.response.RegisterResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.confirmmyshow.userservice.exception.EmailAlreadyRegisteredException;
import com.confirmmyshow.userservice.constan.Constants.Message;
import com.confirmmyshow.userservice.dto.request.LoginRequest;
import com.confirmmyshow.userservice.dto.response.LoginResponse;
import com.confirmmyshow.userservice.enums.UserStatus;
import com.confirmmyshow.userservice.exception.AccountLockedException;
import com.confirmmyshow.userservice.exception.InvalidCredentialsException;


import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {

        // Normalize email before checking and saving.
        String normalizedEmail = request.email().trim().toLowerCase(Locale.ROOT);

        // Check whether email is already registered.
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyRegisteredException("Email is already registered");
        }

        // Fetch the default CUSTOMER role.
        Role customerRole = roleRepository.findByName(RoleName.CUSTOMER.toString())
                .orElseThrow(() -> new IllegalStateException("CUSTOMER role is not configured in database"));

        // Create user entity.
        User user = new User();

        user.setEmail(normalizedEmail);
        user.setFullName(request.fullName().trim());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setPhone(request.phone() == null || request.phone().isBlank() ? null : request.phone());

        // Every newly registered user receives CUSTOMER role.
        user.setRoles(Set.of(customerRole));

        try {
            User savedUser = userRepository.saveAndFlush(user);

            return new RegisterResponse(
                    savedUser.getUserUuid(),
                    savedUser.getEmail(),
                    savedUser.getFullName(),
                    savedUser.getRoles()
                            .stream()
                            .map(role -> role.getName())
                            .toList()
            );

        } catch (DataIntegrityViolationException exception) {
            // Handles concurrent registration attempts for the same email.
            throw new EmailAlreadyRegisteredException("Email is already registered");
        }
    }
    
	@Override
	@Transactional(noRollbackFor = { InvalidCredentialsException.class, AccountLockedException.class })
	public LoginResponse login(LoginRequest request) {

		String email = request.email().trim().toLowerCase(Locale.ROOT);

		User user = userRepository.findByEmail(email).orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

		LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

		if (user.getStatus() == UserStatus.DISABLED) {
			throw new InvalidCredentialsException(Message.USER_ACCOUNT_DISABLE);
		}

		// Handle an existing account lock
		if (user.getStatus() == UserStatus.LOCKED) {

			LocalDateTime lockedUntil = user.getLockedUntil();

			// No expire means an administrative lock.
			if (lockedUntil == null || now.isBefore(lockedUntil)) {
				throw new AccountLockedException("Account is temporarily locked");
			}

			// The temporary lock has expired.
			user.setStatus(UserStatus.ACTIVE);
			user.setLockedUntil(null);
			user.setFailedLoginCount(0);
		}

		// Verify BCrypt password
		if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {

			int failedAttempts = user.getFailedLoginCount() + 1;

			user.setFailedLoginCount(failedAttempts);

			// Lock after five consecutive failed attempts
			if (failedAttempts >= 5) {

				user.setStatus(UserStatus.LOCKED);

				user.setLockedUntil(now.plusMinutes(15));

				userRepository.saveAndFlush(user);

				throw new AccountLockedException("Account is temporarily locked");
			}

			userRepository.saveAndFlush(user);

			throw new InvalidCredentialsException("Invalid email or password");
		}

		// Successful authentication: reset failed attempts
		user.setFailedLoginCount(0);
		user.setLockedUntil(null);
		user.setLastLoginAt(now);

		userRepository.save(user);

		// Get roles
		List<String> roles = user.getRoles().stream().map(role -> role.getName()).toList();

		// Generate access token
		String accessToken = jwtService.generateAccessToken(user.getUserUuid(), user.getEmail(), roles);

		// Generate refresh token
		String refreshToken = refreshTokenService.createRefreshToken(user);

		// Return login response
		LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(user.getUserUuid(), user.getEmail(),user.getFullName(), roles);

		return new LoginResponse(accessToken, refreshToken, "Bearer", 1800, userInfo);
	}

}