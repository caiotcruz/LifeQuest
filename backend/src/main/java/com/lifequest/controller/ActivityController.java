package com.lifequest.controller;

import com.lifequest.domain.Activity;
import com.lifequest.domain.UserActivity;
import com.lifequest.dto.CompleteActivityRequest;
import com.lifequest.service.ActivityService;
import com.lifequest.service.ActivityService.ActivityCompletionResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @GetMapping("/activities")
    public ResponseEntity<List<Activity>> listPredefined() {
        return ResponseEntity.ok(activityService.getAllPredefined());
    }

    @GetMapping("/me/activities/today")
    public ResponseEntity<List<UserActivity>> todayActivities(
        @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(activityService.getTodayActivities(userId));
    }

    @PostMapping("/me/activities/complete")
    public ResponseEntity<ActivityCompletionResult> complete(
        @AuthenticationPrincipal UserDetails userDetails,
        @Valid @RequestBody CompleteActivityRequest request) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(activityService.complete(userId, request));
    }

    private Long getUserId(UserDetails userDetails) {
        if (userDetails instanceof com.lifequest.security.UserPrincipal) {
            return ((com.lifequest.security.UserPrincipal) userDetails).getId();
        }
        throw new IllegalStateException("O objeto UserDetails não é uma instância de UserPrincipal válida");
    }
}