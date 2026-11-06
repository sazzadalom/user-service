package com.confirmmyshow.userservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserProfileRequest(

		@NotBlank(message = "Full name is required") @Size(min = 2, max = 120, message = "Full name must be between 2 and 120 characters") String fullName,

		@Pattern(regexp = "^$|^[0-9]{10,15}$", message = "Phone number must contain 10 to 15 digits") String phone) {
}