package com.lifequest.dto;

import com.lifequest.domain.User;

public record UserProfileResponse(
    Long id,
    String username,
    String email,
    String avatar,
    int level,
    long totalXp,
    int currentStreak,
    int longestStreak,
    String createdAt
) {
    public static UserProfileResponse from(User u) {
        return new UserProfileResponse(
            u.getId(),
            u.getUsername(),
            u.getEmail(),
            u.getAvatar(),
            u.getLevel(),
            u.getTotalXp(),
            u.getCurrentStreak(),
            u.getLongestStreak(),
            u.getCreatedAt() != null ? u.getCreatedAt().toString() : null
        );
    }
}