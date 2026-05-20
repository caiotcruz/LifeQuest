package com.lifequest.dto;

public record AuthResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    long expiresIn,
    UserSummaryResponse user
) {
    public static AuthResponse of(String access, String refresh, long expMs, UserSummaryResponse user) {
        return new AuthResponse(access, refresh, "Bearer", expMs / 1000, user);
    }
}