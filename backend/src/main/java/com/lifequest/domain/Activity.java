package com.lifequest.domain;

import com.lifequest.enums.ActivityCategory;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "activities")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ActivityCategory category;

    @Column(name = "xp_reward", nullable = false)
    private Integer xpReward;

    @Column(name = "icon_name", length = 50)
    private String iconName;

    @Column(name = "is_predefined", nullable = false)
    @Builder.Default
    private Boolean isPredefined = true;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @PrePersist
    public void prePersist() {
        if (this.xpReward == null && this.category != null) {
            this.xpReward = this.category.getBaseXp();
        }
    }
}