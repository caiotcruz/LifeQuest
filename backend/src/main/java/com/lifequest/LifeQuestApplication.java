package com.lifequest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableConfigurationProperties(com.lifequest.config.LifeQuestProperties.class) 
public class LifeQuestApplication {

    public static void main(String[] args) {
        SpringApplication.run(LifeQuestApplication.class, args);
    }
}