package com.lifequest.repository;

import com.lifequest.domain.UserBadge;
import com.lifequest.enums.BadgeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {

    List<UserBadge> findByUserId(Long userId);

    @Query("SELECT ub.badge.badgeType FROM UserBadge ub WHERE ub.user.id = :userId")
    Set<BadgeType> findBadgeTypesByUserId(@Param("userId") Long userId);

    boolean existsByUserIdAndBadgeBadgeType(Long userId, BadgeType type);
}