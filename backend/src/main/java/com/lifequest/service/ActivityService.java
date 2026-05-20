package com.lifequest.service;

import com.lifequest.domain.*;
import com.lifequest.dto.CompleteActivityRequest;
import com.lifequest.exception.NotFoundException;
import com.lifequest.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityService {

    private final UserActivityRepository userActivityRepo;
    private final ActivityRepository     activityRepo;
    private final UserRepository         userRepo;
    private final XpService              xpService;
    private final StreakService          streakService;
    private final BadgeService           badgeService;

    @Transactional
    public ActivityCompletionResult complete(Long userId, CompleteActivityRequest req) {
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        Activity activity = activityRepo.findById(req.activityId())
            .orElseThrow(() -> new NotFoundException("Atividade não encontrada"));

        LocalDate completedDate = req.completedDate() != null
            ? req.completedDate()
            : LocalDate.now();

        UserActivity userActivity = UserActivity.builder()
            .user(user)
            .activity(activity)
            .completedDate(completedDate)
            .completedAt(LocalDateTime.now())
            .xpEarned(activity.getXpReward())
            .durationMinutes(req.durationMinutes())
            .notes(req.notes())
            .build();

        userActivityRepo.save(userActivity);

        XpService.LevelUpResult levelUpResult = xpService.addXp(user, activity.getXpReward());

        streakService.checkAndUpdateStreak(user, completedDate);

        List<Badge> newBadges = badgeService.checkAndAward(user, userActivity);

        log.debug("Atividade '{}' concluída por {} (+{} XP)",
            activity.getTitle(), user.getUsername(), activity.getXpReward());

        return new ActivityCompletionResult(
            userActivity.getId(),
            activity.getTitle(),
            activity.getXpReward(),
            levelUpResult,
            user.getCurrentStreak(),
            newBadges
        );
    }

    @Transactional(readOnly = true)
    public List<UserActivity> getTodayActivities(Long userId) {
        return userActivityRepo.findByUserIdAndCompletedDate(userId, LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<Activity> getAllPredefined() {
        return activityRepo.findByIsPredefinedTrue();
    }

    public record ActivityCompletionResult(
        Long userActivityId,
        String activityTitle,
        int xpEarned,
        XpService.LevelUpResult levelUpResult,
        int currentStreak,
        List<Badge> newBadgesEarned
    ) {}
}