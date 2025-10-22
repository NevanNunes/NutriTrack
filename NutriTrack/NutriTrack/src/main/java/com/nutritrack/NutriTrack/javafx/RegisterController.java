package com.nutritrack.NutriTrack.javafx;

import com.nutritrack.NutriTrack.config.StageManager;
import com.nutritrack.NutriTrack.config.ViewPaths;
import com.nutritrack.NutriTrack.entity.User;
import com.nutritrack.NutriTrack.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Controller;

import java.util.stream.IntStream;

@Controller
public class RegisterController {

    private final UserService userService;
    private final StageManager stageManager;

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField nameField;
    @FXML private TextField ageField;
    @FXML private ComboBox<User.Gender> genderComboBox;
    @FXML private TextField heightField;
    @FXML private TextField weightField;
    @FXML private ComboBox<User.ActivityLevel> activityLevelComboBox;
    @FXML private Label errorLabel;

    public RegisterController(UserService userService, StageManager stageManager) {
        this.userService = userService;
        this.stageManager = stageManager;
    }

    @FXML
    private void initialize() {
        genderComboBox.getItems().addAll(User.Gender.values());
        activityLevelComboBox.getItems().addAll(User.ActivityLevel.values());
        errorLabel.setText("");
        errorLabel.setVisible(false);
    }

    @FXML
    private void handleRegister() {
        try {
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();
            String name = nameField.getText().trim();
            int age = Integer.parseInt(ageField.getText().trim());
            User.Gender gender = genderComboBox.getValue();
            double heightCm = Double.parseDouble(heightField.getText().trim());
            double weightKg = Double.parseDouble(weightField.getText().trim());
            User.ActivityLevel activityLevel = activityLevelComboBox.getValue();

            if (!password.equals(confirmPassword)) {
                throw new IllegalArgumentException("Passwords do not match.");
            }

            userService.registerUser(username, email, password, name, age, gender, heightCm, weightKg, activityLevel);

            stageManager.switchScene(ViewPaths.LOGIN);

        } catch (NumberFormatException e) {
            showError("Please enter valid numbers for age, height, and weight.");
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("An unexpected error occurred: " + e.getMessage());
        }
    }

    @FXML
    private void handleBackToLogin() {
        try {
            stageManager.switchScene(ViewPaths.LOGIN);
        } catch (Exception e) {
            showError("Error returning to login page.");
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
