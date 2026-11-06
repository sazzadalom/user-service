package com.confirmmyshow.userservice.service;

public record RefreshTokenResult(
        boolean valid,
        String accessToken,
        String refreshToken
) {

    public static RefreshTokenResult invalid() {
        return new RefreshTokenResult(false, null, null);
    }

    public static RefreshTokenResult success(
            String accessToken,
            String refreshToken
    ) {
        return new RefreshTokenResult(true, accessToken, refreshToken);
    }
}