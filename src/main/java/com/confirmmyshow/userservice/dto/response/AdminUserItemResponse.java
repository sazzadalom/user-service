package com.confirmmyshow.userservice.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record AdminUserItemResponse(
        String userId,
        String email,
        String fullName,
        String status,
        List<String> roles,
        LocalDateTime createdAt
) {}