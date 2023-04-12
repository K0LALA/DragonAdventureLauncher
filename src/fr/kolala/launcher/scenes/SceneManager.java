package fr.kolala.launcher.scenes;

import fr.trxyy.alternative.alternative_api.GameEngine;
import fr.trxyy.alternative.alternative_api_ui.LauncherPane;
import javafx.scene.Scene;
import javafx.scene.shape.Rectangle;

public class SceneManager {

    private final GameEngine gameEngine;

    public SceneManager(GameEngine engine) {
        this.gameEngine = engine;
    }

    public Scene createPane() {
        LauncherPane contentPane = new LauncherPane(this.gameEngine);
        Scene scene = new Scene(contentPane);
        Rectangle rectangle = new Rectangle(this.gameEngine.getLauncherPreferences().getWidth(),
                this.gameEngine.getLauncherPreferences().getHeight());
        rectangle.setArcWidth(10.0);
        rectangle.setArcHeight(10.0);
        contentPane.setClip(rectangle);
        contentPane.setStyle("-fx-background-color: transparent;");
        new LauncherPanel(contentPane, this.gameEngine);
        return scene;
    }
}
