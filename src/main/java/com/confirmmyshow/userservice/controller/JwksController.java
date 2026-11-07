package com.confirmmyshow.userservice.controller;

import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.confirmmyshow.userservice.dto.response.JwksResponse;
import com.confirmmyshow.userservice.service.JwksService;

import java.util.concurrent.TimeUnit;

@RestController
public class JwksController {

	private final JwksService jwksService;

	public JwksController(JwksService jwksService) {
		this.jwksService = jwksService;
	}

	@GetMapping("/.well-known/jwks.json")
	public ResponseEntity<JwksResponse> getJwks() {
		return ResponseEntity.ok().cacheControl(CacheControl.maxAge(600, TimeUnit.SECONDS).cachePublic()).body(jwksService.getJwks());
	}
}