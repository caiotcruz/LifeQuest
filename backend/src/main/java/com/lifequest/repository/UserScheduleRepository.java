package com.lifequest.repository;

import com.lifequest.domain.UserSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserScheduleRepository extends JpaRepository<UserSchedule, Long> {

    @Query("SELECT us FROM UserSchedule us JOIN FETCH us.activity WHERE us.user.id = :userId AND us.isActive = true")
    List<UserSchedule> findAllActiveByUserId(@Param("userId") Long userId);
}