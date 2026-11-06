package com.confirmmyshow.userservice.dto.response;

import java.util.List;

public record LoginResponse(String acessToken, String refreshToken, String tokenType, long expiresIn, UserInfo user) {
	public record UserInfo(String userId, String email, String fullName, List<String> roles) {}
}
