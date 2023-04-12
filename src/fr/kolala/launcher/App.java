package fr.kolala.launcher;

import fr.kolala.launcher.scenes.SceneManager;
import fr.trxyy.alternative.alternative_api.*;
import fr.trxyy.alternative.alternative_api.utils.Mover;
import fr.trxyy.alternative.alternative_api_ui.base.AlternativeBase;
import fr.trxyy.alternative.alternative_api_ui.base.LauncherBase;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class App extends AlternativeBase {

    //TODO: check if there's an update on the github

    private final GameFolder gameFolder = createGameFolder();
    private final GameLinks gameLinks = createGameLinks();
    private final LauncherPreferences launcherPreferences = createLauncherPreferences();
    private final GameEngine gameEngine = createGameEngine();

    private final SceneManager sceneManager = new SceneManager(gameEngine);
    private Scene scene;

    public void launcher() {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        createContent();
        this.gameEngine.reg(primaryStage);
        LauncherBase launcherBase = new LauncherBase(primaryStage, scene, StageStyle.DECORATED, this.gameEngine);
        launcherBase.setIconImage(primaryStage, "assets/favicon.png");
    }

    private GameFolder createGameFolder() {
        return new GameFolder("dragonadventure");
    }

    private GameLinks createGameLinks() {
        return new GameLinks("http://example-url.com/forge/", "1.16.5-forge-36.2.39.json");
    }

    private LauncherPreferences createLauncherPreferences() {
        return new LauncherPreferences("Dragon Adventure Launcher", 1280, 720, Mover.MOVE);
    }

    private GameEngine createGameEngine() {
        return new GameEngine(gameFolder, gameLinks, launcherPreferences, GameStyle.FORGE_1_13_HIGHER);
    }

    private void createContent() {
        scene = sceneManager.createPane();
        this.gameEngine.reg(gameLinks);
    }
}
