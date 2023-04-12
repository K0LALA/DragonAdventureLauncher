package fr.kolala.launcher.scenes;

import fr.trxyy.alternative.alternative_api.GameEngine;
import fr.trxyy.alternative.alternative_api_ui.LauncherPane;
import javafx.scene.Scene;

public class SceneManager {

    private final GameEngine gameEngine;

    public SceneManager(GameEngine engine) {
        this.gameEngine = engine;
    }

    public Scene createPane() {
        LauncherPane contentPane = new LauncherPane(this.gameEngine);
        Scene scene = new Scene(contentPane);
        new LauncherPanel(contentPane, this.gameEngine);
        return scene;
    }
}
