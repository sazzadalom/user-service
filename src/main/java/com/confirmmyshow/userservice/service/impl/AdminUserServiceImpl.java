package com.confirmmyshow.userservice.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.confirmmyshow.userservice.constan.Constants.Message;
import com.confirmmyshow.userservice.dto.request.AdminUserSearchRequest;
import com.confirmmyshow.userservice.dto.request.UpdateUserRolesRequest;
import com.confirmmyshow.userservice.dto.request.UpdateUserStatusRequest;
import com.confirmmyshow.userservice.dto.response.AdminUserItemResponse;
import com.confirmmyshow.userservice.dto.response.AdminUserListResponse;
import com.confirmmyshow.userservice.dto.response.UpdateUserRolesResponse;
import com.confirmmyshow.userservice.dto.response.UpdateUserStatusResponse;
import com.confirmmyshow.userservice.entity.Role;
import com.confirmmyshow.userservice.entity.User;
import com.confirmmyshow.userservice.enums.RoleName;
import com.confirmmyshow.userservice.exception.InvalidStatusTransitionException;
import com.confirmmyshow.userservice.exception.ResourceNotFoundException;
import com.confirmmyshow.userservice.repository.RoleRepository;
import com.confirmmyshow.userservice.repository.UserRepository;
import com.confirmmyshow.userservice.service.AdminUserService;
import com.confirmmyshow.userservice.specification.AdminUserSpecification;

@Service
public class AdminUserServiceImpl implements AdminUserService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;

	public AdminUserServiceImpl(UserRepository userRepository,RoleRepository roleRepository) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
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

	@Override
	public UpdateUserStatusResponse updateUserStatus(String userId, UpdateUserStatusRequest request) {
		 User user = userRepository.findByUserUuid(userId).orElseThrow(()->new ResourceNotFoundException(Message.USER_ID_NOT_FOUNDED + userId));
		 user.setStatus(request.status());
		 User update = userRepository.save(user);
		 
		return new UpdateUserStatusResponse(update.getUserUuid(),update.getStatus());
	}

	@Override
	@Transactional
	public UpdateUserRolesResponse updateUserRoles(String userId, String adminUserId, UpdateUserRolesRequest request) {

		User user = userRepository.findByUserUuid(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

		Set<String> currentRoles = user.getRoles().stream().map(role -> role.getName()).collect(Collectors.toSet());

		// Prevent an admin from removing their own ADMIN role
		if (userId.equals(adminUserId) && currentRoles.contains(RoleName.ADMIN.toString())
				&& !request.roles().contains(RoleName.ADMIN)) {

			throw new InvalidStatusTransitionException(Message.ADMIN_USER_CANNOT_REMOVE_THEIR_ROLE);
		}

		
		Set<Role> updatedRoles = request.roles().stream()
				.map(roleName -> roleRepository.findByName(roleName.toString())
						.orElseThrow(() -> new IllegalStateException("Role not found: " + roleName)))
				.collect(Collectors.toSet());

		user.setRoles(updatedRoles);

		User updatedUser = userRepository.save(user);

		Set<RoleName> responseRoles = updatedUser.getRoles().stream()
		        .map(role -> RoleName.valueOf(role.getName()))
		        .collect(Collectors.toSet());
		
		return new UpdateUserRolesResponse(updatedUser.getUserUuid(), responseRoles);
	}
}