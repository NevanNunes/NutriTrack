package com.nutritrack.NutriTrack.javafx;

import com.nutritrack.NutriTrack.config.StageManager;
import com.nutritrack.NutriTrack.entity.User;
import com.nutritrack.NutriTrack.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text; // Ensure this import is present
import org.springframework.stereotype.Controller;

import java.time.format.DateTimeFormatter;

@Controller
public class ProfileController {

    private final UserService userService;
    private final StageManager stageManager;

    // This field must match the fx:id in the FXML and be of the correct type
    @FXML
    private Text lastUpdatedText;

    @FXML private TextField nameField;
    @FXML private TextField ageField;
    @FXML private ComboBox<User.Gender> genderComboBox;
    @FXML private TextField heightField;
    @FXML private TextField weightField;
    @FXML private ComboBox<User.ActivityLevel> activityLevelComboBox;
    @FXML private TextField emailField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;
    @FXML private Button resetButton; // Added missing fx:id
    @FXML private Button saveButton; // Added missing fx:id

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

    public ProfileController(UserService userService, StageManager stageManager) {
        this.userService = userService;
        this.stageManager = stageManager;
    }

    @FXML
    private void initialize() {
        setupComboBoxes();
        loadUserData();
        clearLabels();
    }

    private void setupComboBoxes() {
        genderComboBox.getItems().addAll(User.Gender.values());
        activityLevelComboBox.getItems().addAll(User.ActivityLevel.values());
    }

    private void loadUserData() {
        try {
            User user = stageManager.getLoggedInUser();
            if (user == null) {
                showError("No user logged in");
                return;
            }

            nameField.setText(user.getName());
            emailField.setText(user.getEmail());
            ageField.setText(String.valueOf(user.getAge()));
            heightField.setText(String.valueOf(user.getHeightCm()));
            weightField.setText(String.valueOf(user.getWeightKg()));
            genderComboBox.setValue(user.getGender());
            activityLevelComboBox.setValue(user.getActivityLevel());

            if (user.getUpdatedAt() != null) {
                lastUpdatedText.setText("Last updated: " + user.getUpdatedAt().format(dtf));
            } else {
                lastUpdatedText.setText("Profile not yet updated.");
            }

            newPasswordField.clear();
            confirmPasswordField.clear();

        } catch (Exception e) {
            showError("Error loading user data: " + e.getMessage());
        }
    }

    private void clearLabels() {
        errorLabel.setText("");
        errorLabel.setVisible(false);
        successLabel.setText("");
        successLabel.setVisible(false);
    }

    @FXML
    private void onSave() {
        clearLabels();

        try {
            User user = stageManager.getLoggedInUser();
            if (user == null) {
                showError("No user logged in");
                return;
            }

            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();
            if (!newPassword.isEmpty() || !confirmPassword.isEmpty()) {
                if (!newPassword.equals(confirmPassword)) {
                    showError("Passwords do not match.");
                    return;
                }
                user.setPassword(newPassword);
            }

            user.setName(nameField.getText().trim());
            user.setEmail(emailField.getText().trim());
            user.setAge(Integer.parseInt(ageField.getText().trim()));
            user.setHeightCm(Double.parseDouble(heightField.getText().trim()));
            user.setWeightKg(Double.parseDouble(weightField.getText().trim()));
            user.setGender(genderComboBox.getValue());
            user.setActivityLevel(activityLevelComboBox.getValue());

            User updatedUser = userService.updateUser(user);

            showSuccess("Profile updated successfully!");

            if (updatedUser.getUpdatedAt() != null) {
                lastUpdatedText.setText("Last updated: " + updatedUser.getUpdatedAt().format(dtf));
            }

            newPasswordField.clear();
            confirmPasswordField.clear();

        } catch (NumberFormatException e) {
            showError("Please enter valid numbers for age, height, and weight.");
        } catch (Exception e) {
            showError("Error updating profile: " + e.getMessage());
        }
    }

    @FXML
    private void onReset() {
        clearLabels();
        loadUserData();
        showSuccess("Changes have been reset.");
    }

    @FXML
    private void handleBack() {
        try {
            stageManager.goBack();
        } catch (Exception e) {
            showError("Error navigating back: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void showSuccess(String message) {
        successLabel.setText(message);
        successLabel.setVisible(true);
    }
}
