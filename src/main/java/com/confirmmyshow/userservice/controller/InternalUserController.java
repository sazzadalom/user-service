package com.confirmmyshow.userservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.confirmmyshow.userservice.dto.internal.UserContactResponse;
import com.confirmmyshow.userservice.service.UserService;

@RestController
@RequestMapping("/internal/v1/users")
public class InternalUserController {

	private final UserService userService;

	public InternalUserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/{userId}/contact")
	public ResponseEntity<UserContactResponse> getUserContact(@PathVariable String userId) {

		return ResponseEntity.ok(userService.getUserContact(userId));
	}
}