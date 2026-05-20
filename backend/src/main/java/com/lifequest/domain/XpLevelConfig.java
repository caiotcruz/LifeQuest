package com.lifequest.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "xp_level_config")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class XpLevelConfig {

    @Id
    @Column(name = "level")
    private Integer level;

    @Column(name = "xp_required", nullable = false)
    private Long xpRequired;

    @Column(name = "title", length = 50)
    private String title;
}