package com.lifequest.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.util.HashMap;
import java.util.Map;

@Data
@Component 
@ConfigurationProperties(prefix = "lifequest")
public class LifeQuestProperties {
    
    private Jwt jwt = new Jwt();
    private Xp xp = new Xp();
    private Streak streak = new Streak();

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