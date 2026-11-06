package com.confirmmyshow.userservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class JwtConfig {

    @Bean
    public KeyPair jwtKeyPair(
            @Value("${security.jwt.private-key-location}")
            Resource privateKeyResource,

            @Value("${security.jwt.public-key-location}")
            Resource publicKeyResource
    ) throws Exception {

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        // Load private key
        String privateKeyPem = readPemFile(privateKeyResource);

        byte[] privateKeyBytes = Base64.getDecoder().decode(removePemHeaders(privateKeyPem));

        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);

        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

        // Load public key
        String publicKeyPem = readPemFile(publicKeyResource);

        byte[] publicKeyBytes = Base64.getDecoder().decode(removePemHeaders(publicKeyPem));

        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);

        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        return new KeyPair(publicKey, privateKey);
    }

    private String readPemFile(Resource resource) throws Exception {

        try (InputStream inputStream = resource.getInputStream()) {
            return new String(inputStream.readAllBytes(),StandardCharsets.UTF_8);
        }
    }

    private String removePemHeaders(String pem) {

        return pem
                .replaceAll("-----BEGIN [^-]+-----", "")
                .replaceAll("-----END [^-]+-----", "")
                .replaceAll("\\s", "");
    }
}
