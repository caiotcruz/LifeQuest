package com.lifequest.controller;

import com.lifequest.dto.*;
import com.lifequest.security.SecurityUtils;
import com.lifequest.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(userService.getProfile(userId));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest req) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(userService.updateProfile(userId, req));
    }

    @GetMapping("/streak")
    public ResponseEntity<StreakInfoResponse> getStreak() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(userService.getStreakInfo(userId));
    }

    @PostMapping("/streak/recover")
    public ResponseEntity<Map<String, Object>> recoverStreak() {
        Long userId = SecurityUtils.getCurrentUserId();
        boolean success = userService.recoverStreak(userId);
        
        return ResponseEntity.ok(Map.of(
            "success", success,
            "message", success ? "Streak recuperada com sucesso via Redis!" : "Limite de recuperações mensais atingido."
        ));
    }
}