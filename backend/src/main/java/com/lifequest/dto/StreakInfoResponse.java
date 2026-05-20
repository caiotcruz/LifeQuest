package com.lifequest.dto;

public record StreakInfoResponse(
    int currentStreak,
    int longestStreak,
    int recoveryUsedThisMonth,
    int maxRecoveryPerMonth,
    boolean canRecover
) {}