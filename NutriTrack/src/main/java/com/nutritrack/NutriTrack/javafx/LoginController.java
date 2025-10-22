package com.nutritrack.NutriTrack.javafx;

import com.nutritrack.NutriTrack.config.StageManager;
import com.nutritrack.NutriTrack.config.ViewPaths;
import com.nutritrack.NutriTrack.entity.User;
import com.nutritrack.NutriTrack.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Controller;

@Controller
public class LoginController {

    private final UserService userService;
    private final StageManager stageManager;

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;
    @FXML private Hyperlink registerLink;

    public LoginController(UserService userService, StageManager stageManager) {
        this.userService = userService;
        this.stageManager = stageManager;
    }

    @FXML
    private void initialize() {
        errorLabel.setText("");
        usernameField.textProperty().addListener((obs, oldValue, newValue) -> validateInputs());
        passwordField.textProperty().addListener((obs, oldValue, newValue) -> validateInputs());
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (!validateInputs()) {
            return;
        }

        try {
            User user = userService.loginUser(username, password);
            stageManager.setLoggedInUser(user);
            errorLabel.setText("");
            stageManager.switchScene(ViewPaths.DASHBOARD);
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleRegisterLink() {
        try {
            stageManager.switchScene(ViewPaths.REGISTER);
        } catch (Exception e) {
            showError("Error loading registration page");
        }
    }

    private boolean validateInputs() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        boolean isValid = true;

        if (username.isEmpty()) {
            errorLabel.setText("Username is required");
            isValid = false;
        } else if (password.isEmpty()) {
            errorLabel.setText("Password is required");
            isValid = false;
        }

        if (isValid) {
            errorLabel.setText("");
        }

        loginButton.setDisable(!isValid);
        return isValid;
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
