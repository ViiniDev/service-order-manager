package com.viinidev.serviceorder.dto;

import com.viinidev.serviceorder.domain.Role;
import com.viinidev.serviceorder.domain.User;

public record UserResponse(
        Long id,
        String name,
        String email,
        Role role
) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}
