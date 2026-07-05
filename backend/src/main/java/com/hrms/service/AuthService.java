package com.hrms.service;

import com.hrms.dto.AuthResponse;
import com.hrms.dto.LoginRequest;
import com.hrms.dto.RegisterRequest;

// ==============================================================================
// AUTHENTICATION SERVICE INTERFACE
// ==============================================================================
// Defines operations for user registration and login.
// ==============================================================================

public interface AuthService {
    String registerUser(RegisterRequest request);
    AuthResponse loginUser(LoginRequest request);
}
