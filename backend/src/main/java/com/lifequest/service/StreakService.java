package com.lifequest.service;

import com.lifequest.domain.User;
import com.lifequest.repository.UserRepository;
import com.lifequest.repository.UserActivityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StreakService {

    private final UserRepository userRepository;
    private final UserActivityRepository activityRepository;
    private final StringRedisTemplate redisTemplate;

    @Value("${lifequest.streak.minimum-completion-rate:0.60}")
    private double minimumCompletionRate;

    @Value("${lifequest.streak.max-recovery-per-month:2}")
    private int maxRecoveryPerMonth;

    private static final String STREAK_KEY      = "streak:user:%d";
    private static final String SCHEDULE_COUNT  = "schedule:count:user:%d:date:%s";

    @Transactional
    public void checkAndUpdateStreak(User user, LocalDate date) {
        String scheduleKey = String.format(SCHEDULE_COUNT, user.getId(), date);
        
        String totalPlannedStr = redisTemplate.opsForValue().get(scheduleKey + ":planned");
        int totalPlanned = totalPlannedStr != null ? Integer.parseInt(totalPlannedStr) : 0;
        
        long totalCompleted = activityRepository.countByUserAndDate(user.getId(), date);
        
        if (totalPlanned == 0) {
            if (totalCompleted >= 1) {
                incrementStreak(user, date);
            }
        } else {
            double completionRate = (double) totalCompleted / totalPlanned;
            if (completionRate >= minimumCompletionRate) {
                incrementStreak(user, date);
            }
        }
    }

    private void incrementStreak(User user, LocalDate today) {
        String redisKey = String.format(STREAK_KEY, user.getId());
        String lastDateStr = redisTemplate.opsForValue().get(redisKey + ":lastDate");

        LocalDate lastStreakDate = lastDateStr != null
            ? LocalDate.parse(lastDateStr)
            : null;

        if (lastStreakDate == null || lastStreakDate.isBefore(today.minusDays(1))) {
            user.setCurrentStreak(1);
        } else if (lastStreakDate.isEqual(today.minusDays(1))) {
            user.incrementStreak();
        }

        redisTemplate.opsForValue().set(redisKey + ":lastDate", today.toString(), Duration.ofDays(2));
        userRepository.save(user);

        log.debug("Streak updated for user {}: {} days", user.getUsername(), user.getCurrentStreak());
    }

    @Transactional
    public boolean recoverStreak(User user) {
        if (user.getStreakRecoveryUsedThisMonth() >= maxRecoveryPerMonth) {
            log.warn("User {} exhausted streak recoveries for this month", user.getUsername());
            return false;
        }

        user.setCurrentStreak(user.getCurrentStreak() + 1);
        user.setStreakRecoveryCount(user.getStreakRecoveryCount() + 1);
        user.setStreakRecoveryUsedThisMonth(user.getStreakRecoveryUsedThisMonth() + 1);
        userRepository.save(user);

        log.info("Streak recovered for user {}", user.getUsername());
        return true;
    }

    @Scheduled(cron = "0 0 0 1 * *")
    @Transactional
    public void resetMonthlyRecoveries() {
        log.info("Resetting monthly streak recoveries counter...");
        userRepository.findAll().forEach(user -> user.setStreakRecoveryUsedThisMonth(0));
        userRepository.saveAll(userRepository.findAll());
    }

    @Scheduled(cron = "0 5 0 * * *")
    @Transactional
    public void checkBrokenStreaks() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        log.info("Checking broken streaks for {}", yesterday);

        List<User> users = userRepository.findAll();
        for (User user : users) {
            String redisKey = String.format(STREAK_KEY, user.getId()) + ":lastDate";
            String lastDateStr = redisTemplate.opsForValue().get(redisKey);

            if (lastDateStr == null) continue;

            LocalDate lastDate = LocalDate.parse(lastDateStr);
            if (lastDate.isBefore(yesterday)) {
                user.resetStreak();
                userRepository.save(user);
                log.debug("Streak reset for user {}", user.getUsername());
            }
        }
    }

    public void setDailyPlannedCount(Long userId, LocalDate date, int count) {
        String key = String.format(SCHEDULE_COUNT, userId, date) + ":planned";
        redisTemplate.opsForValue().set(key, String.valueOf(count), Duration.ofDays(2));
    }
}