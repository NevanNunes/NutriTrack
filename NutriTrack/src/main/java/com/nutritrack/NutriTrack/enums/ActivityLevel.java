package com.nutritrack.NutriTrack.enums;

// ==================== ACTIVITY LEVEL ENUM ====================
public enum ActivityLevel {
    SEDENTARY("Sedentary", 1.2, "Little or no exercise"),
    LIGHT("Lightly Active", 1.375, "Light exercise 1-3 days/week"),
    MODERATE("Moderately Active", 1.55, "Moderate exercise 3-5 days/week"),
    ACTIVE("Very Active", 1.725, "Hard exercise 6-7 days/week"),
    VERY_ACTIVE("Extremely Active", 1.9, "Physical job or training twice per day");

    private final String displayName;
    private final double multiplier;
    private final String description;

    ActivityLevel(String displayName, double multiplier, String description) {
        this.displayName = displayName;
        this.multiplier = multiplier;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public String getDescription() {
        return description;
    }
}

