package com.confirmmyshow.userservice.service;

import com.confirmmyshow.userservice.dto.request.AdminUserSearchRequest;
import com.confirmmyshow.userservice.dto.response.AdminUserListResponse;

public interface AdminUserService {

    AdminUserListResponse getUsers(AdminUserSearchRequest request);
}