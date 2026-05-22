package com.viinidev.serviceorder.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viinidev.serviceorder.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

@Service
public class JwtService {

    private final String secret;
    private final long expirationMinutes;
    private final ObjectMapper objectMapper;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-minutes}") long expirationMinutes,
            ObjectMapper objectMapper
    ) {
        this.secret = secret;
        this.expirationMinutes = expirationMinutes;
        this.objectMapper = objectMapper;
    }

    public String generate(User user) {
        try {
            String header = encode(objectMapper.writeValueAsString(Map.of("alg", "HS256", "typ", "JWT")));
            String payload = encode(objectMapper.writeValueAsString(Map.of(
                    "sub", user.getEmail(),
                    "role", user.getRole().name(),
                    "name", user.getName(),
                    "exp", Instant.now().plusSeconds(expirationMinutes * 60).getEpochSecond()
            )));
            return header + "." + payload + "." + sign(header + "." + payload);
        } catch (Exception exception) {
            throw new IllegalStateException("Could not generate token.", exception);
        }
    }

    public String extractSubject(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3 || !sign(parts[0] + "." + parts[1]).equals(parts[2])) {
                throw new IllegalArgumentException("Invalid token.");
            }
            JsonNode payload = objectMapper.readTree(Base64.getUrlDecoder().decode(parts[1]));
            if (payload.get("exp").asLong() < Instant.now().getEpochSecond()) {
                throw new IllegalArgumentException("Expired token.");
            }
            return payload.get("sub").asText();
        } catch (Exception exception) {
            throw new IllegalArgumentException("Invalid token.", exception);
        }
    }

    private String encode(String value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String sign(String value) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
    }
}
