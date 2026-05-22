package com.viinidev.serviceorder.dto;

import com.viinidev.serviceorder.domain.User;

public record AuthResponse(
        String token,
        UserResponse user
) {
    public static AuthResponse from(String token, User user) {
        return new AuthResponse(token, UserResponse.from(user));
    }
}
