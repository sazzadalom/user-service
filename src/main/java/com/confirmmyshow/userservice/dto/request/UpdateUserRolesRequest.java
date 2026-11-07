package com.confirmmyshow.userservice.dto.request;

import java.util.Set;

import com.confirmmyshow.userservice.enums.RoleName;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRolesRequest(

		@NotNull(message = "Roles are required") @NotEmpty(message = "At least one role must be provided") Set<RoleName> roles

) {
}