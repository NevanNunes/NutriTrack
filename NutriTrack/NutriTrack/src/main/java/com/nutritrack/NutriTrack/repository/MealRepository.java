package com.nutritrack.NutriTrack.repository;

import com.nutritrack.NutriTrack.entity.User;
import com.nutritrack.NutriTrack.entity.Meal;
import com.nutritrack.NutriTrack.entity.DietRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// ==================== MEAL REPOSITORY ====================
@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {

    /**
     * Find all meals for a user ordered by log date (most recent first)
     * @param userId the user ID
     * @return list of meals ordered by date descending
     */
    List<Meal> findByUserIdOrderByLogDateDesc(Long userId);

    /**
     * Find all meals for a user on a specific date
     * @param userId the user ID
     * @param date the specific date
     * @return list of meals for that date
     */
    List<Meal> findByUserIdAndLogDate(Long userId, LocalDate date);

    /**
     * Find all meals for a user within a date range
     * @param userId the user ID
     * @param start the start date (inclusive)
     * @param end the end date (inclusive)
     * @return list of meals within the date range
     */
    List<Meal> findByUserIdAndLogDateBetween(Long userId, LocalDate start, LocalDate end);

    /**
     * Calculate total calories consumed by a user on a specific date
     * @param userId the user ID
     * @param date the specific date
     * @return total calories for the day, or 0 if no meals logged
     */
    @Query("SELECT COALESCE(SUM(m.calories), 0) FROM Meal m WHERE m.user.id = :userId AND m.logDate = :date")
    Double getTotalCaloriesByDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    /**
     * Calculate total protein consumed by a user on a specific date
     * @param userId the user ID
     * @param date the specific date
     * @return total protein in grams for the day
     */
    @Query("SELECT COALESCE(SUM(m.proteinG), 0) FROM Meal m WHERE m.user.id = :userId AND m.logDate = :date")
    Double getTotalProteinByDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    /**
     * Calculate total carbs consumed by a user on a specific date
     * @param userId the user ID
     * @param date the specific date
     * @return total carbs in grams for the day
     */
    @Query("SELECT COALESCE(SUM(m.carbsG), 0) FROM Meal m WHERE m.user.id = :userId AND m.logDate = :date")
    Double getTotalCarbsByDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    /**
     * Calculate total fat consumed by a user on a specific date
     * @param userId the user ID
     * @param date the specific date
     * @return total fat in grams for the day
     */
    @Query("SELECT COALESCE(SUM(m.fatG), 0) FROM Meal m WHERE m.user.id = :userId AND m.logDate = :date")
    Double getTotalFatByDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    /**
     * Calculate average daily calories over a date range
     * @param userId the user ID
     * @param start the start date
     * @param end the end date
     * @return average calories per day
     */
    @Query("SELECT AVG(daily_calories) FROM (" +
            "SELECT SUM(m.calories) as daily_calories " +
            "FROM Meal m " +
            "WHERE m.user.id = :userId AND m.logDate BETWEEN :start AND :end " +
            "GROUP BY m.logDate" +
            ") as daily_totals")
    Double getAverageDailyCalories(@Param("userId") Long userId, @Param("start") LocalDate start, @Param("end") LocalDate end);

    /**
     * Count meals logged by a user on a specific date
     * @param userId the user ID
     * @param date the specific date
     * @return number of meals logged
     */
    @Query("SELECT COUNT(m) FROM Meal m WHERE m.user.id = :userId AND m.logDate = :date")
    long countMealsByDate(@Param("userId") Long userId, @Param("date") LocalDate date);
}
