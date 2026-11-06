package com.confirmmyshow.userservice.service;

import com.confirmmyshow.userservice.dto.response.RefreshTokenResponse;
import com.confirmmyshow.userservice.entity.User;

public interface RefreshTokenService {

	String createRefreshToken(User user);
	
	RefreshTokenResponse refresh(String rawRefreshToken);
	
	void logout(String rawRefreshToken);
}
