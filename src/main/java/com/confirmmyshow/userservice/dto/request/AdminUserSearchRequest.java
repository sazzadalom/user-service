package com.confirmmyshow.userservice.dto.request;

import com.confirmmyshow.userservice.enums.UserStatus;

public record AdminUserSearchRequest(
        String email,
        UserStatus status,
        int page,
        int size
) {}