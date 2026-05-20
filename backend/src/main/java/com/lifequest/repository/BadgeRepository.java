package com.lifequest.repository;

import com.lifequest.domain.Badge;
import com.lifequest.enums.BadgeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {
    Optional<Badge> findByBadgeType(BadgeType type);
}