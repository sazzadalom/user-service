package com.confirmmyshow.userservice.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record UserProfileResponse(String userId, String email, String fullName, String phone, List<String> roles,
		LocalDateTime createdAt) {
}