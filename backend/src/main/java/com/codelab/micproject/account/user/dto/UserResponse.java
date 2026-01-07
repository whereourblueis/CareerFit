package com.codelab.micproject.account.user.dto;

import com.codelab.micproject.account.user.domain.User;
import com.codelab.micproject.account.user.domain.UserRole;

public record UserResponse(Long id, String email, String name, UserRole role) {
    public static UserResponse from(User u) {
        return new UserResponse(u.getId(), u.getEmail(), u.getName(), u.getRole());
    }
}
