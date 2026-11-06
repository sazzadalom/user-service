package com.confirmmyshow.userservice.controller;

import com.confirmmyshow.userservice.dto.request.UpdateUserProfileRequest;
import com.confirmmyshow.userservice.dto.response.UserProfileResponse;
import com.confirmmyshow.userservice.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/me")
	public ResponseEntity<UserProfileResponse> getMyProfile(Authentication authentication) {

		String userUuid = authentication.getName();

		return ResponseEntity.ok(userService.getMyProfile(userUuid));
	}

	@PutMapping("/me")
	public ResponseEntity<UserProfileResponse> updateMyProfile(Authentication authentication,
			@Valid @RequestBody UpdateUserProfileRequest request) {

		String userUuid = authentication.getName();

		return ResponseEntity.ok(userService.updateMyProfile(userUuid, request));
	}
}