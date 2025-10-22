package com.nutritrack.NutriTrack.entity;

import com.nutritrack.NutriTrack.enums.MealType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode; // Added this import
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.time.LocalDate;
import java.time.LocalDateTime;

// ==================== MEAL ENTITY ====================
@Entity
@Table(name = "meal_log", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_log_date", columnList = "log_date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"user"})
@EqualsAndHashCode(exclude = {"user"})
public class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // ==================== EXPLICIT GETTERS ====================
    // Added to resolve compile-time issues with Lombok

    public Long getId() { return id; }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_meal_user"))
    @JsonBackReference
    private User user;

    @Column(name = "meal_name", nullable = false, length = 150)
    private String mealName;

    @Column(name = "meal_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MealType mealType;

    @Column(name = "calories", nullable = false)
    @Positive(message = "Calories must be greater than 0")
    private Double calories;

    @Column(name = "protein_g")
    @Positive(message = "Protein must be greater than 0")
    private Double proteinG;

    @Column(name = "carbs_g")
    @Positive(message = "Carbs must be greater than 0")
    private Double carbsG;

    @Column(name = "fat_g")
    @Positive(message = "Fat must be greater than 0")
    private Double fatG;

    @Column(name = "portion_size_g")
    private Double portionSize;

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public String getMealName() { return mealName; }

    public MealType getMealType() { return mealType; }

    public Double getCalories() { return calories; }

    public Double getProteinG() { return proteinG; }

    public Double getCarbsG() { return carbsG; }

    public Double getFatG() { return fatG; }

    public Double getPortionSize() { return portionSize; } // Added this getter

    public String getNotes() { return notes; } // Added this getter

    public LocalDate getLogDate() { return logDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    // ==================== EXPLICIT SETTERS ====================
    // Added to resolve compile-time issues with Lombok

    public void setUser(User user) {
        this.user = user;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }

    public void setCalories(Double calories) {
        this.calories = calories;
    }

    public void setProteinG(Double proteinG) {
        this.proteinG = proteinG;
    }

    public void setCarbsG(Double carbsG) {
        this.carbsG = carbsG;
    }

    public void setFatG(Double fatG) {
        this.fatG = fatG;
    }

    public void setPortionSize(Double portionSize) {
        this.portionSize = portionSize;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setLogDate(LocalDate logDate) {
        this.logDate = logDate;
    }


    // ==================== CALCULATION METHODS ====================

    /**
     * Get total macronutrients (sum of protein, carbs, and fat in grams)
     */
    public Double getTotalMacros() {
        double total = 0;
        if (proteinG != null) total += proteinG;
        if (carbsG != null) total += carbsG;
        if (fatG != null) total += fatG;
        return total;
    }

    /**
     * Calculate calories from protein (1g protein = 4 kcal)
     */
    public Double getProteinCalories() {
        if (proteinG == null) {
            return 0.0;
        }
        return proteinG * 4.0;
    }

    /**
     * Calculate calories from carbs (1g carbs = 4 kcal)
     */
    public Double getCarbsCalories() {
        if (carbsG == null) {
            return 0.0;
        }
        return carbsG * 4.0;
    }

    /**
     * Calculate calories from fat (1g fat = 9 kcal)
     */
    public Double getFatCalories() {
        if (fatG == null) {
            return 0.0;
        }
        return fatG * 9.0;
    }

    /**
     * Get macro breakdown as string
     */
    public String getMacroBreakdown() {
        return String.format("P: %.1fg | C: %.1fg | F: %.1fg",
                proteinG != null ? proteinG : 0,
                carbsG != null ? carbsG : 0,
                fatG != null ? fatG : 0);
    }

    @Override
    public String toString() {
        return "Meal{" +
                "id=" + id +
                ", mealName='" + mealName + '\'' +
                ", mealType=" + mealType +
                ", calories=" + calories +
                ", macros=" + getMacroBreakdown() +
                ", logDate=" + logDate +
                ", createdAt=" + createdAt +
                '}';
    }
}
