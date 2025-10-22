package com.nutritrack.NutriTrack.config;

import com.nutritrack.NutriTrack.entity.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class StageManager {
    private Stage primaryStage;
    private final ApplicationContext applicationContext;
    private User loggedInUser;
    private final Map<String, Object> controllers = new HashMap<>();

    public StageManager(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void switchScene(String fxmlPath) throws Exception {
        // The fxmlPath from ViewPaths is already a full path (e.g., "/fxml/login.fxml")
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        loader.setControllerFactory(applicationContext::getBean);
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
        primaryStage.show(); // This line makes the window visible
        controllers.put(fxmlPath, loader.getController());
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
