package fr.kolala.launcher.scenes;

import fr.kolala.launcher.game.Updater;
import fr.trxyy.alternative.alternative_api_uiv2.components.*;
import fr.trxyy.alternative.alternative_apiv2.base.GameEngine;
import fr.trxyy.alternative.alternative_apiv2.base.IScreen;
import fr.trxyy.alternative.alternative_apiv2.settings.GameInfos;
import fr.trxyy.alternative.alternative_apiv2.settings.GameSaver;
import fr.trxyy.alternative.alternative_apiv2.updater.GameUpdater;
import fr.trxyy.alternative.alternative_apiv2.utils.FontLoader;
import fr.trxyy.alternative.alternative_authv2.base.GameAuth;
import fr.trxyy.alternative.alternative_authv2.base.Session;
import javafx.animation.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainPanel extends IScreen {

    // https://coolors.co/121414-d8dcff-f38b30
    private final String redColor = "#E57373";
    private final String whiteColor = "#D3D3D3";
    private final String grayColor = "#7F7F7F";

    private final GameEngine engine;
    private GameAuth gameAuth;
    private Session gameSession;
    private GameUpdater updater;
    private Thread updateThread;

    private final LauncherImage backgroundImage;

    /* Login Pane */
    private final Rectangle loginPanel;
    private final LauncherButton microsoftLogin;
    private final LauncherLabel crackLabel;
    // private LauncherLabel identifiersLabel;
    private final LauncherTextField usernameField;
    private final LauncherLabel validUsername;
    // private LauncherPasswordField passwordField;
    private final LauncherButton loginButton;

    /* Play Pane */
    private final Rectangle playPanel;
    private final LauncherLabel backButton;
    private ImageView profileImage;
    private final LauncherLabel usernameLabel;
    private final Slider memorySlider;
    private final LauncherLabel memoryUsage;
    private final LauncherButton playButton;
    private final LauncherProgressBar progressBar;
    private final LauncherLabel updateFileLabel;
    // Ajouter tous les composants relatifs à l'update

    private boolean isLoginValid = true;

    public MainPanel(Pane root, GameEngine gameEngine) {
        this.engine = gameEngine;

        this.backgroundImage = new LauncherImage(root, loadImage(engine, "assets/background_blurred.png"));
        this.backgroundImage.setSize(engine.getWidth(), engine.getHeight());

        this.drawRect(root, 0, 0, engine.getWidth(), engine.getHeight(), Color.rgb(42, 42, 42, 0.2));

        /* Login Pane */
        this.loginPanel = new Rectangle(engine.getWidth() / 2d - 138, engine.getHeight() / 2d - 145, 276, 240);
        this.loginPanel.setFill(Color.rgb(20, 20, 20, 0.35));
        this.loginPanel.setArcHeight(10);
        this.loginPanel.setArcWidth(10);
        root.getChildren().add(loginPanel);

        this.microsoftLogin = new LauncherButton(root);
        this.microsoftLogin.setBounds(this.engine.getWidth() / 2 - 118, this.engine.getHeight() / 2 - 125, 236, 66);
        LauncherImage microsoftImage = new LauncherImage(root, loadImage(engine, "assets/microsoft.png"));
        microsoftImage.setSize(216, 46);
        this.microsoftLogin.setGraphic(microsoftImage);
        this.microsoftLogin.addStyle("-fx-border-radius: 3px;");
        this.microsoftLogin.addStyle("-fx-background-color: rgba(25, 25, 25, 0.5);");
        this.microsoftLogin.addStyle("-fx-border-width: 2px;");
        this.microsoftLogin.addStyle("-fx-border-color: " + grayColor + ";");
        this.microsoftLogin.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                gameAuth = new GameAuth();
                showMicrosoftAuth(engine, gameAuth);
                if(gameAuth.isLogged())
                {
                    gameSession = gameAuth.getSession();
                    usernameLabel.setText(" ─ " + gameSession.getUsername());
                    profileImage = new ImageView("https://minotar.net/armor/bust/" + gameSession.getUuid() + "/25.png");
                    usernameLabel.setGraphic(profileImage);
                    nextPanel();
                }
            }
        });

        this.crackLabel = new LauncherLabel(root);
        this.crackLabel.setText("━━━━━━━ Crack ━━━━━━━");
        this.crackLabel.setPosition(engine.getWidth() / 2 - 120, engine.getHeight() / 2 - 50);
        this.crackLabel.setFont(font(14F));
        this.crackLabel.addStyle("-fx-text-fill: " + whiteColor + ";");

        /* this.identifiersLabel = new LauncherLabel(root);
        this.identifiersLabel.setText("━━━━━━ Identifiants ━━━━━━");
        this.identifiersLabel.setPosition(engine.getWidth() / 2 - 122, engine.getHeight() / 2 - 135);
        this.identifiersLabel.setFont(this.font(14F));
        this.identifiersLabel.addStyle("-fx-text-fill: " + whiteColor + ";"); */

        this.usernameField = new LauncherTextField(root);
        this.usernameField.setVoidText("Nom d'utilisateur");
        this.usernameField.setPosition(engine.getWidth() / 2 - 113, engine.getHeight() / 2 - 25);
        this.usernameField.setSize(226, 30);
        this.usernameField.setFont(font(14F));
        this.usernameField.addStyle("-fx-text-fill: " + whiteColor + ";");
        this.usernameField.addStyle("-fx-border-radius: 3px;");
        this.usernameField.addStyle("-fx-background-radius: 0 0 0 0;");
        this.usernameField.addStyle("-fx-background-color: rgba(25, 25, 25, 0.5);");
        this.usernameField.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue.length() < 3)
                {
                    if(isLoginValid)
                    {
                        isLoginValid = false;

                        usernameField.addStyle("-fx-text-fill: " + redColor + ";");
                        fadeIn(validUsername, 150);
                        validUsername.setText("Le pseudo doit avoir 3 caractères min");

                        fade(loginButton, 150, 0.3);
                        loginButton.setHover(new EventHandler<MouseEvent>() {
                            public void handle(MouseEvent event) {
                                loginButton.setOpacity(0.3);
                                loginButton.setFont(font(18F));
                            }
                        });
                        loginButton.setUnHover(new EventHandler<MouseEvent>() {
                            public void handle(MouseEvent event) {
                                loginButton.setOpacity(0.3);
                                loginButton.setFont(font(18F));
                            }
                        });
                    }
                }
                else if(newValue.length() > 16) {

                    if(isLoginValid)
                    {
                        isLoginValid = false;

                        usernameField.addStyle("-fx-text-fill: " + redColor + ";");
                        fadeIn(validUsername, 150);
                        validUsername.setText("Le pseudo peut avoir 16 caractères max");

                        fade(loginButton, 150, 0.3);
                        loginButton.setHover(new EventHandler<MouseEvent>() {
                            public void handle(MouseEvent event) {
                                loginButton.setOpacity(0.3);
                                loginButton.setFont(font(18F));
                            }
                        });
                        loginButton.setUnHover(new EventHandler<MouseEvent>() {
                            public void handle(MouseEvent event) {
                                loginButton.setOpacity(0.3);
                                loginButton.setFont(font(18F));
                            }
                        });
                    }
                }
                else {
                    if(!isLoginValid)
                    {
                        isLoginValid = true;

                        usernameField.addStyle("-fx-text-fill: " + whiteColor + ";");
                        fadeOut(validUsername, 150);

                        fade(loginButton, 150, 1);
                        loginButton.setHover(new EventHandler<MouseEvent>() {
                            public void handle(MouseEvent event) {
                                loginButton.setOpacity(0.8);
                                loginButton.setFont(font(18F));
                            }
                        });
                        loginButton.setUnHover(new EventHandler<MouseEvent>() {
                            public void handle(MouseEvent event) {
                                loginButton.setOpacity(1);
                                loginButton.setFont(font(18F));
                            }
                        });
                    }
                }
            }
        });

        LauncherImage notValid = new LauncherImage(root, loadImage(engine, "assets/red_cross.png"));
        notValid.setSize(16, 16);
        this.validUsername = new LauncherLabel(root);
        this.validUsername.setText("Le pseudo doit avoir 3 caractères min");
        this.validUsername.setGraphic(notValid);
        this.validUsername.setPosition(engine.getWidth() / 2 - 113, engine.getHeight() / 2 + 7);
        this.validUsername.setFont(font(11F));
        this.validUsername.addStyle("-fx-text-fill: " + redColor + ";");
        this.validUsername.addStyle("-fx-font-weight: bold;");
        this.validUsername.setOpacity(0);

        /* this.passwordField = new LauncherPasswordField(root);
        LauncherImage microsoftLogo = new LauncherImage(root, loadImage(engine, "assets/microsoft_.png"));
        microsoftLogo.setSize(20, 20);
        microsoftLogo.setBounds(this.engine.getWidth() / 2 - 136, this.engine.getHeight() / 2 - 56, 20, 20);
        microsoftLogo.setOpacity(0.7);
        this.passwordField.setVoidText("Mot de passe");
        this.passwordField.setPosition(engine.getWidth() / 2 - 113, engine.getHeight() / 2 - 61);
        this.passwordField.setSize(226, 30);
        this.passwordField.setFont(font(12F));
        this.passwordField.addStyle("-fx-text-fill: " + whiteColor + ";");
        this.passwordField.addStyle("-fx-border-radius: 3px;");
        this.passwordField.addStyle("-fx-background-radius: 0 0 0 0;");
        this.passwordField.addStyle("-fx-background-color: rgba(25, 25, 25, 0.5);"); */

        this.loginButton = new LauncherButton(root);
        this.loginButton.setText("Se Connecter");
        this.loginButton.setFont(font(18F));
        this.loginButton.setBounds(this.engine.getWidth() / 2 - 100, this.engine.getHeight() / 2 + 25, 200, 50);
        this.loginButton.setOpacity(0.3);
        this.loginButton.addStyle("-fx-font-weight: bold;");
        this.loginButton.addStyle("-fx-border-radius: 3px;");
        this.loginButton.addStyle("-fx-background-color: rgba(25, 25, 25, 0.5);");
        this.loginButton.addStyle("-fx-border-width: 2px;");
        this.loginButton.addStyle("-fx-border-color: " + grayColor + ";");
        this.loginButton.addStyle("-fx-text-fill: " + grayColor + ";");
        this.loginButton.setHover(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                loginButton.setOpacity(0.3);
                loginButton.setFont(font(18F));
            }
        });
        this.loginButton.setUnHover(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                loginButton.setOpacity(0.3);
                loginButton.setFont(font(18F));
            }
        });
        this.loginButton.setOnMousePressed(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                loginButton.setFont(font(18F));
                if(isLoginValid)
                {
                    usernameLabel.setText(" ─ " + usernameField.getText());
                    profileImage = new ImageView("https://minotar.net/armor/bust/MHF_Steve/25.png");
                    usernameLabel.setGraphic(profileImage);
                    gameSession = new Session(usernameField.getText(), UUID.randomUUID().toString(), UUID.randomUUID().toString());
                    nextPanel();
                }
            }
        });


        /* Play Pane */
        this.playPanel = new Rectangle(engine.getWidth() / 2d - 138 + 500, engine.getHeight() / 2d - 145, 276, 240);
        this.playPanel.setFill(Color.rgb(20, 20, 20, 0.35));
        this.playPanel.setArcHeight(10);
        this.playPanel.setArcWidth(10);
        root.getChildren().add(playPanel);
        this.playPanel.setOpacity(0);
        this.playPanel.setDisable(true);

        this.usernameLabel = new LauncherLabel(root);
        this.usernameLabel.setText("Nom d'utilisateur");
        this.usernameLabel.setFont(font(18F));
        this.usernameLabel.setPosition(engine.getWidth() / 2 - 125 + 500, engine.getHeight() / 2 - 135);
        this.usernameLabel.setSize(250, (int) usernameLabel.getHeight());
        this.usernameLabel.addStyle("-fx-text-fill: " + whiteColor + ";");
        this.usernameLabel.setAlignment(Pos.CENTER);
        this.usernameLabel.setOpacity(0);
        this.usernameLabel.setDisable(true);

        this.backButton = new LauncherLabel(root);
        this.backButton.setText("← Retour");
        this.backButton.setPosition(15 + 500, 10);
        this.backButton.setFont(font(14F));
        this.backButton.addStyle("-fx-border-color: " + whiteColor + ", transparent;");
        this.backButton.addStyle("-fx-border-width: 0 0 1 0, 0 0 1 0;");
        this.backButton.addStyle("-fx-border-insets: 0 0 1 0, 0 0 1 0;");
        this.backButton.addStyle("-fx-text-fill: " + whiteColor + ";");
        this.backButton.setOpacity(0);
        this.backButton.setDisable(true);
        this.backButton.setHover(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                backButton.addStyle("-fx-cursor: hand;");
            }
        });
        this.backButton.setUnHover(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                backButton.addStyle("-fx-cursor: default;");
            }
        });
        this.backButton.setOnMousePressed(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                previousPanel();
            }
        });

        this.memorySlider = new Slider(2000, (double) ((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize() / 1000000 - 1000, 4000);
        this.setSize(memorySlider, 150, 30);
        this.memorySlider.setLayoutX(engine.getWidth() / 2d - 118 + 500);
        this.memorySlider.setLayoutY(engine.getHeight() / 2d - 100);
        this.memorySlider.setStyle("-fx-base: #F38B30; -fx-control-inner-background: #121414;");
        this.memorySlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                String value = newValue.toString().substring(0, newValue.toString().indexOf('.'));
                memoryUsage.setText(value + " Mb");
            }
        });
        this.memorySlider.setOpacity(0);
        this.memorySlider.setDisable(true);
        root.getChildren().add(this.memorySlider);

        this.memoryUsage = new LauncherLabel(root);
        this.memoryUsage.setFont(font(16F));
        this.memoryUsage.setText("4000 Mb");
        this.memoryUsage.setSize(75, 30);
        this.memoryUsage.setPosition(engine.getWidth() / 2 + 40 + 500, engine.getHeight() / 2 - 100);
        this.memoryUsage.addStyle("-fx-text-fill: " + whiteColor + ";");
        this.memoryUsage.addStyle("-fx-background-radius: 0 0 0 0;");
        this.memoryUsage.setOpacity(0);
        this.memoryUsage.setDisable(true);

        this.progressBar = new LauncherProgressBar(root);
        this.progressBar.setProgress(0);
        this.progressBar.setSize(engine.getWidth() - 500, 30);
        this.progressBar.setPosition(250, engine.getHeight() - 100);
        this.progressBar.addStyle("-fx-base: rgba(10, 10, 10, 0.2);");
        this.progressBar.addStyle("-fx-accent: " + whiteColor + ";");
        this.progressBar.setOpacity(0);
        this.progressBar.setDisable(true);

        this.updateFileLabel = new LauncherLabel(root);
        this.updateFileLabel.setFont(font(16F));
        this.updateFileLabel.setSize(engine.getWidth() - 100, 30);
        this.updateFileLabel.setPosition(50, engine.getHeight() - 140);
        this.updateFileLabel.setAlignment(Pos.CENTER);
        this.updateFileLabel.addStyle("-fx-text-fill: " + whiteColor + ";");
        this.updateFileLabel.setOpacity(0);
        this.updateFileLabel.setDisable(true);

        //TODO: faire pour que le bouton ait une taille adaptée tout le temps
        this.playButton = new LauncherButton(root);
        this.playButton.setText("JOUER");
        this.playButton.setFont(font(20F));
        this.playButton.setBounds(this.engine.getWidth() / 2 - 118 + 500, this.engine.getHeight() / 2 + 10, 236, 66);
        this.playButton.addStyle("-fx-font-weight: bold;");
        this.playButton.addStyle("-fx-border-radius: 3px;");
        this.playButton.addStyle("-fx-background-color: rgba(25, 25, 25, 0.5);");
        this.playButton.addStyle("-fx-border-width: 2px;");
        this.playButton.addStyle("-fx-border-color: " + grayColor + ";");
        this.playButton.addStyle("-fx-text-fill: " + grayColor + ";");
        this.playButton.setOnMousePressed(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                playButton.setFont(font(20F));
                fade(playButton, 150, 0.3);
                playButton.setDisable(true);
                fade(backButton, 150, 0);
                backButton.setDisable(true);
                fade(memorySlider, 150, 0.3);
                memorySlider.setDisable(true);
                //Updater updater = new Updater(engine, gameSession, progressBar, updateFileLabel);
                //File jsonFile = downloadVersion(engine.getGameLinks().getJsonUrl(), engine);


                try {
                    launchGame(gameSession);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                //update(gameSession, jsonFile);
            }
        });
        this.playButton.setOpacity(0);
        this.playButton.setDisable(true);
    }

    public void launchGame(Session auth) throws IOException {
        String gameDir = "C:/Users/kolal/OneDrive/Documents/dragonadventure";
        String version = "1.16.5";
        String gameJar = version + ".jar";

        ProcessBuilder minecraftBuilder = new ProcessBuilder();
        minecraftBuilder.directory(new File(gameDir));
        minecraftBuilder.command(
                "java",
                gameJar,
                "--gameDir",
                gameDir,
                "--assetsDir",
                gameDir + "/assets",
                "--nativesDirectory",
                gameDir + "/natives",
                "--launcherName",
                "dragonadventure-launcher",
                "--launcherVersion",
                "2.2.6374",
                "--accessToken",
                auth.getToken(),
                "--userProperties",
                "{}",
                "--width",
                "800",
                "--height",
                "600",
                "--username",
                auth.getUsername(),
                "--version",
                version,
                "-Xmx2G"
        );

        Process process = minecraftBuilder.start();
    }

    public void update(Session auth, File jsonFile)
    {
        this.updateThread = new Thread() {
            public void run() {
                updater = new GameUpdater(prepareGameUpdate(updater, engine, auth, jsonFile), engine);
                engine.reg(updater);

                fadeIn(progressBar, 150);
                fadeIn(updateFileLabel, 150);

                Timeline t = new Timeline(new KeyFrame(Duration.seconds(0.0D), new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent event) {
                        double percent = (engine.getGameUpdater().downloadedFiles * 100.0D / engine.getGameUpdater().filesToDownload / 100.0D);
                        progressBar.setProgress(percent);
                        updateFileLabel.setText(engine.getGameUpdater().getCurrentFile());
                    }
                }), new KeyFrame(Duration.seconds(0.1D)));
                t.setCycleCount(Animation.INDEFINITE);
                t.play();
                downloadGameAndRun(updater, auth);
            }
        };
        this.updateThread.start();
    }

    private void previousPanel() {
        fadeEnable(this.loginPanel, 300, 350, Orientation.HORIZONTAL, 500);
        fadeEnable(this.microsoftLogin, 300, 350, Orientation.HORIZONTAL, 500);
        fadeEnable(this.crackLabel, 300, 350, Orientation.HORIZONTAL, 500);
        fadeEnable(this.usernameField, 300, 350, Orientation.HORIZONTAL, 500);
        if(!isLoginValid)
            fadeEnable(this.validUsername, 300, 350, Orientation.HORIZONTAL, 500);
        else
            translateTransition(validUsername, 350, Orientation.HORIZONTAL, 500);
        fadeEnable(this.loginButton, 300, 350, Orientation.HORIZONTAL, 500);

        fadeDisable(this.playPanel, 300, 350, Orientation.HORIZONTAL, 500);
        fadeDisable(this.usernameLabel, 300, 350, Orientation.HORIZONTAL, 500);
        fadeDisable(this.backButton, 300, 350, Orientation.HORIZONTAL, 500);
        fadeDisable(this.playButton, 300, 350, Orientation.HORIZONTAL, 500);
        fadeDisable(this.memorySlider, 300, 350, Orientation.HORIZONTAL, 500);
        fadeDisable(this.memoryUsage, 300, 350, Orientation.HORIZONTAL, 500);
    }

    private void nextPanel() {
        fadeDisable(this.loginPanel, 300, 350, Orientation.HORIZONTAL, -500);
        fadeDisable(this.microsoftLogin, 300, 350, Orientation.HORIZONTAL, -500);
        fadeDisable(this.crackLabel, 300, 350, Orientation.HORIZONTAL, -500);
        fadeDisable(this.usernameField, 300, 350, Orientation.HORIZONTAL, -500);
        fadeDisable(this.validUsername, 300, 350, Orientation.HORIZONTAL, -500);
        fadeDisable(this.loginButton, 300, 350, Orientation.HORIZONTAL, -500);

        fadeEnable(this.playPanel, 300, 350, Orientation.HORIZONTAL, -500);
        fadeEnable(this.usernameLabel, 300, 350, Orientation.HORIZONTAL, -500);
        fadeEnable(this.backButton, 300, 350, Orientation.HORIZONTAL, -500);
        fadeEnable(this.playButton, 300, 350, Orientation.HORIZONTAL, -500);
        fadeEnable(this.memorySlider, 300, 350, Orientation.HORIZONTAL, -500);
        fadeEnable(this.memoryUsage, 300, 350, Orientation.HORIZONTAL, -500);
    }

    private void fadeDisable(Node object, int fadeTime, int translateTime, Orientation orientation, double translateMovement) {
        object.setDisable(true);
        fade(object, fadeTime, 0);
        translateTransition(object, translateTime, orientation, translateMovement);
    }

    private void fadeEnable(Node object, int fadeTime, int translateTime, Orientation orientation, double translateMovement) {
        fade(object, fadeTime, 1);
        translateTransition(object, translateTime, orientation, translateMovement);
        object.setDisable(false);
    }

    private void fade(Node object, int time, double finish) {
        FadeTransition ft = new FadeTransition(Duration.millis(time), object);
        ft.setFromValue(object.getOpacity());
        ft.setToValue(finish);
        ft.play();
    }

    private void translateTransition(Node object, int time, Orientation orientation, double movement) {
        TranslateTransition translate = new TranslateTransition(Duration.millis(time), object);
        if(orientation == Orientation.HORIZONTAL)
            translate.setByX(movement);
        else
            translate.setByY(movement);
        translate.play();
    }

    private void setSize(Control object, double width, double height) {
        object.setMinSize(width, height);
        object.setPrefSize(width, height);
        object.setMaxSize(width, height);
    }

    private Font font(float size)
    {
        return FontLoader.loadFont("assets/Roboto-Light.ttf", "Roboto-Light", size);
    }
}
