package com.hrms.service.impl;

import com.hrms.dto.AuthResponse;
import com.hrms.dto.LoginRequest;
import com.hrms.dto.RegisterRequest;
import com.hrms.entity.User;
import com.hrms.repository.UserRepository;
import com.hrms.security.JwtUtils;
import com.hrms.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// ==============================================================================
// AUTHENTICATION SERVICE IMPLEMENTATION
// ==============================================================================
// Implements manual register and login logic including password hashing and JWT.
// ==============================================================================

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    @Transactional
    public String registerUser(RegisterRequest request) {
        // 1. Verify username uniqueness
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username is already taken!");
        }

        // 2. Verify email uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered!");
        }

        // 3. Determine the user role
        String role = request.getRole();
        if (role == null || role.isBlank()) {
            role = "ROLE_STAFF"; // Default role
        } else if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role.toUpperCase();
        }

        // 4. Create and save the new User
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // Hash password
                .role(role)
                .build();

        userRepository.save(user);

        return "User registered successfully!";
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse loginUser(LoginRequest request) {
        // 1. Find user by username or email
        User user = userRepository.findByUsername(request.getUsernameOrEmail())
                .or(() -> userRepository.findByEmail(request.getUsernameOrEmail()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password!"));

        // 2. Check if password matches
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password!");
        }

        // 3. Generate JWT Token
        String token = jwtUtils.generateToken(user.getUsername(), user.getEmail(), user.getRole());

        // 4. Return AuthResponse
        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
