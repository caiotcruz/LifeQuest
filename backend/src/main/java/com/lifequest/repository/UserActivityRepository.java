package com.lifequest.repository;

import com.lifequest.domain.UserActivity;
import com.lifequest.enums.ActivityCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {

    List<UserActivity> findByUserIdAndCompletedDate(Long userId, LocalDate date);

    @Query("SELECT COUNT(ua) FROM UserActivity ua WHERE ua.user.id = :userId AND ua.completedDate = :date")
    long countByUserAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("SELECT COALESCE(SUM(ua.xpEarned), 0) FROM UserActivity ua " +
            "WHERE ua.user.id = :userId AND ua.completedDate BETWEEN :from AND :to")
    long sumXpBetween(@Param("userId") Long userId,
                      @Param("from") LocalDate from,
                      @Param("to") LocalDate to);

    @Query("SELECT COUNT(ua) FROM UserActivity ua " +
            "WHERE ua.user.id = :userId AND ua.activity.category = :category")
    long countByUserAndCategory(@Param("userId") Long userId,
                                @Param("category") ActivityCategory category);

    @Query("SELECT ua.activity.category, COUNT(ua) as total " +
            "FROM UserActivity ua WHERE ua.user.id = :userId " +
            "GROUP BY ua.activity.category ORDER BY total DESC")
    List<Object[]> countByCategory(@Param("userId") Long userId);

    @Query("SELECT EXTRACT(HOUR FROM ua.completedAt) as hour, COUNT(ua) as total " +
            "FROM UserActivity ua WHERE ua.user.id = :userId " +
            "GROUP BY hour ORDER BY total DESC")
    List<Object[]> mostProductiveHours(@Param("userId") Long userId);

    @Query("SELECT FUNCTION('DATE', ua.completedAt), COUNT(ua) as total FROM UserActivity ua " +
            "WHERE ua.user.id = :userId AND ua.completedAt BETWEEN :from AND :to " +
            "GROUP BY FUNCTION('DATE', ua.completedAt) ORDER BY FUNCTION('DATE', ua.completedAt)")
    List<Object[]> dailyActivityCount(@Param("userId") Long userId,
                                      @Param("from") LocalDateTime from,
                                      @Param("to") LocalDateTime to);

    @Query("SELECT COALESCE(SUM(ua.durationMinutes), 0) FROM UserActivity ua " +
            "WHERE ua.user.id = :userId AND ua.activity.category = :category")
    long sumDurationByCategory(@Param("userId") Long userId,
                               @Param("category") ActivityCategory category);

    @Query("SELECT COUNT(DISTINCT ua.completedDate) FROM UserActivity ua " +
            "WHERE ua.user.id = :userId AND ua.completedDate BETWEEN :from AND :to")
    long countDistinctActiveDays(@Param("userId") Long userId,
                                 @Param("from") LocalDate from,
                                 @Param("to") LocalDate to);

    @Query("SELECT new map(FUNCTION('DATE', ua.completedAt) as date, SUM(ua.activity.xpReward) as totalXp) " +
           "FROM UserActivity ua " +
           "WHERE ua.user.id = :userId AND ua.completedAt >= :startDate " +
           "GROUP BY FUNCTION('DATE', ua.completedAt)")
    List<Map<String, Object>> findXpPerDaySince(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT FUNCTION('DATE', ua.completedAt), SUM(ua.activity.xpReward) " +
           "FROM UserActivity ua " +
           "WHERE ua.user.id = :userId AND ua.completedAt >= :startDate AND ua.completedAt <= :endDate " +
           "GROUP BY FUNCTION('DATE', ua.completedAt)")
    List<Object[]> dailyXpSum(@Param("userId") Long userId, 
                              @Param("startDate") LocalDateTime startDate, 
                              @Param("endDate") LocalDateTime endDate);
}