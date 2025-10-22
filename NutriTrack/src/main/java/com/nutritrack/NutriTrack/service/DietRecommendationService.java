package com.nutritrack.NutriTrack.service;

import com.nutritrack.NutriTrack.entity.DietRecommendation;
import com.nutritrack.NutriTrack.entity.User;
import com.nutritrack.NutriTrack.repository.DietRecommendationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class for generating and managing dietary recommendations.
 * Provides personalized suggestions based on nutritional analysis.
 */
@Service
public class DietRecommendationService {

    @Autowired
    private DietRecommendationRepository recommendationRepository;

    @Autowired
    private DietAnalyzerService dietAnalyzerService;

    @Autowired
    private UserService userService;

    /**
     * Generates and saves a personalized dietary recommendation for a user.
     *
     * @param userId the user ID
     * @return the saved DietRecommendation entity
     */
    public DietRecommendation generateAndSaveRecommendation(Long userId) {
        User user = userService.getUserById(userId);

        // Get nutritional analysis
        List<String> gaps = dietAnalyzerService.identifyNutritionalGaps(userId);
        int healthScore = dietAnalyzerService.calculateHealthScore(userId);
        String scoreCategory = dietAnalyzerService.getScoreCategory(healthScore);

        // Build recommendation text
        StringBuilder recommendationText = new StringBuilder();
        recommendationText.append("=== Dietary Recommendation Report ===\n\n");
        recommendationText.append("Health Score: ").append(healthScore).append("/100 (").append(scoreCategory).append(")\n\n");

        recommendationText.append("Nutritional Analysis:\n");
        for (String gap : gaps) {
            recommendationText.append("• ").append(gap).append("\n");
        }

        recommendationText.append("\nPersonalized Suggestions:\n");

        // Add specific suggestions based on gaps
        for (String gap : gaps) {
            if (gap.contains("protein")) {
                recommendationText.append(getSuggestionForProtein(gap)).append("\n");
            } else if (gap.contains("carb")) {
                recommendationText.append(getSuggestionForCarbs(gap)).append("\n");
            } else if (gap.contains("fat")) {
                recommendationText.append(getSuggestionForHealthyFats()).append("\n");
            } else if (gap.contains("meal frequency")) {
                recommendationText.append("• Try to spread your meals throughout the day to maintain energy levels.\n");
            } else if (gap.contains("calorie")) {
                recommendationText.append(getSuggestionForCalories(gap)).append("\n");
            }
        }

        recommendationText.append("\nGeneral Tips:\n");
        recommendationText.append("• Stay hydrated - aim for 8 glasses of water daily\n");
        recommendationText.append("• Include a variety of colorful vegetables in your meals\n");
        recommendationText.append("• Choose whole grains over refined carbohydrates\n");
        recommendationText.append("• Plan your meals ahead to maintain consistency\n");

        // Create and save recommendation
        DietRecommendation recommendation = new DietRecommendation();
        recommendation.setUser(user);
        recommendation.setRecommendationText(recommendationText.toString());
        recommendation.setHealthScore(healthScore);
        recommendation.setCreatedAt(LocalDateTime.now());

        return recommendationRepository.save(recommendation);
    }

    /**
     * Retrieves the most recent recommendation for a user.
     *
     * @param userId the user ID
     * @return the latest DietRecommendation or null if none exists
     */
    public DietRecommendation getLatestRecommendation(Long userId) {
        return recommendationRepository.findFirstByUserIdOrderByCreatedAtDesc(userId)
                .orElse(null);
    }

    /**
     * Retrieves all recommendations for a user, ordered by date (newest first).
     *
     * @param userId the user ID
     * @return list of all DietRecommendations for the user
     */
    public List<DietRecommendation> getAllRecommendations(Long userId) {
        return recommendationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Provides suggestions for protein intake adjustments.
     *
     * @param gap the identified protein gap
     * @return suggestion text
     */
    private String getSuggestionForProtein(String gap) {
        if (gap.toLowerCase().contains("increase")) {
            return """
                • Add lean protein sources: chicken breast, fish, eggs, Greek yogurt, legumes, or tofu
                • Aim for protein in every meal to meet your daily requirements""";
        } else {
            return """
                • Balance your protein intake with more vegetables and whole grains
                • Focus on moderate portions of lean protein sources""";
        }
    }

    /**
     * Provides suggestions for carbohydrate intake adjustments.
     *
     * @param gap the identified carbohydrate gap
     * @return suggestion text
     */
    private String getSuggestionForCarbs(String gap) {
        if (gap.toLowerCase().contains("reduce")) {
            return """
                • Reduce refined carbohydrates (white bread, pastries, sugary drinks)
                • Replace with vegetables, lean proteins, and healthy fats
                • Choose smaller portions of whole grains""";
        } else {
            return """
                • Include complex carbohydrates: oats, quinoa, brown rice, sweet potatoes
                • Add more fruits and vegetables to your meals
                • These provide energy and essential nutrients""";
        }
    }

    /**
     * Provides suggestions for incorporating healthy fats.
     *
     * @return suggestion text
     */
    private String getSuggestionForHealthyFats() {
        return """
            • Include healthy fat sources: avocados, nuts, seeds, olive oil, fatty fish
            • Add a handful of almonds or walnuts as a snack
            • Use olive oil for cooking and salad dressings
            • Aim for omega-3 rich foods like salmon or chia seeds""";
    }

    /**
     * Provides suggestions for calorie intake adjustments.
     *
     * @param gap the identified calorie gap
     * @return suggestion text
     */
    private String getSuggestionForCalories(String gap) {
        if (gap.toLowerCase().contains("increase")) {
            return """
                • Add nutrient-dense snacks between meals
                • Include healthy fats and complex carbohydrates
                • Consider adding smoothies or protein shakes""";
        } else {
            return """
                • Practice portion control using smaller plates
                • Focus on high-volume, low-calorie foods like vegetables
                • Reduce high-calorie beverages and processed snacks
                • Eat mindfully and avoid distractions during meals""";
        }
    }
}
