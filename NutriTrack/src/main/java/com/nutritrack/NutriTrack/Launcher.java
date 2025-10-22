package com.nutritrack.NutriTrack;

/**
 * This is a workaround launcher class to solve the "JavaFX runtime components are missing" error.
 * It calls the main application's main method, bypassing the JVM's initial module check.
 */
public class Launcher {

    public static void main(String[] args) {
        NutriTrackApplication.main(args);
    }
}