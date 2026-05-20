package com.lifequest.domain;

import com.lifequest.enums.BadgeType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "badges")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "badge_type", nullable = false, unique = true, length = 50)
    private BadgeType badgeType;

    @Column(nullable = false, length = 80)
    private String title;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(name = "icon_name", length = 50)
    private String iconName;

    @Column(name = "xp_bonus", nullable = false)
    @Builder.Default
    private Integer xpBonus = 0;
}