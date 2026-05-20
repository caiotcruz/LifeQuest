package com.lifequest.controller;

import com.lifequest.service.AnalyticsService;
import com.lifequest.service.AnalyticsService.DashboardStats;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStats> getDashboard(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        DashboardStats stats = analyticsService.getDashboard(userId);
        return ResponseEntity.ok(stats);
    }

    private Long getUserId(UserDetails userDetails) {
        if (userDetails instanceof com.lifequest.security.UserPrincipal) {
            return ((com.lifequest.security.UserPrincipal) userDetails).getId();
        }
        throw new IllegalStateException("Contexto de autenticação inválido.");
    }
}