package com.confirmmyshow.userservice.service.impl;

import com.confirmmyshow.userservice.constan.Constants.Message;
import com.confirmmyshow.userservice.dto.internal.UserContactResponse;
import com.confirmmyshow.userservice.dto.request.ChangePasswordRequest;
import com.confirmmyshow.userservice.dto.request.UpdateUserProfileRequest;
import com.confirmmyshow.userservice.dto.response.UserProfileResponse;
import com.confirmmyshow.userservice.entity.User;
import com.confirmmyshow.userservice.exception.InvalidCredentialsException;
import com.confirmmyshow.userservice.exception.ResourceNotFoundException;
import com.confirmmyshow.userservice.repository.RefreshTokenRepository;
import com.confirmmyshow.userservice.repository.UserRepository;
import com.confirmmyshow.userservice.service.UserService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	private final RefreshTokenRepository refreshTokenRepository;
	private final PasswordEncoder passwordEncoder;
	
	public UserServiceImpl(UserRepository userRepository,RefreshTokenRepository refreshTokenRepository,PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.refreshTokenRepository = refreshTokenRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	@Transactional(readOnly = true)
	public UserProfileResponse getMyProfile(String userUuid) {

		User user = findUserByUuid(userUuid);

		return mapToResponse(user);
	}

	@Override
	@Transactional
	public UserProfileResponse updateMyProfile(String userUuid, UpdateUserProfileRequest request) {

		User user = findUserByUuid(userUuid);

		user.setFullName(request.fullName().trim());

		String phone = request.phone();

		user.setPhone(phone == null || phone.isBlank() ? null : phone.trim());

		User updatedUser = userRepository.save(user);

		return mapToResponse(updatedUser);
	}

	private User findUserByUuid(String userUuid) {

		return userRepository.findByUserUuid(userUuid).orElseThrow(() -> new ResourceNotFoundException("User not found"));
	}

	private UserProfileResponse mapToResponse(User user) {

		List<String> roles = user.getRoles().stream().map(role -> role.getName()).toList();

		return new UserProfileResponse(user.getUserUuid(), user.getEmail(), user.getFullName(), user.getPhone(), roles,
				user.getCreatedAt());
	}

	@Override
	@Transactional
	public void changePassword(String userUuid,ChangePasswordRequest request) {

	    User user = findUserByUuid(userUuid);

	    // Verify the current password.
	    if (!passwordEncoder.matches(request.currentPassword(),user.getPasswordHash())) {
	        throw new InvalidCredentialsException(Message.INVALID_USER_CREDENTIAL);
	    }

	    // Encode and save the new password.
	    user.setPasswordHash(passwordEncoder.encode(request.newPassword()));

	    userRepository.save(user);

	    // Revoke all active refresh tokens for this user.
	    refreshTokenRepository.revokeAllActiveTokensByUserId(user.getId(),LocalDateTime.now(ZoneOffset.UTC));
	}
	
	@Override
	@Transactional(readOnly = true)
	public UserContactResponse getUserContact(String userId) {

		User user = userRepository.findByUserUuid(userId)
				.orElseThrow(() -> new ResourceNotFoundException(Message.USER_ID_NOT_FOUNDED + userId));

		return new UserContactResponse(user.getUserUuid(), user.getEmail(), user.getFullName());
	}
}