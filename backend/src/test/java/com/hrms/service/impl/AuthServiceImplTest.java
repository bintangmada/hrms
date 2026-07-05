package com.hrms.service.impl;

import com.hrms.dto.AuthResponse;
import com.hrms.dto.LoginRequest;
import com.hrms.dto.RegisterRequest;
import com.hrms.entity.Role;
import com.hrms.entity.User;
import com.hrms.entity.UserRole;
import com.hrms.repository.RoleRepository;
import com.hrms.repository.UserRepository;
import com.hrms.repository.UserRoleRepository;
import com.hrms.security.JwtUtils;
import com.hrms.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private UserRoleRepository userRoleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterUser_Success() {
        RegisterRequest request = RegisterRequest.builder()
                .username("john_doe")
                .email("john@example.com")
                .password("securePass")
                .roleIds(List.of(1L))
                .build();

        Role mockRole = Role.builder().id(1L).name("ROLE_STAFF").build();
        User mockSavedUser = User.builder()
                .id(100L)
                .username("john_doe")
                .email("john@example.com")
                .emailVerified(0)
                .build();

        when(userRepository.existsByUsername("john_doe")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(roleRepository.findByIdAndDeletedStatus(1L, 0)).thenReturn(Optional.of(mockRole));
        when(passwordEncoder.encode("securePass")).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenReturn(mockSavedUser);

        String result = authService.registerUser(request);

        assertEquals("User registered successfully! Please check your email to verify your account.", result);
        verify(userRepository, times(1)).save(any(User.class));
        verify(userRoleRepository, times(1)).save(any(UserRole.class));
        verify(emailService, times(1)).sendVerificationEmail(eq("john@example.com"), eq("john_doe"), anyString());
        verify(emailService, times(1)).sendRoleAssignmentNotification(eq("john@example.com"), eq("john_doe"), eq(List.of("ROLE_STAFF")));
    }

    @Test
    public void testRegisterUser_UsernameTaken() {
        RegisterRequest request = RegisterRequest.builder()
                .username("john_doe")
                .email("john@example.com")
                .password("securePass")
                .roleIds(List.of(1L))
                .build();

        when(userRepository.existsByUsername("john_doe")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> authService.registerUser(request));
    }

    @Test
    public void testRegisterUser_RoleIdsEmpty() {
        RegisterRequest request = RegisterRequest.builder()
                .username("john_doe")
                .email("john@example.com")
                .password("securePass")
                .roleIds(Collections.emptyList())
                .build();

        assertThrows(IllegalArgumentException.class, () -> authService.registerUser(request));
    }

    @Test
    public void testRegisterUser_RoleNotFound() {
        RegisterRequest request = RegisterRequest.builder()
                .username("john_doe")
                .email("john@example.com")
                .password("securePass")
                .roleIds(List.of(99L))
                .build();

        when(userRepository.existsByUsername("john_doe")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(roleRepository.findByIdAndDeletedStatus(99L, 0)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> authService.registerUser(request));
    }

    @Test
    public void testRegisterStaffUser_Success() {
        RegisterRequest request = RegisterRequest.builder()
                .username("staff_user")
                .email("staff@example.com")
                .password("staffPass")
                .build();

        Role staffRole = Role.builder().id(2L).name("ROLE_STAFF").build();
        User mockSavedUser = User.builder()
                .id(101L)
                .username("staff_user")
                .email("staff@example.com")
                .emailVerified(0)
                .build();

        when(userRepository.existsByUsername("staff_user")).thenReturn(false);
        when(userRepository.existsByEmail("staff@example.com")).thenReturn(false);
        when(roleRepository.findByNameAndDeletedStatus("ROLE_STAFF", 0)).thenReturn(Optional.of(staffRole));
        when(passwordEncoder.encode("staffPass")).thenReturn("encodedStaffPass");
        when(userRepository.save(any(User.class))).thenReturn(mockSavedUser);

        String result = authService.registerStaffUser(request);

        assertEquals("Staff registered successfully! Please check your email to verify your account.", result);
        verify(userRepository, times(1)).save(any(User.class));
        verify(userRoleRepository, times(1)).save(any(UserRole.class));
        verify(emailService, times(1)).sendVerificationEmail(eq("staff@example.com"), eq("staff_user"), anyString());
    }

    @Test
    public void testLoginUser_Success() {
        LoginRequest request = LoginRequest.builder()
                .usernameOrEmail("john_doe")
                .password("myPassword")
                .build();

        User user = User.builder()
                .id(100L)
                .username("john_doe")
                .email("john@example.com")
                .password("hashedPassword")
                .role("ROLE_STAFF")
                .emailVerified(1) // Verified
                .build();

        Role mockRole = Role.builder().id(1L).name("ROLE_STAFF").build();
        UserRole userRole = UserRole.builder().userId(100L).roleId(1L).build();

        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("myPassword", "hashedPassword")).thenReturn(true);
        when(userRoleRepository.findAllByUserIdAndDeletedStatus(100L, 0)).thenReturn(List.of(userRole));
        when(roleRepository.findByIdAndDeletedStatus(1L, 0)).thenReturn(Optional.of(mockRole));
        when(jwtUtils.generateToken("john_doe", "john@example.com", "ROLE_STAFF")).thenReturn("mockedJwtToken");

        AuthResponse response = authService.loginUser(request);

        assertNotNull(response);
        assertEquals("mockedJwtToken", response.getToken());
        assertEquals("john_doe", response.getUsername());
        assertEquals("john@example.com", response.getEmail());
        assertEquals("ROLE_STAFF", response.getRole());
    }

    @Test
    public void testLoginUser_UnverifiedEmail() {
        LoginRequest request = LoginRequest.builder()
                .usernameOrEmail("john_doe")
                .password("myPassword")
                .build();

        User user = User.builder()
                .id(100L)
                .username("john_doe")
                .email("john@example.com")
                .password("hashedPassword")
                .emailVerified(0) // Unverified!
                .build();

        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> authService.loginUser(request));
    }

    @Test
    public void testLoginUser_InvalidPassword() {
        LoginRequest request = LoginRequest.builder()
                .usernameOrEmail("john_doe")
                .password("wrongPassword")
                .build();

        User user = User.builder()
                .id(100L)
                .username("john_doe")
                .email("john@example.com")
                .password("hashedPassword")
                .emailVerified(1)
                .build();

        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> authService.loginUser(request));
    }

    @Test
    public void testConfirmEmail_Success() {
        User user = User.builder()
                .id(100L)
                .username("john_doe")
                .email("john@example.com")
                .emailVerified(0)
                .verificationToken("valid-token")
                .build();

        when(userRepository.findByVerificationTokenAndDeletedStatus("valid-token", 0)).thenReturn(Optional.of(user));

        String result = authService.confirmEmail("valid-token");

        assertEquals("Email verified successfully! You can now log in.", result);
        assertEquals(1, user.getEmailVerified());
        assertNull(user.getVerificationToken());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testConfirmEmail_InvalidToken() {
        when(userRepository.findByVerificationTokenAndDeletedStatus("invalid-token", 0)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> authService.confirmEmail("invalid-token"));
    }
}
