package com.confirmmyshow.userservice.dto.response;

import com.confirmmyshow.userservice.enums.UserStatus;

public record UpdateUserStatusResponse(String userId,UserStatus status) {}