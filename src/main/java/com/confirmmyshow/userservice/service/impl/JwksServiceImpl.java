package com.confirmmyshow.userservice.service.impl;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.confirmmyshow.userservice.dto.response.JwksResponse;
import com.confirmmyshow.userservice.service.JwksService;

@Service
public class JwksServiceImpl implements JwksService {

	private final KeyPair keyPair;
	private final String keyId;

	public JwksServiceImpl(KeyPair keyPair, @Value("${security.jwt.key-id}") String keyId) {
		this.keyPair = keyPair;
		this.keyId = keyId;
	}

	@Override
	public JwksResponse getJwks() {

		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

//		String modulus = Base64.getUrlEncoder().withoutPadding().encodeToString(publicKey.getModulus().toByteArray());
//		String exponent = Base64.getUrlEncoder().withoutPadding().encodeToString(publicKey.getPublicExponent().toByteArray());
		String modulus = toBase64Url(publicKey.getModulus());
		String exponent = toBase64Url(publicKey.getPublicExponent());


		JwksResponse.JwkKey jwkKey = new JwksResponse.JwkKey("RSA", "sig", "RS256", keyId, modulus, exponent);

		return new JwksResponse(List.of(jwkKey));
	}

	private String toBase64Url(BigInteger value) {
		byte[] bytes = value.toByteArray();

		if (bytes.length > 1 && bytes[0] == 0) {
			bytes = java.util.Arrays.copyOfRange(bytes, 1, bytes.length);
		}

		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}
}