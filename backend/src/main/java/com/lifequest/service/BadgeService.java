package com.lifequest.service;

import com.lifequest.domain.*;
import com.lifequest.enums.ActivityCategory;
import com.lifequest.enums.BadgeType;
import com.lifequest.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BadgeService {

    private final BadgeRepository      badgeRepo;
    private final UserBadgeRepository  userBadgeRepo;
    private final UserActivityRepository activityRepo;

    @Transactional
    public List<Badge> checkAndAward(User user, UserActivity lastActivity) {
        Set<BadgeType> alreadyEarned = userBadgeRepo.findBadgeTypesByUserId(user.getId());
        List<Badge> newBadges = new ArrayList<>();

        if (!alreadyEarned.contains(BadgeType.FIRST_ACTIVITY)) {
            long total = activityRepo.countByUserAndDate(user.getId(), lastActivity.getCompletedDate());
            if (total >= 1) award(user, BadgeType.FIRST_ACTIVITY, newBadges);
        }

        checkStreak(user, alreadyEarned, newBadges, BadgeType.STREAK_7,   7);
        checkStreak(user, alreadyEarned, newBadges, BadgeType.STREAK_30,  30);
        checkStreak(user, alreadyEarned, newBadges, BadgeType.STREAK_100, 100);

        if (!alreadyEarned.contains(BadgeType.WORKOUTS_20)) {
            long fitnessCount = activityRepo.countByUserAndCategory(user.getId(), ActivityCategory.HEALTH);
            if (fitnessCount >= 20) award(user, BadgeType.WORKOUTS_20, newBadges);
        }

        if (!alreadyEarned.contains(BadgeType.STUDY_SESSIONS_30)) {
            long studyCount = activityRepo.countByUserAndCategory(user.getId(), ActivityCategory.STUDY);
            if (studyCount >= 30) award(user, BadgeType.STUDY_SESSIONS_30, newBadges);
        }

        if (!alreadyEarned.contains(BadgeType.STUDY_50H)) {
            long studyMinutes = activityRepo.sumDurationByCategory(user.getId(), ActivityCategory.STUDY);
            if (studyMinutes >= 3000) award(user, BadgeType.STUDY_50H, newBadges);
        }

        if (!alreadyEarned.contains(BadgeType.WORK_TASKS_100)) {
            long workCount = activityRepo.countByUserAndCategory(user.getId(), ActivityCategory.WORK);
            if (workCount >= 100) award(user, BadgeType.WORK_TASKS_100, newBadges);
        }

        checkLevel(user, alreadyEarned, newBadges, BadgeType.LEVEL_10, 10);
        checkLevel(user, alreadyEarned, newBadges, BadgeType.LEVEL_25, 25);
        checkLevel(user, alreadyEarned, newBadges, BadgeType.LEVEL_50, 50);

        if (!newBadges.isEmpty()) {
            log.info("User {} earned {} badge(s): {}", 
                user.getUsername(), newBadges.size(),
                newBadges.stream().map(Badge::getTitle).toList());
        }

        return newBadges;
    }

    private void checkStreak(User user, Set<BadgeType> earned, List<Badge> result, BadgeType type, int required) {
        if (!earned.contains(type) && user.getCurrentStreak() >= required) {
            award(user, type, result);
        }
    }

    private void checkLevel(User user, Set<BadgeType> earned, List<Badge> result, BadgeType type, int required) {
        if (!earned.contains(type) && user.getLevel() >= required) {
            award(user, type, result);
        }
    }

    private void award(User user, BadgeType type, List<Badge> result) {
        badgeRepo.findByBadgeType(type).ifPresent(badge -> {
            UserBadge userBadge = UserBadge.builder()
                .user(user)
                .badge(badge)
                .build();
            userBadgeRepo.save(userBadge);
            result.add(badge);
        });
    }
}