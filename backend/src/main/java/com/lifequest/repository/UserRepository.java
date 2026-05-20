package com.lifequest.repository;

import com.lifequest.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    // Unificado e corrigido usando os nomes de parâmetros corretos do JPQL
    @Query("SELECT u FROM User u WHERE u.email = :usernameOrEmail OR u.username = :usernameOrEmail")
    Optional<User> findByEmailOrUsername(@Param("usernameOrEmail") String usernameOrEmail);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    @Modifying
    @Query("UPDATE User u SET u.totalXp = u.totalXp + :xp, u.level = :level WHERE u.id = :id")
    void updateXpAndLevel(@Param("id") Long id,
                          @Param("xp") int xp,
                          @Param("level") int level);

    @Modifying
    @Query("UPDATE User u SET u.currentStreak = :streak, u.longestStreak = " +
           "CASE WHEN :streak > u.longestStreak THEN :streak ELSE u.longestStreak END " +
           "WHERE u.id = :id")
    void updateStreak(@Param("id") Long id, @Param("streak") int streak);
}