package com.nutritrack.NutriTrack.repository;

import com.nutritrack.NutriTrack.entity.DietRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DietRecommendationRepository extends JpaRepository<DietRecommendation, Long> {

    /**
     * Find all recommendations for a user ordered by creation date (most recent first)
     * @param userId the user ID
     * @return list of recommendations ordered by date descending
     */
    List<DietRecommendation> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Find the most recent recommendation for a user
     * @param userId the user ID
     * @return Optional containing the most recent recommendation
     */
    Optional<DietRecommendation> findFirstByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Find recommendations for a user with a minimum health score
     * @param userId the user ID
     * @param minScore the minimum health score threshold
     * @return list of recommendations meeting the score threshold
     */
    @Query("SELECT r FROM DietRecommendation r WHERE r.user.id = :userId AND r.healthScore >= :minScore ORDER BY r.createdAt DESC")
    List<DietRecommendation> findByUserIdAndHealthScoreGreaterThan(@Param("userId") Long userId, @Param("minScore") Integer minScore);

    /**
     * Count total recommendations for a user
     * @param userId the user ID
     * @return number of recommendations
     */
    @Query("SELECT COUNT(r) FROM DietRecommendation r WHERE r.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    /**
     * Get average health score for a user
     * @param userId the user ID
     * @return average health score
     */
    @Query("SELECT AVG(r.healthScore) FROM DietRecommendation r WHERE r.user.id = :userId")
    Double getAverageHealthScore(@Param("userId") Long userId);
}