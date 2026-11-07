package com.confirmmyshow.userservice.dto.response;

import java.util.Set;

import com.confirmmyshow.userservice.enums.RoleName;

public record UpdateUserRolesResponse(String userId, Set<RoleName> roles) {
}