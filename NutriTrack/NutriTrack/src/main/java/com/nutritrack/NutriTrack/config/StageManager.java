package com.nutritrack.NutriTrack.config;

import com.nutritrack.NutriTrack.entity.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

@Component
public class StageManager {
    private Stage primaryStage;
    private final ApplicationContext applicationContext;
    private User loggedInUser;
    private final Map<String, Object> controllers = new HashMap<>();
    private final Stack<String> sceneHistory = new Stack<>();

    public StageManager(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void switchScene(String fxmlPath) throws Exception {
        if (primaryStage.getScene() != null) {
            sceneHistory.push(primaryStage.getScene().getUserData().toString());
        }
        // The fxmlPath from ViewPaths is already a full path (e.g., "/fxml/login.fxml")
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        loader.setControllerFactory(applicationContext::getBean);
        Scene scene = new Scene(loader.load());
        scene.setUserData(fxmlPath); // Store the fxmlPath in the scene's UserData
        primaryStage.setScene(scene);
        primaryStage.show(); // This line makes the window visible
        controllers.put(fxmlPath, loader.getController());
    }

    public void goBack() throws Exception {
        if (!sceneHistory.isEmpty()) {
            String previousFxmlPath = sceneHistory.pop();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(previousFxmlPath));
            loader.setControllerFactory(applicationContext::getBean);
            Scene scene = new Scene(loader.load());
            scene.setUserData(previousFxmlPath);
            primaryStage.setScene(scene);
            primaryStage.show();
        } else {
            // Optionally, navigate to a default screen like login or dashboard if history is empty
            System.out.println("No previous scene in history.");
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getController(String fxmlPath) {
        return (T) controllers.get(fxmlPath);
    }

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }
}
