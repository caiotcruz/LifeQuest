package com.lifequest.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_badges",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_user_badge", columnNames = {"user_id", "badge_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserBadge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_ub_user"))
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "badge_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_ub_badge"))
    private Badge badge;

    @Column(name = "earned_at", nullable = false)
    @Builder.Default
    private LocalDateTime earnedAt = LocalDateTime.now();
}