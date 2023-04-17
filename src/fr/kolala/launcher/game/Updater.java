package fr.kolala.launcher.game;

import fr.trxyy.alternative.alternative_api.GameEngine;
import fr.trxyy.alternative.alternative_api.GameMemory;
import fr.trxyy.alternative.alternative_api.updater.GameUpdater;
import fr.trxyy.alternative.alternative_api.utils.config.EnumConfig;
import fr.trxyy.alternative.alternative_api.utils.config.LauncherConfig;
import fr.trxyy.alternative.alternative_api_ui.components.LauncherLabel;
import fr.trxyy.alternative.alternative_api_ui.components.LauncherProgressBar;
import fr.trxyy.alternative.alternative_auth.base.GameAuth;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class Updater {

    private final GameEngine engine;
    private final LauncherConfig config;
    private final GameUpdater gameUpdater = new GameUpdater();

    public Updater(GameEngine gameEngine, LauncherConfig config) {
        this.engine = gameEngine;
        this.config = config;
    }

    private void timelineUpdate (LauncherProgressBar progressBar, LauncherLabel updateFileLabel) {
        double percent = 0D;
        if (this.engine.getGameUpdater().downloadedFiles > 0)
            percent = engine.getGameUpdater().downloadedFiles * 100.0D / engine.getGameUpdater().filesToDownload;
        updateFileLabel.setText(engine.getGameUpdater().getCurrentFile());
        progressBar.setProgress(percent / 100.0D);
    }

    public void updateGame (GameAuth auth, LauncherProgressBar progressBar, LauncherLabel updateFileLabel) {
        engine.getGameLinks().JSON_URL = "http://127.0.0.1/launcher/1.16.5.json";
        gameUpdater.reg(engine);
        gameUpdater.reg(auth.getSession());

        engine.reg(GameMemory.getMemory(Double.parseDouble((String) this.config.getValue(EnumConfig.RAM))));

        engine.reg(gameUpdater);

        Thread updateThread = new Thread(() -> engine.getGameUpdater().start());
        updateThread.start();

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.0D), event -> timelineUpdate(progressBar, updateFileLabel)),
                new KeyFrame(Duration.seconds(0.1D)));

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
}
