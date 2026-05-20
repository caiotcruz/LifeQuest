package com.lifequest.controller;

import com.lifequest.domain.UserSchedule;
import com.lifequest.dto.ScheduleRequest;
import com.lifequest.service.UserScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class UserScheduleController {

    private final UserScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<UserSchedule> create(@AuthenticationPrincipal UserDetails userDetails,
                                               @Valid @RequestBody ScheduleRequest request) {
        Long userId = getUserId(userDetails);
        UserSchedule schedule = scheduleService.createSchedule(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(schedule);
    }

    @GetMapping
    public ResponseEntity<List<UserSchedule>> listActive(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(scheduleService.getActiveSchedules(userId));
    }

    private Long getUserId(UserDetails userDetails) {
        if (userDetails instanceof com.lifequest.security.UserPrincipal) {
            return ((com.lifequest.security.UserPrincipal) userDetails).getId();
        }
        throw new IllegalStateException("Contexto de autenticação inválido.");
    }
}