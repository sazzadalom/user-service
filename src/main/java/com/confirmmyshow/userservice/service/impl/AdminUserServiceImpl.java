package com.confirmmyshow.userservice.service.impl;

import com.confirmmyshow.userservice.dto.request.AdminUserSearchRequest;
import com.confirmmyshow.userservice.dto.response.AdminUserItemResponse;
import com.confirmmyshow.userservice.dto.response.AdminUserListResponse;
import com.confirmmyshow.userservice.entity.User;
import com.confirmmyshow.userservice.repository.UserRepository;
import com.confirmmyshow.userservice.service.AdminUserService;
import com.confirmmyshow.userservice.specification.AdminUserSpecification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminUserServiceImpl implements AdminUserService {

	private final UserRepository userRepository;

	public AdminUserServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public AdminUserListResponse getUsers(AdminUserSearchRequest request) {

		int page = Math.max(request.page(), 0);
		int size = Math.min(Math.max(request.size(), 1), 100);

		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

		Page<User> userPage = userRepository.findAll(AdminUserSpecification.hasEmail(request.email())
				.and(AdminUserSpecification.hasStatus(request.status())), pageable);

		List<AdminUserItemResponse> items = userPage.getContent().stream().map(this::mapToResponse).toList();

		return new AdminUserListResponse(userPage.getNumber(), userPage.getSize(), userPage.getTotalElements(),
				userPage.getTotalPages(), items);
	}

	private AdminUserItemResponse mapToResponse(User user) {

		List<String> roles = user.getRoles().stream().map(role -> role.getName()).toList();

		return new AdminUserItemResponse(user.getUserUuid(), user.getEmail(), user.getFullName(),
				user.getStatus().name(), roles, user.getCreatedAt());
	}
}