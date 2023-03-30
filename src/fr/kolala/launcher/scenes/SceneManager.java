package fr.kolala.launcher.scenes;

import fr.trxyy.alternative.alternative_api.GameEngine;
import fr.trxyy.alternative.alternative_api_ui.LauncherPane;
import javafx.scene.Scene;
import javafx.scene.shape.Rectangle;

public class SceneManager {

    private final GameEngine gameEngine;
    private Scene scene;
    private LauncherPanel panel;

    public SceneManager(GameEngine engine) {
        this.gameEngine = engine;


    }

    public Scene createPane() {
        LauncherPane contentPane = new LauncherPane(this.gameEngine);
        scene = new Scene(contentPane);
        new LauncherPanel(contentPane, this.gameEngine);
        return scene;
    }

    private void setPanel(LauncherPanel panel) {
        this.panel = panel;
    }
}
