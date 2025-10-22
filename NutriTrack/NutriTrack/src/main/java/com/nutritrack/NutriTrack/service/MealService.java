package com.nutritrack.NutriTrack.service;

import com.nutritrack.NutriTrack.enums.MealType;
import com.nutritrack.NutriTrack.entity.Meal;
import com.nutritrack.NutriTrack.entity.User;
import com.nutritrack.NutriTrack.repository.MealRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MealService {

    private final MealRepository mealRepository;
    private final UserService userService;

    public MealService(MealRepository mealRepository, UserService userService) {
        this.mealRepository = mealRepository;
        this.userService = userService;
    }

    public Meal addMeal(Long userId, String mealName, MealType mealType,
                        Double portionSize, String notes,
                        double calories, double proteinG, double carbsG, double fatG,
                        LocalDate logDate) {
        User user = userService.getUserById(userId);

        Meal meal = new Meal();
        meal.setUser(user);
        meal.setMealName(mealName);
        meal.setMealType(mealType);
        meal.setPortionSize(portionSize);
        meal.setNotes(notes);
        meal.setCalories(calories);
        meal.setProteinG(proteinG);
        meal.setCarbsG(carbsG);
        meal.setFatG(fatG);
        meal.setLogDate(logDate);

        return mealRepository.save(meal);
    }

    public List<Meal> getTodaysMeals(Long userId) {
        LocalDate today = LocalDate.now();
        return mealRepository.findByUserIdAndLogDate(userId, today);
    }

    public List<Meal> getMealsByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return mealRepository.findByUserIdAndLogDateBetween(userId, startDate, endDate);
    }

    public Double getTodaysTotalCalories(Long userId) {
        // Use the optimized repository query to calculate in the database
        return mealRepository.getTotalCaloriesByDate(userId, LocalDate.now());
    }

    public Map<String, Double> getTodaysMacros(Long userId) {
        LocalDate today = LocalDate.now();
        // Use the optimized repository queries for each macro
        double totalProtein = mealRepository.getTotalProteinByDate(userId, today);
        double totalCarbs = mealRepository.getTotalCarbsByDate(userId, today);
        double totalFat = mealRepository.getTotalFatByDate(userId, today);

        Map<String, Double> macros = new HashMap<>();
        macros.put("protein", totalProtein);
        macros.put("carbs", totalCarbs);
        macros.put("fat", totalFat);

        return macros;
    }

    public void deleteMeal(Long mealId) {
        if (!mealRepository.existsById(mealId)) {
            throw new IllegalArgumentException("Meal not found with ID: " + mealId);
        }
        mealRepository.deleteById(mealId);
    }

    public List<Meal> getAllMealsForUser(Long userId) {
        return mealRepository.findByUserIdOrderByLogDateDesc(userId);
    }
}
