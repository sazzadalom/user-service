package com.confirmmyshow.userservice.service;

import com.confirmmyshow.userservice.dto.request.LoginRequest;
import com.confirmmyshow.userservice.dto.request.RegisterRequest;
import com.confirmmyshow.userservice.dto.response.LoginResponse;
import com.confirmmyshow.userservice.dto.response.RegisterResponse;

public interface AuthService {

    RegisterResponse register(RegisterRequest request);
    
    LoginResponse login(LoginRequest request);
}