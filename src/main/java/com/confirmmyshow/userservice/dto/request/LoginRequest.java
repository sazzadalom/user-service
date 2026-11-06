package com.confirmmyshow.userservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
		
		@NotBlank(message = "email is required")
		@Email(message = "please provide email")
		@Size(max=255, message= "email can not exceed 255 charachters")
		String email,
		
		@NotBlank(message = "password is required")
		@Size(max=72, message= "password can not exceed 72 characters")
		String password) {}
