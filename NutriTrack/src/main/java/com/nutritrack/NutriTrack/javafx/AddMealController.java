package com.nutritrack.NutriTrack.javafx;

import com.nutritrack.NutriTrack.config.StageManager;
import com.nutritrack.NutriTrack.config.ViewPaths;
import com.nutritrack.NutriTrack.enums.MealType;
import com.nutritrack.NutriTrack.service.MealService;
import com.nutritrack.NutriTrack.entity.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.stream.IntStream;

@Controller
public class AddMealController {

    private final MealService mealService;
    private final StageManager stageManager;

    @FXML private ComboBox<MealType> mealTypeComboBox;
    @FXML private TextField foodNameField;
    @FXML private TextField portionSizeField;
    @FXML private TextField caloriesField;
    @FXML private TextField proteinField;
    @FXML private TextField carbsField;
    @FXML private TextField fatField;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> hourComboBox;
    @FXML private ComboBox<String> minuteComboBox;
    @FXML private TextArea notesField;
    @FXML private Label errorLabel;

    public AddMealController(MealService mealService, StageManager stageManager) {
        this.mealService = mealService;
        this.stageManager = stageManager;
    }

    @FXML
    private void initialize() {
        mealTypeComboBox.getItems().addAll(MealType.values());
        hourComboBox.getItems().addAll(IntStream.range(0, 24).mapToObj(i -> String.format("%02d", i)).toList());
        minuteComboBox.getItems().addAll(IntStream.range(0, 60).mapToObj(i -> String.format("%02d", i)).toList());

        datePicker.setValue(LocalDate.now());
        hourComboBox.setValue("12");
        minuteComboBox.setValue("00");

        errorLabel.setText("");
    }

    @FXML
    private void onSave() {
        try {
            if (foodNameField.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Please enter a food name");
            }
            if (mealTypeComboBox.getValue() == null) {
                throw new IllegalArgumentException("Please select a meal type");
            }

            double calories = Double.parseDouble(caloriesField.getText().trim());
            double protein = Double.parseDouble(proteinField.getText().trim());
            double carbs = Double.parseDouble(carbsField.getText().trim());
            double fat = Double.parseDouble(fatField.getText().trim());
            LocalDate date = datePicker.getValue();
            String notes = notesField.getText().trim();

            // Handle optional portion size
            Double portion = null;
            if (!portionSizeField.getText().trim().isEmpty()) {
                portion = Double.parseDouble(portionSizeField.getText().trim());
            }

            User currentUser = stageManager.getLoggedInUser();
            if (currentUser == null) {
                throw new IllegalStateException("No user logged in");
            }

            mealService.addMeal(
                currentUser.getId(),
                foodNameField.getText().trim(),
                mealTypeComboBox.getValue(),
                portion,
                notes,
                calories,
                protein,
                carbs,
                fat,
                date
            );

            // Navigate back and refresh the dashboard
            stageManager.switchScene(ViewPaths.DASHBOARD);
            DashboardController dashboardController = stageManager.getController(ViewPaths.DASHBOARD);
            if (dashboardController != null) {
                dashboardController.refreshDashboard();
            }

        } catch (NumberFormatException e) {
            showError("Please enter valid numbers for nutritional values");
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void onCancel() {
        try {
            stageManager.switchScene(ViewPaths.DASHBOARD);
        } catch (Exception e) {
            showError("Error returning to dashboard");
        }
    }

    private void showError(String message) {
        errorLabel.setVisible(true);
        errorLabel.setText(message);
    }
}
