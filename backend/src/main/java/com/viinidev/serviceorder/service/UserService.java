package com.viinidev.serviceorder.service;

import com.viinidev.serviceorder.domain.Role;
import com.viinidev.serviceorder.dto.UserResponse;
import com.viinidev.serviceorder.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponse> listTechnicians() {
        return userRepository.findByRole(Role.TECHNICIAN).stream().map(UserResponse::from).toList();
    }
}
