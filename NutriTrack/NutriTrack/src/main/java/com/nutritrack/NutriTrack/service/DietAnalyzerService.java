package com.nutritrack.NutriTrack.service;

import com.nutritrack.NutriTrack.entity.Meal;
import com.nutritrack.NutriTrack.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class for analyzing dietary intake and providing health insights.
 * Calculates nutritional metrics, health scores, and identifies nutritional gaps.
 */
@org.springframework.stereotype.Service
public class DietAnalyzerService {

    private final MealService mealService;
    private final UserService userService;

    @Autowired
    public DietAnalyzerService(MealService mealService, UserService userService) {
        this.mealService = mealService;
        this.userService = userService;
    }

    /**
     * Analyzes today's diet for a user and compares with recommendations.
     *
     * @param userId the user ID
     * @return map containing dietary analysis data
     */
    public Map<String, Object> analyzeTodaysDiet(Long userId) {
        User user = userService.getUserById(userId);
        Map<String, Double> todaysMacros = mealService.getTodaysMacros(userId);

        double totalCalories = mealService.getTodaysTotalCalories(userId);
        double totalProtein = todaysMacros.getOrDefault("protein", 0.0);
        double totalCarbs = todaysMacros.getOrDefault("carbs", 0.0);
        double totalFat = todaysMacros.getOrDefault("fat", 0.0);

        // Calculate recommended values
        double recommendedCalories = user.calculateDailyCalorieNeeds();
        double recommendedProtein = user.getWeightKg() * 1.6; // 1.6g per kg for active individuals
        double recommendedCarbs = (recommendedCalories * 0.5) / 4; // 50% of calories from carbs
        double recommendedFat = (recommendedCalories * 0.3) / 9; // 30% of calories from fat

        double calorieDeficit = recommendedCalories - totalCalories;

        // Calculate macronutrient percentages
        double totalMacroCalories = (totalProtein * 4) + (totalCarbs * 4) + (totalFat * 9);
        double proteinPercent = totalMacroCalories > 0 ? (totalProtein * 4 / totalMacroCalories) * 100 : 0;
        double carbsPercent = totalMacroCalories > 0 ? (totalCarbs * 4 / totalMacroCalories) * 100 : 0;
        double fatPercent = totalMacroCalories > 0 ? (totalFat * 9 / totalMacroCalories) * 100 : 0;

        // Check if diet is balanced
        boolean isBalanced = proteinPercent >= 10 && proteinPercent <= 35 &&
                carbsPercent >= 45 && carbsPercent <= 65 &&
                fatPercent >= 20 && fatPercent <= 35;

        Map<String, Object> analysis = new HashMap<>();
        analysis.put("totalCalories", totalCalories);
        analysis.put("totalProtein", totalProtein);
        analysis.put("totalCarbs", totalCarbs);
        analysis.put("totalFat", totalFat);
        analysis.put("recommendedCalories", recommendedCalories);
        analysis.put("recommendedProtein", recommendedProtein);
        analysis.put("recommendedCarbs", recommendedCarbs);
        analysis.put("recommendedFat", recommendedFat);
        analysis.put("calorieDeficit", calorieDeficit);
        analysis.put("proteinPercent", proteinPercent);
        analysis.put("carbsPercent", carbsPercent);
        analysis.put("fatPercent", fatPercent);
        analysis.put("isBalanced", isBalanced);
        analysis.put("mealCount", mealService.getTodaysMeals(userId).size());

        return analysis;
    }

    /**
     * Calculates a health score (0-100) based on dietary intake.
     *
     * @param userId the user ID
     * @return health score between 0 and 100
     */
    public int calculateHealthScore(Long userId) {
        Map<String, Object> analysis = analyzeTodaysDiet(userId);

        int score = 100;

        double totalCalories = (Double) analysis.get("totalCalories");
        double recommendedCalories = (Double) analysis.get("recommendedCalories");
        double proteinPercent = (Double) analysis.get("proteinPercent");
        double carbsPercent = (Double) analysis.get("carbsPercent");
        double fatPercent = (Double) analysis.get("fatPercent");
        int mealCount = (Integer) analysis.get("mealCount");

        // Deduct points for calorie imbalances
        if (totalCalories > recommendedCalories + 500) {
            score -= 20;
        }
        if (totalCalories < recommendedCalories - 300) {
            score -= 15;
        }

        // Deduct points for protein imbalance
        if (proteinPercent < 10 || proteinPercent > 35) {
            score -= 15;
        }

        // Deduct points for carbs imbalance
        if (carbsPercent < 45 || carbsPercent > 65) {
            score -= 10;
        }

        // Deduct points for fat imbalance
        if (fatPercent < 20 || fatPercent > 35) {
            score -= 10;
        }

        // Deduct points for insufficient meals
        if (mealCount < 3) {
            score -= 10;
        }

        return Math.max(0, Math.min(100, score));
    }

    /**
     * Gets the category description for a health score.
     *
     * @param score the health score (0-100)
     * @return category description
     */
    public String getScoreCategory(int score) {
        if (score >= 90) {
            return "Excellent";
        } else if (score >= 75) {
            return "Good";
        } else if (score >= 60) {
            return "Fair";
        } else {
            return "Needs Improvement";
        }
    }

    /**
     * Identifies nutritional gaps in the user's diet.
     *
     * @param userId the user ID
     * @return list of nutritional gap descriptions
     */
    public List<String> identifyNutritionalGaps(Long userId) {
        Map<String, Object> analysis = analyzeTodaysDiet(userId);
        List<String> gaps = new ArrayList<>();

        double totalProtein = (Double) analysis.get("totalProtein");
        double totalCarbs = (Double) analysis.get("totalCarbs");
        double totalFat = (Double) analysis.get("totalFat");
        double recommendedProtein = (Double) analysis.get("recommendedProtein");
        double recommendedCarbs = (Double) analysis.get("recommendedCarbs");
        double recommendedFat = (Double) analysis.get("recommendedFat");
        double proteinPercent = (Double) analysis.get("proteinPercent");
        double carbsPercent = (Double) analysis.get("carbsPercent");
        double fatPercent = (Double) analysis.get("fatPercent");

        // Check protein intake
        if (totalProtein < recommendedProtein * 0.8) {
            gaps.add("Increase protein intake");
        } else if (proteinPercent > 35) {
            gaps.add("Reduce protein intake slightly");
        }

        // Check carbohydrate intake
        if (carbsPercent > 65) {
            gaps.add("Reduce carbohydrate intake");
        } else if (carbsPercent < 45) {
            gaps.add("Increase complex carbohydrate intake");
        }

        // Check fat intake
        if (totalFat < recommendedFat * 0.8 || fatPercent < 20) {
            gaps.add("Add more healthy fats");
        } else if (fatPercent > 35) {
            gaps.add("Reduce fat intake");
        }

        // Check meal frequency
        int mealCount = (Integer) analysis.get("mealCount");
        if (mealCount < 3) {
            gaps.add("Increase meal frequency to at least 3 meals per day");
        }

        // Check overall calorie intake
        double calorieDeficit = (Double) analysis.get("calorieDeficit");
        if (calorieDeficit > 300) {
            gaps.add("Increase overall calorie intake");
        } else if (calorieDeficit < -500) {
            gaps.add("Reduce overall calorie intake");
        }

        if (gaps.isEmpty()) {
            gaps.add("Your diet is well-balanced. Keep up the good work!");
        }

        return gaps;
    }
}
