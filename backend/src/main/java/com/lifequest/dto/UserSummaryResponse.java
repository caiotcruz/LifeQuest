package com.lifequest.dto;

public record UserSummaryResponse(
    Long id,
    String username,
    String email,
    String avatar,
    int level,
    long totalXp,
    int currentStreak
) {}