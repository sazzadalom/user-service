package com.confirmmyshow.userservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.confirmmyshow.userservice.dto.request.AdminUserSearchRequest;
import com.confirmmyshow.userservice.dto.request.UpdateUserRolesRequest;
import com.confirmmyshow.userservice.dto.request.UpdateUserStatusRequest;
import com.confirmmyshow.userservice.dto.response.AdminUserListResponse;
import com.confirmmyshow.userservice.dto.response.UpdateUserRolesResponse;
import com.confirmmyshow.userservice.dto.response.UpdateUserStatusResponse;
import com.confirmmyshow.userservice.enums.UserStatus;
import com.confirmmyshow.userservice.service.AdminUserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/admin/users")
public class AdminUserController {

	private final AdminUserService adminUserService;

	public AdminUserController(AdminUserService adminUserService) {
		this.adminUserService = adminUserService;
	}

	@GetMapping
	public ResponseEntity<AdminUserListResponse> getUsers(

			@RequestParam(required = false) String email,

			@RequestParam(required = false) UserStatus status,

			@RequestParam(defaultValue = "0") int page,

			@RequestParam(defaultValue = "10") int size) {

		AdminUserSearchRequest request = new AdminUserSearchRequest(email, status, page, size);

		return ResponseEntity.ok(adminUserService.getUsers(request));
	}

	@PatchMapping("/{userId}/status")
	public ResponseEntity<UpdateUserStatusResponse> updateUserStatus(
			@PathVariable String userId,
			@Valid @RequestBody UpdateUserStatusRequest request) {

		return ResponseEntity.ok(adminUserService.updateUserStatus(userId, request));
	}

	@PutMapping("/{userId}/roles")
	public ResponseEntity<UpdateUserRolesResponse> updateUserRoles(@PathVariable String userId,@Valid @RequestBody UpdateUserRolesRequest request, Authentication authentication) {

		return ResponseEntity.ok(adminUserService.updateUserRoles(userId, authentication.getName(), request));
	}
}