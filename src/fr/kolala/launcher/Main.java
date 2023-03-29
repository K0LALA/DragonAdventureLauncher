package fr.kolala.launcher;

import javafx.application.Application;

import fr.kolala.launcher.scenes.SceneManager;

public class Main {
    public static void main(String[] args) {
        // Launch the app from a class that is a children of Application
        Application.launch(SceneManager.class, args);
    }
}
