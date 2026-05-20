package com.lifequest.service;

import com.lifequest.enums.ActivityCategory;
import com.lifequest.repository.UserActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final UserActivityRepository activityRepo;

    @Transactional(readOnly = true)
    @Cacheable(value = "userDashboard", key = "#userId")
    public DashboardStats getDashboard(Long userId) {
        LocalDate today     = LocalDate.now();
        LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate yearStart  = today.withDayOfYear(1);

        long xpWeek  = activityRepo.sumXpBetween(userId, weekStart,  today);
        long xpMonth = activityRepo.sumXpBetween(userId, monthStart, today);
        long xpYear  = activityRepo.sumXpBetween(userId, yearStart,  today);

        long activeDaysMonth = activityRepo.countDistinctActiveDays(userId, monthStart, today);

        List<Object[]> categoryData = activityRepo.countByCategory(userId);
        String dominantCategory = categoryData.isEmpty() ? null
            : ((ActivityCategory) categoryData.get(0)[0]).getLabel();

        LocalDate heatmapStart = today.minusDays(89);
        List<Object[]> heatmapData = activityRepo.dailyActivityCount(userId, heatmapStart, today);
        Map<LocalDate, Long> heatmap = heatmapData.stream()
            .collect(Collectors.toMap(
                row -> (LocalDate) row[0],
                row -> (Long) row[1]));

        List<Object[]> hourData = activityRepo.mostProductiveHours(userId);
        Integer mostProductiveHour = hourData.isEmpty() ? null
            : ((Number) hourData.get(0)[0]).intValue();

        return new DashboardStats(
            xpWeek, xpMonth, xpYear,
            activeDaysMonth, dominantCategory,
            mostProductiveHour, heatmap
        );
    }

    public record DashboardStats(
        long xpWeek,
        long xpMonth,
        long xpYear,
        long activeDaysThisMonth,
        String dominantCategory,
        Integer mostProductiveHour,
        Map<LocalDate, Long> activityHeatmap
    ) {}
}