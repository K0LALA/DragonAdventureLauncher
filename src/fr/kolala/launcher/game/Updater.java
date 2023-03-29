package fr.kolala.launcher.game;

import fr.trxyy.alternative.alternative_api_uiv2.components.LauncherLabel;
import fr.trxyy.alternative.alternative_api_uiv2.components.LauncherProgressBar;
import fr.trxyy.alternative.alternative_apiv2.base.GameEngine;
import fr.trxyy.alternative.alternative_apiv2.base.IScreen;
import fr.trxyy.alternative.alternative_apiv2.updater.GameUpdater;
import fr.trxyy.alternative.alternative_authv2.base.Session;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

import java.io.File;
import java.util.Objects;

public class Updater extends IScreen {

    private final GameEngine engine;
    private final Session session;
    private GameUpdater updater;

    private final LauncherProgressBar progressBar;
    private final LauncherLabel updateFileLabel;

    public Updater(GameEngine engine, Session session, LauncherProgressBar progressBar, LauncherLabel updateFileLabel) {
        this.engine = engine;
        this.session = session;
        this.progressBar = progressBar;
        this.updateFileLabel = updateFileLabel;
    }

    public void update()
    {
        File jsonFile = downloadVersion(engine.getGameLinks().getJsonUrl(), engine);
        Thread updateThread = new Thread() {
            public void run() {
                updater = new GameUpdater(prepareGameUpdate(updater, engine, session, jsonFile), engine);
                engine.reg(updater);

                fadeIn(progressBar, 300);
                fadeIn(updateFileLabel, 300);

                Timeline t = new Timeline(new KeyFrame(Duration.seconds(0.0D), new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent event) {
                        double percent = (engine.getGameUpdater().downloadedFiles * 100.0D / engine.getGameUpdater().filesToDownload / 100.0D);
                        progressBar.setProgress(percent);
                        updateFileLabel.setText(engine.getGameUpdater().getCurrentFile() + " — " + percent * 100 + "%");
                    }
                }), new KeyFrame(Duration.seconds(0.1D)));
                t.setCycleCount(Animation.INDEFINITE);
                t.play();
                downloadGameAndRun(updater, session);
            }
        };
        updateThread.start();
    }

    // TODO récupérer les fichiers sur le FTP
    // Regarder dans le channel principal de Nitro :) pour les credentials
    /*
    import org.apache.commons.net.ftp.FTPSClient;

    public class FTPSTest {
       public static void main(String[] args) {
          String server = "ftp.example.com";
          int port = 21;
          String username = "yourusername";
          String password = "yourpassword";

          FTPSClient ftpsClient = new FTPSClient("TLS", true);

          try {
             ftpsClient.connect(server, port);
             ftpsClient.login(username, password);

             // Do something with the FTP connection...

             ftpsClient.logout();
             ftpsClient.disconnect();
          } catch (Exception e) {
             e.printStackTrace();
          }
       }
    }
     */
}
