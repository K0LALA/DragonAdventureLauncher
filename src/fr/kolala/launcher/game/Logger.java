package fr.kolala.launcher.game;

import fr.trxyy.alternative.alternative_api.GameEngine;
import fr.trxyy.alternative.alternative_api_ui.LauncherPane;
import fr.trxyy.alternative.alternative_api_ui.base.IScreen;
import fr.trxyy.alternative.alternative_auth.account.AccountType;
import fr.trxyy.alternative.alternative_auth.base.GameAuth;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.UUID;

public class Logger extends IScreen {

    private final GameEngine gameEngine;
    private GameAuth gameAuth;

    public Logger(GameEngine engine) {
        this.gameEngine = engine;
    }

    private Parent createMicrosoftPanel() {
        LauncherPane contentPane = new LauncherPane(gameEngine);
        gameAuth.connectMicrosoft(gameEngine, contentPane);
        return contentPane;
    }

    private void showMicrosoftAuth() {
        Scene scene = new Scene(createMicrosoftPanel());
        Stage stage = new Stage();
        scene.setFill(Color.rgb(255, 0, 255));
        stage.setResizable(false);
        stage.setTitle("Microsoft Authentication");
        stage.setWidth(500);
        stage.setHeight(600);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    public void microsoftAuthentification() {
        gameAuth = new GameAuth(AccountType.MICROSOFT);
        showMicrosoftAuth();
        if(gameAuth.isLogged()) {

            System.out.println("Connexion réussie");
        } else {
            System.out.println("Microsoft authentification failed!");
        }
    }

    public void crackAuthentification(String username) {
        gameAuth = new GameAuth(username, "", AccountType.OFFLINE);
    }
}
