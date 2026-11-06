package com.confirmmyshow.userservice.service;

import com.confirmmyshow.userservice.dto.request.UpdateUserProfileRequest;
import com.confirmmyshow.userservice.dto.response.UserProfileResponse;

public interface UserService {

	UserProfileResponse getMyProfile(String userUuid);

	UserProfileResponse updateMyProfile(String userUuid, UpdateUserProfileRequest request);
}