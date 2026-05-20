package com.lifequest.controller;

import com.lifequest.domain.UserSchedule;
import com.lifequest.dto.ScheduleRequest;
import com.lifequest.security.SecurityUtils;
import com.lifequest.service.UserScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedules") // Mantivemos a sua rota original!
@RequiredArgsConstructor
public class UserScheduleController {

    private final UserScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<UserSchedule> create(@Valid @RequestBody ScheduleRequest request) {
        // Usando a nossa utilidade estática para pegar o ID limpo direto do JWT
        Long userId = SecurityUtils.getCurrentUserId();
        UserSchedule schedule = scheduleService.createSchedule(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(schedule);
    }

    @GetMapping
    public ResponseEntity<List<UserSchedule>> listActive() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(scheduleService.getActiveSchedules(userId));
    }
}