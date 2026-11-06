package com.confirmmyshow.userservice.controller;

import com.confirmmyshow.userservice.dto.request.LoginRequest;
import com.confirmmyshow.userservice.dto.request.RefreshTokenRequest;
import com.confirmmyshow.userservice.dto.request.RegisterRequest;
import com.confirmmyshow.userservice.dto.response.LoginResponse;
import com.confirmmyshow.userservice.dto.response.RefreshTokenResponse;
import com.confirmmyshow.userservice.dto.response.RegisterResponse;
import com.confirmmyshow.userservice.service.AuthService;
import com.confirmmyshow.userservice.service.RefreshTokenService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @Operation(summary = "Registration", description = "using this api customer can register there account")
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {

        RegisterResponse response = authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @Operation(summary = "Login", description = "using this api customer can get jwt for login")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Refresh Token", description = "using this api customer can get access token with the help of refresh token")
    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(refreshTokenService.refresh(request.refreshToken()));
    }
    
	@PostMapping("/logout")
	public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {

		refreshTokenService.logout(request.refreshToken());

		return ResponseEntity.noContent().build();
	}
}