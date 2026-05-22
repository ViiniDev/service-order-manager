package com.viinidev.serviceorder.service;

import com.viinidev.serviceorder.domain.User;
import com.viinidev.serviceorder.dto.AuthRequest;
import com.viinidev.serviceorder.dto.AuthResponse;
import com.viinidev.serviceorder.dto.RegisterRequest;
import com.viinidev.serviceorder.repository.UserRepository;
import com.viinidev.serviceorder.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already registered.");
        }
        User user = userRepository.save(new User(
                request.name().trim(),
                request.email().trim().toLowerCase(),
                passwordEncoder.encode(request.password()),
                request.role()
        ));
        return AuthResponse.from(jwtService.generate(user), user);
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        User user = userRepository.findByEmail(request.email().trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials."));
        return AuthResponse.from(jwtService.generate(user), user);
    }
}
