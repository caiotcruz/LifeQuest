package com.lifequest.service;

import com.lifequest.domain.User;
import com.lifequest.dto.*;
import com.lifequest.exception.NotFoundException;
import com.lifequest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final StreakService streakService;

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(Long userId) {
        User user = findUser(userId);
        return UserProfileResponse.from(user);
    }

    @Transactional
    public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest req) {
        User user = findUser(userId);
        
        if (req.username() != null && !req.username().isBlank()) {
            user.setUsername(req.username());
        }
        if (req.avatar() != null) {
            user.setAvatar(req.avatar());
        }
        
        userRepository.save(user);
        return UserProfileResponse.from(user);
    }

    @Transactional(readOnly = true)
    public StreakInfoResponse getStreakInfo(Long userId) {
        User user = findUser(userId);
        int maxRecovery = 2; 
        
        return new StreakInfoResponse(
            user.getCurrentStreak(),
            user.getLongestStreak(),
            user.getStreakRecoveryUsedThisMonth(),
            maxRecovery,
            user.getStreakRecoveryUsedThisMonth() < maxRecovery
        );
    }

    @Transactional
    public boolean recoverStreak(Long userId) {
        User user = findUser(userId);
        return streakService.recoverStreak(user);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("Herói não encontrado no banco de dados. ID: " + userId));
    }
}