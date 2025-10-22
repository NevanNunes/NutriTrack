package com.nutritrack.NutriTrack.javafx;

import com.nutritrack.NutriTrack.config.ViewPaths;
import javafx.application.Application;
import javafx.application.Platform;
import org.springframework.boot.WebApplicationType;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import com.nutritrack.NutriTrack.config.StageManager;
import com.nutritrack.NutriTrack.NutriTrackApplication;

public class NutriTrackFXApp extends Application {

    private ConfigurableApplicationContext springContext;

    @Override
    public void init() {
        springContext = new SpringApplicationBuilder(NutriTrackApplication.class)
                .web(WebApplicationType.NONE) // This line disables the web server
                .run();
    }

    @Override
    public void start(Stage stage) {
        try {
            stage.setTitle("NutriTrack");

            // Get the StageManager bean from the Spring context
            StageManager stageManager = springContext.getBean(StageManager.class);
            // Provide the primary stage to the StageManager
            stageManager.setPrimaryStage(stage);

            // Show login screen initially
            stageManager.switchScene(ViewPaths.LOGIN);
        } catch (Exception e) {
            showErrorAndExit("Application Error", "Failed to start application", e.getMessage());
        }
    }

    @Override
    public void stop() {
        springContext.close();
        Platform.exit();
    }

    private static void showError(String title, String header, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    private static void showErrorAndExit(String title, String header, String content) {
        if (Platform.isFxApplicationThread()) {
            showError(title, header, content);
        }
        // Avoid immediate exit which can cause issues with showing the alert
    }
}
