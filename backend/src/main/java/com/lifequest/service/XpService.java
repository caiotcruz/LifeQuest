package com.lifequest.service;

import com.lifequest.domain.User;
import com.lifequest.domain.XpLevelConfig;
import com.lifequest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class XpService {

    private final UserRepository userRepository;
    private final EntityManager em;

    @Transactional
    public LevelUpResult addXp(User user, int xpToAdd) {
        int previousLevel = user.getLevel();

        user.addXp(xpToAdd);

        int newLevel = calculateLevel(user.getTotalXp());
        boolean leveledUp = newLevel > previousLevel;
        user.setLevel(newLevel);

        userRepository.save(user);

        if (leveledUp) {
            log.info("Level up! Usuário {} subiu do nível {} para {}", 
                user.getUsername(), previousLevel, newLevel);
        }

        return new LevelUpResult(
            leveledUp, previousLevel, newLevel,
            xpToAdd, user.getTotalXp(),
            xpForNextLevel(newLevel),
            xpProgressInCurrentLevel(user.getTotalXp(), newLevel)
        );
    }

    public int calculateLevel(long totalXp) {
        List<XpLevelConfig> configs = getLevelConfigs();

        int level = 1;
        for (XpLevelConfig config : configs) {
            if (totalXp >= config.getXpRequired()) {
                level = config.getLevel();
            } else {
                break;
            }
        }
        return level;
    }

    public long xpForNextLevel(int currentLevel) {
        List<XpLevelConfig> configs = getLevelConfigs();
        return configs.stream()
            .filter(c -> c.getLevel() == currentLevel + 1)
            .findFirst()
            .map(XpLevelConfig::getXpRequired)
            .orElse(Long.MAX_VALUE);
    }

    public long xpProgressInCurrentLevel(long totalXp, int currentLevel) {
        List<XpLevelConfig> configs = getLevelConfigs();
        long xpAtCurrentLevel = configs.stream()
            .filter(c -> c.getLevel() == currentLevel)
            .findFirst()
            .map(XpLevelConfig::getXpRequired)
            .orElse(0L);
        return totalXp - xpAtCurrentLevel;
    }

    @Cacheable("xpLevelConfigs")
    public List<XpLevelConfig> getLevelConfigs() {
        return em.createQuery("SELECT x FROM XpLevelConfig x ORDER BY x.level", XpLevelConfig.class)
            .getResultList();
    }

    public record LevelUpResult(
        boolean leveledUp,
        int previousLevel,
        int newLevel,
        int xpEarned,
        long totalXp,
        long xpForNextLevel,
        long xpProgressInLevel
    ) {}
}