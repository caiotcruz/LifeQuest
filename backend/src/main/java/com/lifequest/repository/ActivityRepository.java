package com.lifequest.repository;

import com.lifequest.domain.Activity;
import com.lifequest.enums.ActivityCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findByIsPredefinedTrue();

    List<Activity> findByIsPredefinedTrueAndIsActiveTrue();

    List<Activity> findByCategory(ActivityCategory category);

    List<Activity> findByCategoryAndIsPredefinedTrue(ActivityCategory category);
}