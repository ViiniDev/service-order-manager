package com.viinidev.serviceorder.controller;

import com.viinidev.serviceorder.dto.UserResponse;
import com.viinidev.serviceorder.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/technicians")
    public List<UserResponse> listTechnicians() {
        return userService.listTechnicians();
    }
}
