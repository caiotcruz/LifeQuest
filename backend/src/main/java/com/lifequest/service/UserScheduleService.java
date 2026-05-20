package com.lifequest.service;

import com.lifequest.domain.Activity;
import com.lifequest.domain.User;
import com.lifequest.domain.UserSchedule;
import com.lifequest.dto.ScheduleRequest;
import com.lifequest.exception.NotFoundException;
import com.lifequest.repository.ActivityRepository;
import com.lifequest.repository.UserRepository;
import com.lifequest.repository.UserScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserScheduleService {

    private final UserScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;
    private final StreakService streakService;

    @Transactional
    public UserSchedule createSchedule(Long userId, ScheduleRequest req) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        Activity activity = activityRepository.findById(req.activityId())
            .orElseThrow(() -> new NotFoundException("Atividade não encontrada"));

        UserSchedule schedule = UserSchedule.builder()
            .user(user)
            .activity(activity)
            .recurrenceType(req.recurrenceType())
            .daysOfWeek(req.daysOfWeek() != null ? req.daysOfWeek() : java.util.Collections.emptySet())
            .daysOfMonth(req.daysOfMonth() != null ? req.daysOfMonth() : java.util.Collections.emptySet())
            .startDate(req.startDate())
            .endDate(req.endDate())
            .isActive(true)
            .build();

        UserSchedule saved = scheduleRepository.save(schedule);
        
        // Sincroniza o planejamento do dia atual no Redis para o cálculo de consistência (streak)
        updateRedisPlannedCountForToday(userId);

        log.info("Novo agendamento criado ID {} para o usuário {}", saved.getId(), user.getUsername());
        return saved;
    }

    @Transactional(readOnly = true)
    public List<UserSchedule> getActiveSchedules(Long userId) {
        return scheduleRepository.findAllActiveByUserId(userId);
    }

    private void updateRedisPlannedCountForToday(Long userId) {
        LocalDate today = LocalDate.now();
        List<UserSchedule> activeSchedules = scheduleRepository.findAllActiveByUserId(userId);
        
        int plannedCount = 0;
        for (UserSchedule s : activeSchedules) {
            if (isScheduledForDate(s, today)) {
                plannedCount++;
            }
        }
        streakService.setDailyPlannedCount(userId, today, plannedCount);
    }

    private boolean isScheduledForDate(UserSchedule schedule, LocalDate date) {
        if (date.isBefore(schedule.getStartDate())) return false;
        if (schedule.getEndDate() != null && date.isAfter(schedule.getEndDate())) return false;

        return switch (schedule.getRecurrenceType()) {
            case ONCE -> schedule.getStartDate().isEqual(date);
            case DAILY -> true;
            case WEEKLY -> schedule.getDaysOfWeek().contains(date.getDayOfWeek());
            case MONTHLY -> schedule.getDaysOfMonth().contains(date.getDayOfMonth());
        };
    }
}