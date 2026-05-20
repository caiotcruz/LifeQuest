package com.lifequest.service;

import com.lifequest.enums.ActivityCategory;
import com.lifequest.repository.UserActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final UserActivityRepository activityRepo;

    @Transactional(readOnly = true)
    @Cacheable(value = "userDashboard", key = "#userId")
    public DashboardStats getDashboard(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate yearStart = today.withDayOfYear(1);

        long xpWeek = activityRepo.sumXpBetween(userId, weekStart, today);
        long xpMonth = activityRepo.sumXpBetween(userId, monthStart, today);
        long xpYear = activityRepo.sumXpBetween(userId, yearStart, today);

        long activeDaysMonth = activityRepo.countDistinctActiveDays(userId, monthStart, today);

        List<Object[]> categoryData = activityRepo.countByCategory(userId);
        String dominantCategory = categoryData.isEmpty() ? null
            : ((ActivityCategory) categoryData.get(0)[0]).getLabel();

        // ─── Heatmap (Blindado para java.sql.Date) ───
        LocalDate heatmapStart = today.minusDays(89);
        List<Object[]> heatmapData = activityRepo.dailyActivityCount(userId, heatmapStart.atStartOfDay(), today.atTime(23, 59, 59));
        
        List<HeatmapEntry> heatmap = heatmapData.stream()
            .map(row -> {
                LocalDate date = (row[0] instanceof java.sql.Date) ? ((java.sql.Date) row[0]).toLocalDate() : (LocalDate) row[0];
                return new HeatmapEntry(date, (Long) row[1]);
            })
            .collect(Collectors.toList());

        List<Object[]> hourData = activityRepo.mostProductiveHours(userId);
        Integer mostProductiveHour = hourData.isEmpty() ? null
            : ((Number) hourData.get(0)[0]).intValue();

        // ─── LÓGICA DO GRÁFICO: ÚLTIMOS 7 DIAS (Blindado para java.sql.Date) ───
        LocalDate sevenDaysAgo = today.minusDays(6);
        LocalDateTime startDateTime = sevenDaysAgo.atStartOfDay();
        LocalDateTime endDateTime = today.atTime(23, 59, 59);

        List<Object[]> dailyXpData = activityRepo.dailyXpSum(userId, startDateTime, endDateTime);
        
        Map<LocalDate, Integer> dailyXpMap = dailyXpData.stream()
            .collect(Collectors.toMap(
                row -> (row[0] instanceof java.sql.Date) ? ((java.sql.Date) row[0]).toLocalDate() : (LocalDate) row[0],
                row -> ((Number) row[1]).intValue(),
                (existing, replacement) -> existing
            ));

        List<String> labels = new ArrayList<>();
        List<Integer> xpValues = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE", new Locale("pt", "BR"));

        for (int i = 6; i >= 0; i--) {
            LocalDate d = today.minusDays(i);
            String dayName = d.format(formatter);
            dayName = dayName.substring(0, 1).toUpperCase() + dayName.substring(1).replace(".", "");
            labels.add(dayName);
            xpValues.add(dailyXpMap.getOrDefault(d, 0));
        }
        
        WeeklyChartData chartData = new WeeklyChartData(labels, xpValues);

        return new DashboardStats(
            xpWeek, xpMonth, xpYear,
            activeDaysMonth, dominantCategory,
            mostProductiveHour, heatmap, chartData
        );
    }

    public record DashboardStats(
        long xpWeek,
        long xpMonth,
        long xpYear,
        long activeDaysThisMonth,
        String dominantCategory,
        Integer mostProductiveHour,
        List<HeatmapEntry> activityHeatmap,
        WeeklyChartData weeklyChart
    ) {}

    public record HeatmapEntry(LocalDate date, Long count) {}

    public record WeeklyChartData(
        List<String> labels,
        List<Integer> xpValues
    ) {}
}