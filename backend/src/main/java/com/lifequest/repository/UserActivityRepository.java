package com.lifequest.repository;

import com.lifequest.domain.UserActivity;
import com.lifequest.enums.ActivityCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

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

    @Query("SELECT ua.completedDate, COUNT(ua) as total FROM UserActivity ua " +
           "WHERE ua.user.id = :userId AND ua.completedDate BETWEEN :from AND :to " +
           "GROUP BY ua.completedDate ORDER BY ua.completedDate")
    List<Object[]> dailyActivityCount(@Param("userId") Long userId,
                                      @Param("from") LocalDate from,
                                      @Param("to") LocalDate to);

    @Query("SELECT COALESCE(SUM(ua.durationMinutes), 0) FROM UserActivity ua " +
           "WHERE ua.user.id = :userId AND ua.activity.category = :category")
    long sumDurationByCategory(@Param("userId") Long userId,
                               @Param("category") ActivityCategory category);

    @Query("SELECT COUNT(DISTINCT ua.completedDate) FROM UserActivity ua " +
           "WHERE ua.user.id = :userId AND ua.completedDate BETWEEN :from AND :to")
    long countDistinctActiveDays(@Param("userId") Long userId,
                                 @Param("from") LocalDate from,
                                 @Param("to") LocalDate to);
}