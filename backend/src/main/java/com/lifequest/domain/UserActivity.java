package com.lifequest.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "user_activities",
    indexes = {
        @Index(name = "idx_ua_user_date",     columnList = "user_id, completed_date"),
        @Index(name = "idx_ua_user_activity", columnList = "user_id, activity_id")
    })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
public class UserActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_ua_user"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_ua_activity"))
    private Activity activity;

    @Column(name = "completed_date", nullable = false)
    private LocalDate completedDate;

    @Column(name = "completed_at", nullable = false)
    private LocalDateTime completedAt;

    @Column(name = "xp_earned", nullable = false)
    private Integer xpEarned;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(length = 255)
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public LocalTime getCompletedTime() {
        return completedAt != null ? completedAt.toLocalTime() : null;
    }
}