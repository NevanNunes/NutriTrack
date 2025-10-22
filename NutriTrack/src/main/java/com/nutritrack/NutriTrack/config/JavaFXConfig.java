package com.nutritrack.NutriTrack.config;

import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JavaFXConfig {

    @Bean
    public StageManager stageManager(ApplicationContext applicationContext) {
        return new StageManager(applicationContext);
    }
}
