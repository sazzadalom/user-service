package com.confirmmyshow.userservice.service;

import java.util.List;

public interface JwtService {

	String generateAccessToken(String userId,String email, List<String> roles);
}
