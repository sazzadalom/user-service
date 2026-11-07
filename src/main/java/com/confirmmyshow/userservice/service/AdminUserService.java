package com.confirmmyshow.userservice.service;

import com.confirmmyshow.userservice.dto.request.AdminUserSearchRequest;
import com.confirmmyshow.userservice.dto.request.UpdateUserRolesRequest;
import com.confirmmyshow.userservice.dto.request.UpdateUserStatusRequest;
import com.confirmmyshow.userservice.dto.response.AdminUserListResponse;
import com.confirmmyshow.userservice.dto.response.UpdateUserRolesResponse;
import com.confirmmyshow.userservice.dto.response.UpdateUserStatusResponse;

public interface AdminUserService {

    AdminUserListResponse getUsers(AdminUserSearchRequest request);
    
    UpdateUserStatusResponse updateUserStatus(String userId,UpdateUserStatusRequest request);
    

	UpdateUserRolesResponse updateUserRoles(String userId, String adminUserId, UpdateUserRolesRequest request);
}