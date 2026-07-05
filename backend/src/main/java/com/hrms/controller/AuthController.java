package com.hrms.controller;

import com.hrms.dto.ApiResponse;
import com.hrms.dto.AuthResponse;
import com.hrms.dto.LoginRequest;
import com.hrms.dto.RegisterRequest;
import com.hrms.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// ==============================================================================
// AUTHENTICATION CONTROLLER
// ==============================================================================
// Handles HTTP requests for registering new users and logging in.
// ==============================================================================

@Tag(name = "Authentication", description = "Endpoints for user registration and login")
@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ENDPOINT: POST http://localhost:8020/api/v1/auth/register
    @Operation(summary = "Register a new user account")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequest request) {
        String result = authService.registerUser(request);
        return new ResponseEntity<>(ApiResponse.success(result), HttpStatus.CREATED);
    }

    // ENDPOINT: POST http://localhost:8020/api/v1/auth/login
    @Operation(summary = "Login to obtain JWT token")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.loginUser(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }
}
