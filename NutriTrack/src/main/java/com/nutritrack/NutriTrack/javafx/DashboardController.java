package com.nutritrack.NutriTrack.javafx;

import com.nutritrack.NutriTrack.config.StageManager;
import com.nutritrack.NutriTrack.config.ViewPaths;
import com.nutritrack.NutriTrack.entity.Meal;
import com.nutritrack.NutriTrack.entity.User;
import com.nutritrack.NutriTrack.enums.MealType;
import com.nutritrack.NutriTrack.service.DietAnalyzerService;
import com.nutritrack.NutriTrack.service.MealService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    private final MealService mealService;
    private final DietAnalyzerService dietAnalyzerService;
    private final StageManager stageManager;

    @FXML private Button logoutButton;
    @FXML private Button dashboardButton;
    @FXML private Button addMealButton;
    @FXML private Button historyButton;
    @FXML private Button profileButton;
    @FXML private Text caloriesText;
    @FXML private Text macrosText;
    @FXML private Text bmiText; // This should be a Label or Text
    @FXML private TableView<Meal> mealsTable;
    @FXML private TableColumn<Meal, LocalDateTime> timeColumn; // Correct
    @FXML private TableColumn<Meal, MealType> typeColumn; // Corrected from String to MealType
    @FXML private TableColumn<Meal, String> foodColumn;
    @FXML private TableColumn<Meal, Double> caloriesColumn;
    @FXML private TableColumn<Meal, Double> proteinColumn;
    @FXML private TableColumn<Meal, Double> carbsColumn;
    @FXML private TableColumn<Meal, Double> fatColumn;
    @FXML private TableColumn<Meal, Void> actionsColumn;
    @FXML private TextArea recommendationsText;

    public DashboardController(MealService mealService, DietAnalyzerService dietAnalyzerService, StageManager stageManager) {
        this.mealService = mealService;
        this.dietAnalyzerService = dietAnalyzerService;
        this.stageManager = stageManager;
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        refreshDashboard();
    }

    private void setupTableColumns() {
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt")); // Will need custom cell factory
        foodColumn.setCellValueFactory(new PropertyValueFactory<>("mealName"));
        caloriesColumn.setCellValueFactory(new PropertyValueFactory<>("calories"));
        proteinColumn.setCellValueFactory(new PropertyValueFactory<>("proteinG"));
        carbsColumn.setCellValueFactory(new PropertyValueFactory<>("carbsG"));
        fatColumn.setCellValueFactory(new PropertyValueFactory<>("fatG"));

        // Custom cell for time
        timeColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")));
                }
            }
        });

        // Custom cell for MealType to show display name
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("mealType"));
        typeColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(MealType item, boolean empty) { // Corrected from String to MealType
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getDisplayName());
                }
            }
        });

        // Action buttons
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");
            private final HBox pane = new HBox(deleteButton);

            {
                pane.setSpacing(5);
                deleteButton.setOnAction(event -> {
                    Meal meal = getTableView().getItems().get(getIndex());
                    deleteMeal(meal);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    public void refreshDashboard() {
        User user = stageManager.getLoggedInUser();
        if (user == null) {
            onLogout(); // No user, log out
            return;
        }

        loadTodaysMeals(user);
        updateStats(user);
        updateRecommendations(user);
    }

    private void loadTodaysMeals(User user) {
        List<Meal> todaysMeals = mealService.getTodaysMeals(user.getId());
        mealsTable.setItems(FXCollections.observableArrayList(todaysMeals));
    }

    private void updateStats(User user) {
        Map<String, Object> dietAnalysis = dietAnalyzerService.analyzeTodaysDiet(user.getId());

        double totalCalories = ((Number) dietAnalysis.getOrDefault("totalCalories", 0.0)).doubleValue();
        double recommendedCalories = ((Number) dietAnalysis.getOrDefault("recommendedCalories", 2000.0)).doubleValue();
        caloriesText.setText(String.format("%.0f / %.0f kcal", totalCalories, recommendedCalories));

        double totalProtein = ((Number) dietAnalysis.getOrDefault("totalProtein", 0.0)).doubleValue();
        double totalCarbs = ((Number) dietAnalysis.getOrDefault("totalCarbs", 0.0)).doubleValue();
        double totalFat = ((Number) dietAnalysis.getOrDefault("totalFat", 0.0)).doubleValue();
        macrosText.setText(String.format("P: %.0fg, C: %.0fg, F: %.0fg", totalProtein, totalCarbs, totalFat));

        Double bmi = user.calculateBMI();
        bmiText.setText(bmi != null ? String.format("%.1f", bmi) : "N/A");
    }

    private void updateRecommendations(User user) {
        List<String> gaps = dietAnalyzerService.identifyNutritionalGaps(user.getId());
        String recommendations = gaps.stream().map(s -> "- " + s).collect(Collectors.joining("\n"));
        recommendationsText.setText(recommendations);
    }

    private void deleteMeal(Meal meal) {
        mealService.deleteMeal(meal.getId());
        refreshDashboard();
    }

    @FXML
    private void onLogout() {
        try {
            stageManager.setLoggedInUser(null);
            stageManager.switchScene(ViewPaths.LOGIN);
        } catch (Exception e) {
            showError("Error during logout");
        }
    }

    @FXML
    private void navigateToDashboard() {
        refreshDashboard(); // Already on dashboard, just refresh
    }

    @FXML
    private void navigateToAddMeal() {
        try {
            stageManager.switchScene(ViewPaths.ADD_MEAL);
        } catch (Exception e) {
            showError("Error opening add meal screen");
        }
    }

    @FXML
    private void navigateToHistory() {
        try {
            stageManager.switchScene(ViewPaths.HISTORY);
        } catch (Exception e) {
            showError("Error opening history screen");
        }
    }

    @FXML
    private void navigateToProfile() {
        try {
            stageManager.switchScene(ViewPaths.PROFILE);
        } catch (Exception e) {
            showError("Error opening profile screen");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
