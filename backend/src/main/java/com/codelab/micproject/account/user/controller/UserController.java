package com.codelab.micproject.account.user.controller;

import com.codelab.micproject.account.user.domain.User;
import com.codelab.micproject.account.user.dto.UserResponse;
import com.codelab.micproject.common.response.ApiResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/me")
    public ApiResponse<UserResponse> me(@AuthenticationPrincipal User user) {
        if (user == null) return ApiResponse.ok(null);
        return ApiResponse.ok(UserResponse.from(user));
    }
}
