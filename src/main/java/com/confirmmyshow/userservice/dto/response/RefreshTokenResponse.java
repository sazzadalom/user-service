
package com.confirmmyshow.userservice.dto.response;

public record RefreshTokenResponse(

        String accessToken,

        String refreshToken,

        String tokenType,

        long expiresIn

) {
}
