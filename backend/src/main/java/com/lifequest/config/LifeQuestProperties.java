package com.lifequest.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "lifequest")
public class LifeQuestProperties {

    private final Jwt jwt = new Jwt();
    private final Xp xp = new Xp();
    private final Streak streak = new Streak();

    @Getter
    @Setter
    public static class Jwt {
        private String secret;
        private long expirationMs;
        private long refreshExpirationMs;
    }

    @Getter
    @Setter
    public static class Xp {
        private Map<String, Integer> categories = new HashMap<>();
    }

    @Getter
    @Setter
    public static class Streak {
        private double minimumCompletionRate;
        private int maxRecoveryPerMonth;
    }
}