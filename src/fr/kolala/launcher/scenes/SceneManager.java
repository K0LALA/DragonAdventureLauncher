package fr.kolala.launcher.scenes;

import fr.trxyy.alternative.alternative_apiv2.base.*;
import fr.trxyy.alternative.alternative_apiv2.utils.Mover;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SceneManager extends AlternativeBase {

    private final GameFolder gameFolder = new GameFolder("DragonAdventure");
    private final GameLinks gameLinks = new GameLinks("http://127.0.0.1/launcher/", "1.16.5-forge-36.2.34.json");
    private final LauncherPreferences launcherPreferences = new LauncherPreferences("Dragon Adventure Launcher", 1280, 720, Mover.MOVE);
    private final GameEngine gameEngine = new GameEngine(gameFolder, gameLinks, launcherPreferences);

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(firstPanel());
        LauncherBase launcher = new LauncherBase(primaryStage, scene, StageStyle.DECORATED, gameEngine);
        launcher.setIconImage(primaryStage, "assets/favicon.png");
    }

    private Parent firstPanel() {
        LauncherPane contentPane = new LauncherPane(gameEngine);
        new MainPanel(contentPane, gameEngine);
        return contentPane;
    }
}
