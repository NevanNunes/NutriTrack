package com.nutritrack.NutriTrack.enums;

// ==================== BMI CATEGORY ENUM ====================
public enum BMICategory {
    UNDERWEIGHT("Underweight", 0, 18.5),
    NORMAL("Normal Weight", 18.5, 25),
    OVERWEIGHT("Overweight", 25, 30),
    OBESE("Obese", 30, Double.MAX_VALUE);

    private final String displayName;
    private final double minBMI;
    private final double maxBMI;

    BMICategory(String displayName, double minBMI, double maxBMI) {
        this.displayName = displayName;
        this.minBMI = minBMI;
        this.maxBMI = maxBMI;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getMinBMI() {
        return minBMI;
    }

    public double getMaxBMI() {
        return maxBMI;
    }

    /**
     * Determine BMI category from BMI value
     * @param bmi the BMI value
     * @return appropriate BMICategory
     */
    public static BMICategory fromBMI(double bmi) {
        if (bmi < 18.5) {
            return UNDERWEIGHT;
        } else if (bmi < 25) {
            return NORMAL;
        } else if (bmi < 30) {
            return OVERWEIGHT;
        } else {
            return OBESE;
        }
    }

    /**
     * Get health recommendation based on BMI category
     */
    public String getHealthRecommendation() {
        return switch (this) {
            case UNDERWEIGHT -> "Consider consulting a nutritionist to ensure adequate calorie intake";
            case NORMAL -> "Maintain your current weight through balanced diet and regular exercise";
            case OVERWEIGHT -> "Aim for gradual weight loss through diet and exercise";
            case OBESE -> "Consult with a healthcare professional for a personalized weight management plan";
        };
    }
}