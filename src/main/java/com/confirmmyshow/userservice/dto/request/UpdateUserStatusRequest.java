package com.confirmmyshow.userservice.dto.request;

import com.confirmmyshow.userservice.enums.UserStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateUserStatusRequest(

        @NotNull(message = "Status is required")
        UserStatus status

) {
}