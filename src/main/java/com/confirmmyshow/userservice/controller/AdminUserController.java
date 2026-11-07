package com.confirmmyshow.userservice.controller;

import com.confirmmyshow.userservice.dto.request.AdminUserSearchRequest;
import com.confirmmyshow.userservice.dto.response.AdminUserListResponse;
import com.confirmmyshow.userservice.enums.UserStatus;
import com.confirmmyshow.userservice.service.AdminUserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}