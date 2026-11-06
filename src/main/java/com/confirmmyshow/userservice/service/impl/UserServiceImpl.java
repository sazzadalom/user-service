package com.confirmmyshow.userservice.service.impl;

import com.confirmmyshow.userservice.dto.request.UpdateUserProfileRequest;
import com.confirmmyshow.userservice.dto.response.UserProfileResponse;
import com.confirmmyshow.userservice.entity.User;
import com.confirmmyshow.userservice.exception.ResourceNotFoundException;
import com.confirmmyshow.userservice.repository.UserRepository;
import com.confirmmyshow.userservice.service.UserService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	public UserServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
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
}