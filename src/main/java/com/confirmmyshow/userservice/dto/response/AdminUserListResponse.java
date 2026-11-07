package com.confirmmyshow.userservice.dto.response;

import java.util.List;

public record AdminUserListResponse(
        int page,
        int size,
        long totalElements,
        int totalPages,
        List<AdminUserItemResponse> items
) {}