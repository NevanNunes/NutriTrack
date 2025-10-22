package com.nutritrack.NutriTrack.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.time.LocalDateTime;

// ==================== DIET RECOMMENDATION ENTITY ====================
@Entity
@Table(name = "recommendations", indexes = {
        @Index(name = "idx_recommendation_user_id", columnList = "user_id"),
        @Index(name = "idx_health_score", columnList = "health_score")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "user")
public class DietRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_recommendation_user"))
    @JsonBackReference
    @ToString.Exclude
    private User user;

    @Column(name = "recommendation_text", columnDefinition = "TEXT", nullable = false)
    private String recommendationText;

    @Column(name = "health_score", nullable = false)
    private Integer healthScore;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ==================== VALIDATION METHODS ====================

    /**
     * Validate health score is within valid range (0-100)
     */
    @PrePersist
    @PreUpdate
    private void validateHealthScore() {
        if (healthScore == null || healthScore < 0 || healthScore > 100) {
            throw new IllegalArgumentException("Health score must be between 0 and 100");
        }
    }

    /**
     * Get health score category
     */
    public String getHealthScoreCategory() {
        if (healthScore >= 80) {
            return "EXCELLENT";
        } else if (healthScore >= 60) {
            return "GOOD";
        } else if (healthScore >= 40) {
            return "FAIR";
        } else {
            return "POOR";
        }
    }

    @Override
    public String toString() {
        return "DietRecommendation{" +
                "id=" + id +
                ", healthScore=" + healthScore +
                ", category='" + getHealthScoreCategory() + '\'' +
                ", recommendationText='" + (recommendationText.length() > 50 ?
                recommendationText.substring(0, 50) + "..." : recommendationText) + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}