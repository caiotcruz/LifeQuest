package com.lifequest.service;

import com.lifequest.domain.User;
import com.lifequest.dto.*;
import com.lifequest.exception.ConflictException;
import com.lifequest.repository.UserRepository;
import com.lifequest.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository       userRepository;
    private final PasswordEncoder      passwordEncoder;
    private final JwtTokenProvider      tokenProvider;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Email já cadastrado");
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new ConflictException("Username já em uso");
        }

        User user = User.builder()
            .username(request.username())
            .email(request.email())
            .passwordHash(passwordEncoder.encode(request.password()))
            .build();

        userRepository.save(user);
        log.info("Novo usuário registrado: {}", user.getUsername());

        return buildAuthResponse(user.getEmail(), user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.emailOrUsername(), request.password())
        );

        // Ajustado de (req.emailOrUsername(), req.emailOrUsername()) para apenas um parâmetro
        User user = userRepository
            .findByEmailOrUsername(request.emailOrUsername())
            .orElseThrow();

        log.info("Login realizado: {}", user.getUsername());
        return buildAuthResponse(auth.getName(), user);
    }

    private AuthResponse buildAuthResponse(String principal, User user) {
        String accessToken  = tokenProvider.generateTokenFromUsername(principal);
        String refreshToken = tokenProvider.generateRefreshToken(principal);

        UserSummaryResponse userSummary = new UserSummaryResponse(
            user.getId(), user.getUsername(), user.getEmail(),
            user.getAvatar(), user.getLevel(),
            user.getTotalXp(), user.getCurrentStreak()
        );

        return AuthResponse.of(accessToken, refreshToken, 86400000L, userSummary);
    }
}