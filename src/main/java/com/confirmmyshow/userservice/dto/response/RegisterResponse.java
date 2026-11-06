package com.confirmmyshow.userservice.dto.response;

import java.util.List;

public record RegisterResponse(String userId,String email,String fullName, List<String> roles) {}