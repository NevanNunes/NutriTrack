package com.nutritrack.NutriTrack;

import com.nutritrack.NutriTrack.javafx.NutriTrackFXApp;
import javafx.application.Application; 
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NutriTrackApplication {

    public static void main(String[] args) {
        Application.launch(NutriTrackFXApp.class, args);
    }
}
