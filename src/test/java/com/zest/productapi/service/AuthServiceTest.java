package com.zest.productapi.service;

import com.zest.productapi.dto.AuthRequest;
import com.zest.productapi.dto.RegisterRequest;
import com.zest.productapi.dto.TokenResponse;
import com.zest.productapi.entity.Role;
import com.zest.productapi.entity.User;
import com.zest.productapi.repository.UserRepository;
import com.zest.productapi.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_shouldSaveEncodedUser() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("ravi");
        request.setEmail("ravi@example.com");
        request.setPassword("secret");
        when(passwordEncoder.encode("secret")).thenReturn("encoded");
        when(userRepository.existsByUsername("ravi")).thenReturn(false);
        when(userRepository.existsByEmail("ravi@example.com")).thenReturn(false);

        authService.register(request);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void login_shouldReturnTokens() {
        AuthRequest request = new AuthRequest();
        request.setUsername("ravi");
        request.setPassword("secret");
        User user = new User(1L, "ravi", "ravi@example.com", "encoded", Role.ROLE_USER);
        when(userRepository.findByUsername("ravi")).thenReturn(Optional.of(user));
        when(jwtService.createAccessToken(user)).thenReturn("access");
        when(jwtService.createRefreshToken(user)).thenReturn("refresh");

        TokenResponse result = authService.login(request);

        assertEquals("access", result.getAccessToken());
        assertEquals("refresh", result.getRefreshToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void login_shouldRequireExistingUser() {
        AuthRequest request = new AuthRequest();
        request.setUsername("missing");
        request.setPassword("secret");
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(
                RuntimeException.class, () -> authService.login(request));
    }
}
