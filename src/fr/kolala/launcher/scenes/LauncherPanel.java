package fr.kolala.launcher.scenes;

import fr.kolala.launcher.game.Logger;
import fr.kolala.launcher.game.Updater;
import fr.kolala.launcher.game.server.ServerStatus;
import fr.trxyy.alternative.alternative_api.GameEngine;
import fr.trxyy.alternative.alternative_api.GameLinks;
import fr.trxyy.alternative.alternative_api.GameStyle;
import fr.trxyy.alternative.alternative_api.utils.FontLoader;
import fr.trxyy.alternative.alternative_api.utils.config.EnumConfig;
import fr.trxyy.alternative.alternative_api.utils.config.LauncherConfig;
import fr.trxyy.alternative.alternative_api_ui.base.IScreen;
import fr.trxyy.alternative.alternative_api_ui.components.*;
import fr.trxyy.alternative.alternative_auth.base.GameAuth;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Slider;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.awt.*;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.HashMap;

public class LauncherPanel extends IScreen {

    public static final String redColor = "#E57373";
    public static final String whiteColor = "#D3D3D3";
    public static final String grayColor = "#7F7F7F";

    private final Pane root;
    private final GameEngine engine;
    private GameAuth gameAuth;
    private final LauncherConfig config;
    private final Logger logger;
    private final ServerStatus serverStatus;
    private final Updater updater;

    /* Login Pane */
    private Rectangle loginPane;
    private LauncherButton microsoftLogin;
    private LauncherLabel crackLabel;
    private LauncherTextField usernameField;
    private LauncherLabel validUsername;
    private LauncherButton loginButton;

    /* Server Pane */
    private Rectangle serverPane;
    private LauncherLabel serverName;
    private LauncherButton refreshButton;
    private LauncherLabel players;

    /* Play Pane */
    private Rectangle playPane;
    private LauncherLabel backButton;
    private LauncherLabel usernameLabel;
    private Slider memorySlider;
    private LauncherLabel memoryUsage;
    private LauncherButton playButton;
    private LauncherProgressBar progressBar;
    private LauncherLabel updateFileLabel;

    /* Side Pane */
    private Rectangle sidePane;
    private LauncherButton mapButton;
    private LauncherButton modsButton;
    private LauncherButton discordButton;

    private boolean isLoginValid = true;
    private String username = "";

    public LauncherPanel (Pane root, GameEngine gameEngine) {
        this.root = root;
        this.engine = gameEngine;
        this.config = new LauncherConfig(engine);
        this.config.loadConfiguration();
        this.logger = new Logger(gameEngine);
        this.serverStatus = new ServerStatus("adventuredragon.craft.gg");
        initConfig(root);
        this.updater = new Updater(engine, config);

        LauncherImage backgroundImage = new LauncherImage(root, loadImage(engine, "assets/background_blurred.png"));
        backgroundImage.setSize(engine.getWidth(), engine.getHeight());

        LauncherButton closeButton = new LauncherButton(root);
        LauncherImage closeImage = new LauncherImage(root, getResourceLocation().loadImage(engine, "assets/close.png"));
        closeImage.setSize(48, 48);
        closeButton.setGraphic(closeImage);
        closeButton.setBackground(null);
        closeButton.setPosition(engine.getWidth() - 65, 2);
        closeButton.setSize(48, 48);
        closeButton.setOnAction(event -> System.exit(0));

        LauncherLabel title = new LauncherLabel(root);
        title.setText("Dragon Adventure – L'ère de la magie");
        title.setFont(font(24F));
        title.setSize(500, (int) title.getHeight());
        title.setPosition(engine.getWidth() / 2d - 250, 20);
        title.addStyle("-fx-text-fill: " + whiteColor + ";");
        title.setAlignment(Pos.CENTER);

        loginPane();
        playPane();
    }

    private void initConfig (Pane root) {
        boolean rememberMe = (boolean) config.getValue(EnumConfig.REMEMBER_ME);
        boolean useMicrosoft = (boolean) config.getValue(EnumConfig.USE_MICROSOFT);
        String username_ = (String) config.getValue(EnumConfig.USERNAME);

        if (rememberMe)
        {
            if (useMicrosoft)
            {
                //Authentification Microsoft
                gameAuth = logger.microsoftAuthentification();
                username = gameAuth.getSession().getUsername();
                usernameLabel.setText(" — " + username);
                setPlayerHead(root, username);
            }
            else
            {
                this.usernameField.setText(username_);
                username = username_;
                gameAuth = logger.crackAuthentification(username_);
                usernameLabel.setText(" — " + username_);
                setPlayerHead(root, "MHF_Steve");
                playPanelTransition();
            }
        }

        GameLinks links = new GameLinks("http://127.0.0.1/launcher/", "1.16.5-forge-36.2.34.json");
        engine.reg(links);
        engine.setGameStyle(GameStyle.FORGE_1_13_HIGHER);
    }

    private void loginPanelTransition() {
        fadeEnable(this.loginPane, 300, 350, Orientation.HORIZONTAL, 500);
        fadeEnable(this.microsoftLogin, 300, 350, Orientation.HORIZONTAL, 500);
        fadeEnable(this.crackLabel, 300, 350, Orientation.HORIZONTAL, 500);
        fadeEnable(this.usernameField, 300, 350, Orientation.HORIZONTAL, 500);
        if(!isLoginValid)
            fadeEnable(this.validUsername, 300, 350, Orientation.HORIZONTAL, 500);
        else
            translateTransition(validUsername, 350, Orientation.HORIZONTAL, 500);
        fadeEnable(this.loginButton, 300, 350, Orientation.HORIZONTAL, 500);

        fadeDisable(this.serverPane, 300, 350, Orientation.HORIZONTAL, 500);
        fadeDisable(this.serverName, 300, 350, Orientation.HORIZONTAL, 500);
        fadeDisable(this.refreshButton, 300, 350, Orientation.HORIZONTAL, 500);
        fadeDisable(this.players, 300, 350, Orientation.HORIZONTAL, 500);

        fadeDisable(this.playPane, 300, 350, Orientation.HORIZONTAL, 500);
        fadeDisable(this.usernameLabel, 300, 350, Orientation.HORIZONTAL, 500);
        fadeDisable(this.backButton, 300, 350, Orientation.HORIZONTAL, 500);
        fadeDisable(this.playButton, 300, 350, Orientation.HORIZONTAL, 500);
        fadeDisable(this.memorySlider, 300, 350, Orientation.HORIZONTAL, 500);
        fadeDisable(this.memoryUsage, 300, 350, Orientation.HORIZONTAL, 500);

        fadeDisable(this.sidePane, 300, 350, Orientation.HORIZONTAL, -70);
        fadeDisable(this.mapButton, 300, 350, Orientation.HORIZONTAL, -70);
        fadeDisable(this.modsButton, 300, 350, Orientation.HORIZONTAL, -70);
        fadeDisable(this.discordButton, 300, 350, Orientation.HORIZONTAL, -70);
    }

    private void playPanelTransition() {
        fadeDisable(this.loginPane, 300, 350, Orientation.HORIZONTAL, -500);
        fadeDisable(this.microsoftLogin, 300, 350, Orientation.HORIZONTAL, -500);
        fadeDisable(this.crackLabel, 300, 350, Orientation.HORIZONTAL, -500);
        fadeDisable(this.usernameField, 300, 350, Orientation.HORIZONTAL, -500);
        fadeDisable(this.validUsername, 300, 350, Orientation.HORIZONTAL, -500);
        fadeDisable(this.loginButton, 300, 350, Orientation.HORIZONTAL, -500);

        fadeEnable(this.serverPane, 300, 350, Orientation.HORIZONTAL, -500);
        fadeEnable(this.serverName, 300, 350, Orientation.HORIZONTAL, -500);
        fadeEnable(this.refreshButton, 300, 350, Orientation.HORIZONTAL, -500);
        fadeEnable(this.players, 300, 350, Orientation.HORIZONTAL, -500);

        fadeEnable(this.playPane, 300, 350, Orientation.HORIZONTAL, -500);
        fadeEnable(this.usernameLabel, 300, 350, Orientation.HORIZONTAL, -500);
        fadeEnable(this.backButton, 300, 350, Orientation.HORIZONTAL, -500);
        fadeEnable(this.playButton, 300, 350, Orientation.HORIZONTAL, -500);
        fadeEnable(this.memorySlider, 300, 350, Orientation.HORIZONTAL, -500);
        fadeEnable(this.memoryUsage, 300, 350, Orientation.HORIZONTAL, -500);

        fadeEnable(this.sidePane, 300, 350, Orientation.HORIZONTAL, 70);
        fadeEnable(this.mapButton, 300, 350, Orientation.HORIZONTAL, 70);
        fadeEnable(this.modsButton, 300, 350, Orientation.HORIZONTAL, 70);
        fadeEnable(this.discordButton, 300, 350, Orientation.HORIZONTAL, 70);
    }

    private void loginPane() {
        this.loginPane = new Rectangle(engine.getWidth() / 2d - 138, engine.getHeight() / 2d - 145, 276, 240);
        this.loginPane.setFill(Color.rgb(20, 20, 20, 0.35));
        this.loginPane.setArcWidth(10.0);
        this.loginPane.setArcHeight(10.0);
        root.getChildren().add(loginPane);

        this.microsoftLogin = new LauncherButton(root);
        this.microsoftLogin.setBounds(this.engine.getWidth() / 2 - 118, this.engine.getHeight() / 2 - 125, 236, 66);
        LauncherImage microsoftImage = new LauncherImage(root, loadImage(engine, "assets/microsoft.png"));
        microsoftImage.setSize(216, 46);
        this.microsoftLogin.setGraphic(microsoftImage);
        this.microsoftLogin.addStyle("-fx-border-radius: 3px;");
        this.microsoftLogin.addStyle("-fx-background-color: rgba(25, 25, 25, 0.5);");
        this.microsoftLogin.addStyle("-fx-border-width: 2px;");
        this.microsoftLogin.addStyle("-fx-border-color: " + grayColor + ";");
        this.microsoftLogin.setOnAction(event -> {
            gameAuth = logger.microsoftAuthentification();
            username = gameAuth.getSession().getUsername();
            config.updateValue("useMicrosoft", true);
            config.updateValue("username", username);
            usernameLabel.setText(" — " + username);
            setPlayerHead(root, username);
        });

        this.crackLabel = new LauncherLabel(root);
        this.crackLabel.setText("━━━━━━━ Crack ━━━━━━━");
        this.crackLabel.setPosition(engine.getWidth() / 2d - 120, engine.getHeight() / 2d - 50);
        this.crackLabel.setFont(font(14F));
        this.crackLabel.addStyle("-fx-text-fill: " + whiteColor + ";");

        this.usernameField = new LauncherTextField(root);
        this.usernameField.setVoidText("Nom d'utilisateur");
        this.usernameField.setPosition(engine.getWidth() / 2 - 113, engine.getHeight() / 2 - 25);
        this.usernameField.setSize(226, 30);
        this.usernameField.setFont(font(14F));
        this.usernameField.addStyle("-fx-text-fill: " + whiteColor + ";");
        this.usernameField.addStyle("-fx-border-radius: 3px;");
        this.usernameField.addStyle("-fx-background-radius: 0 0 0 0;");
        this.usernameField.addStyle("-fx-background-color: rgba(25, 25, 25, 0.5);");
        this.usernameField.textProperty().addListener((observable, oldValue, newValue) -> updateUsernameFieldValidity(newValue.length(), validUsername));
        this.usernameField.setTextFormatter(new TextFormatter<String>(change -> {
            if(change.getControlNewText().matches("[abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_]*"))
                return change;
            else
                return null;
        }));

        LauncherImage notValid = new LauncherImage(root, loadImage(engine, "assets/red_cross.png"));
        notValid.setSize(16, 16);
        this.validUsername = new LauncherLabel(root);
        this.validUsername.setText("Le pseudo doit avoir 3 caractères min.");
        this.validUsername.setGraphic(notValid);
        this.validUsername.setPosition(engine.getWidth() / 2d - 113, engine.getHeight() / 2d + 7);
        this.validUsername.setFont(font(11F));
        this.validUsername.addStyle("-fx-text-fill: " + redColor + ";");
        this.validUsername.addStyle("-fx-font-weight: bold;");
        this.validUsername.setOpacity(0.0);

        //TODO: bouton remember me

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
        this.loginButton.setHover(event -> {
            loginButton.setOpacity(0.3);
            loginButton.setFont(font(18F));
        });
        this.loginButton.setUnHover(event -> {
            loginButton.setOpacity(0.3);
            loginButton.setFont(font(18F));
        });
        this.loginButton.setOnMousePressed(event -> {
            loginButton.setFont(font(18F));
            if(isLoginValid && usernameField.getText().length() != 0)
            {
                gameAuth = logger.crackAuthentification(usernameField.getText());
                username = gameAuth.getSession().getUsername();
                config.updateValue("useMicrosoft", false);
                config.updateValue("username", username);
                usernameLabel.setText(" — " + username);
                setPlayerHead(root, "MHF_Steve");
                playPanelTransition();
            }
        });
    }

    private void playPane() {
        /* Server Pane */
        this.serverPane = new Rectangle(engine.getWidth() / 2d - 138 + 500, engine.getHeight() / 2d - 230, 276, 75);
        this.serverPane.setFill(Color.rgb(20, 20, 20, 0.35));
        this.serverPane.setArcWidth(10.0);
        this.serverPane.setArcHeight(10.0);
        this.serverPane.setOpacity(0.0);
        this.serverPane.setDisable(true);
        root.getChildren().add(serverPane);

        this.serverName = new LauncherLabel(root);
        this.serverName.setText("Dragon Adventure");
        this.serverName.setFont(font(18F));
        this.serverName.setSize(200, 50);
        this.serverName.setPosition(engine.getWidth() / 2d - 100 + 500, engine.getHeight() / 2d - 235);
        this.serverName.addStyle("-fx-text-fill: " + whiteColor + ";");
        this.serverName.setAlignment(Pos.CENTER);
        this.serverName.setOpacity(0.0);
        this.serverName.setDisable(true);

        this.refreshButton = new LauncherButton(root);
        LauncherImage refreshImage = new LauncherImage(root, getResourceLocation().loadImage(engine, "assets/refresh.png"));
        refreshImage.setSize(24, 24);
        this.refreshButton.setGraphic(refreshImage);
        this.refreshButton.setBackground(null);
        this.refreshButton.setPosition(engine.getWidth() / 2 + 100 + 500, engine.getHeight() / 2 - 195);
        this.refreshButton.setSize(24, 24);
        this.refreshButton.setOnAction(event -> updateServerStatus());
        this.refreshButton.setOpacity(0.0);
        this.refreshButton.setDisable(true);

        this.players = new LauncherLabel(root);
        this.players.setFont(font(16F));
        this.players.setSize(100, (int) players.getHeight());
        this.players.setPosition(engine.getWidth() / 2 - 50 + 500, engine.getHeight() / 2 - 190);
        this.players.addStyle("-fx-text-fill: " + whiteColor + ";");
        this.players.setAlignment(Pos.CENTER);
        this.players.setOpacity(0.0);
        this.players.setDisable(true);
        updateServerStatus();

        /* Play Pane */
        this.playPane = new Rectangle(engine.getWidth() / 2d - 138 + 500, engine.getHeight() / 2d - 145, 276, 240);
        this.playPane.setFill(Color.rgb(20, 20, 20, 0.35));
        this.playPane.setArcWidth(10.0);
        this.playPane.setArcHeight(10.0);
        this.playPane.setOpacity(0.0);
        this.playPane.setDisable(true);
        root.getChildren().add(playPane);

        this.usernameLabel = new LauncherLabel(root);
        this.usernameLabel.setText("Nom d'utilisateur");
        this.usernameLabel.setFont(font(18F));
        this.usernameLabel.setPosition(engine.getWidth() / 2 - 125 + 500, engine.getHeight() / 2 - 135);
        this.usernameLabel.setSize(250, (int) usernameLabel.getHeight());
        this.usernameLabel.addStyle("-fx-text-fill: " + whiteColor + ";");
        this.usernameLabel.setAlignment(Pos.CENTER);
        this.usernameLabel.setOpacity(0);
        this.usernameLabel.setDisable(true);

        this.memorySlider = new Slider(2000, (double) ((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize() / 1000000 - 1000, 4000);
        this.setSize(memorySlider, 150, 30);
        this.memorySlider.setLayoutX(engine.getWidth() / 2d - 118 + 500);
        this.memorySlider.setLayoutY(engine.getHeight() / 2d - 100);
        this.memorySlider.setStyle("-fx-base: #F38B30; -fx-control-inner-background: #121414;");
        this.memorySlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            String value = newValue.toString().substring(0, newValue.toString().indexOf('.'));
            memoryUsage.setText(value + " Mb");
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
        this.playButton.setOnAction(event -> {
            playButton.setFont(font(20F));
            fade(playButton, 150, 0.3);
            playButton.setDisable(true);
            fade(backButton, 150, 0);
            backButton.setDisable(true);
            fade(memorySlider, 150, 0.3);
            memorySlider.setDisable(true);

            HashMap<String, String> configMap = new HashMap<String, String>();
            configMap.put("allocatedram", String.valueOf(memorySlider.getValue()));
            configMap.put("usediscord", "" + true);
            config.updateValues(configMap);

            updater.updateGame(gameAuth, progressBar, updateFileLabel);
        });
        this.playButton.setOpacity(0.0);
        this.playButton.setDisable(true);

        /* Side Pane */
        this.sidePane = new Rectangle(-70, 0, 70, engine.getHeight());
        this.sidePane.setFill(Color.rgb(20, 20, 20, 0.35));
        this.sidePane.setOpacity(0.0);
        this.sidePane.setDisable(true);
        root.getChildren().add(sidePane);

        this.mapButton = new LauncherButton(root);
        LauncherImage mapImage = new LauncherImage(root, getResourceLocation().loadImage(engine, "assets/map.png"));
        mapImage.setSize(50, 50);
        this.mapButton.setGraphic(mapImage);
        this.mapButton.setPosition(10 - 80, engine.getHeight() / 2 - 95);
        this.mapButton.setSize(50, 50);
        this.mapButton.setBackground(null);
        this.mapButton.setOnAction(event -> {
            try {
                Desktop.getDesktop().browse(new URL("http://adventuredragon.craft.gg:40009/").toURI());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        this.mapButton.setOpacity(0.0);
        this.mapButton.setDisable(true);

        //TODO: afficher la page d'activation des mods clients optionnels
        this.modsButton = new LauncherButton(root);
        LauncherImage modsImage = new LauncherImage(root, getResourceLocation().loadImage(engine, "assets/mods.png"));
        modsImage.setSize(50, 50);
        this.modsButton.setGraphic(modsImage);
        this.modsButton.setPosition(10 - 80, engine.getHeight() / 2 - 25);
        this.modsButton.setSize(50, 50);
        this.modsButton.setBackground(null);
        this.modsButton.setOpacity(0.0);
        this.modsButton.setDisable(true);

        this.discordButton = new LauncherButton(root);
        LauncherImage discordImage = new LauncherImage(root, getResourceLocation().loadImage(engine, "assets/discord.png"));
        discordImage.setSize(50, 50);
        this.discordButton.setGraphic(discordImage);
        this.discordButton.setPosition(10 - 80, engine.getHeight() / 2 + 45);
        this.discordButton.setSize(50, 50);
        this.discordButton.setBackground(null);
        this.discordButton.setOnAction(event -> {
            try {
                Desktop.getDesktop().browse(new URL("https://discord.gg/jSZcjvBcnK").toURI());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        this.discordButton.setOpacity(0.0);
        this.discordButton.setDisable(true);

        /* Back Button */
        // Put in last to be on top of everything
        this.backButton = new LauncherLabel(root);
        this.backButton.setText("← Retour");
        this.backButton.setPosition(8 + 500, 3);
        this.backButton.setFont(font(14F));
        this.backButton.addStyle("-fx-border-color: " + whiteColor + ", transparent;");
        this.backButton.addStyle("-fx-border-width: 0 0 1 0, 0 0 1 0;");
        this.backButton.addStyle("-fx-border-insets: 0 0 1 0, 0 0 1 0;");
        this.backButton.addStyle("-fx-text-fill: " + whiteColor + ";");
        this.backButton.setOpacity(0);
        this.backButton.setDisable(true);
        this.backButton.setHover(event -> backButton.addStyle("-fx-cursor: hand;"));
        this.backButton.setUnHover(event -> backButton.addStyle("-fx-cursor: default;"));
        this.backButton.setOnMousePressed(event -> loginPanelTransition());
    }

    private void updateServerStatus () {
        serverStatus.update();
        Circle online = new Circle(5);

        if(serverStatus.isOnline())
        {
            online.setFill(Color.rgb(108, 172, 25));
            this.players.setText(String.valueOf(serverStatus.getOnlinePlayers()) + "/" + String.valueOf(serverStatus.getMaxPlayer()));
        }
        else
            online.setFill(Color.rgb(229, 32, 32));
        this.serverName.setGraphic(online);
    }

    private void setPlayerHead (Pane root, String username) {
        LauncherImage profileImage = new LauncherImage(root, new Image("https://minotar.net/armor/bust/" + username + "/25.png"));
        this.usernameLabel.setGraphic(profileImage);
    }

    private void translateTransition(Node object, int time, Orientation orientation, double movement) {
        TranslateTransition translate = new TranslateTransition(Duration.millis(time), object);
        if(orientation == Orientation.HORIZONTAL)
            translate.setByX(movement);
        else
            translate.setByY(movement);
        translate.play();
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

    private void setSize(Control object, double width, double height) {
        object.setMinSize(width, height);
        object.setPrefSize(width, height);
        object.setMaxSize(width, height);
    }

    private void updateUsernameFieldValidity(int length, LauncherLabel validUsername) {
        if(length < 3)
        {
            if(isLoginValid)
            {
                isLoginValid = false;

                usernameField.addStyle("-fx-text-fill: " + redColor + ";");
                fadeIn(validUsername, 150);
                validUsername.setText("Le pseudo doit avoir 3 caractères min");

                fade(loginButton, 150, 0.3);
                loginButton.setHover(event -> {
                    loginButton.setOpacity(0.3);
                    loginButton.setFont(font(18F));
                });
                loginButton.setUnHover(event -> {
                    loginButton.setOpacity(0.3);
                    loginButton.setFont(font(18F));
                });
            }
        }
        else if(length > 16) {

            if(isLoginValid)
            {
                isLoginValid = false;

                usernameField.addStyle("-fx-text-fill: " + redColor + ";");
                fadeIn(validUsername, 150);
                validUsername.setText("Le pseudo peut avoir 16 caractères max");

                fade(loginButton, 150, 0.3);
                loginButton.setHover(event -> {
                    loginButton.setOpacity(0.3);
                    loginButton.setFont(font(18F));
                });
                loginButton.setUnHover(event -> {
                    loginButton.setOpacity(0.3);
                    loginButton.setFont(font(18F));
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
                loginButton.setHover(event -> {
                    loginButton.setOpacity(0.8);
                    loginButton.setFont(font(18F));
                });
                loginButton.setUnHover(event -> {
                    loginButton.setOpacity(1);
                    loginButton.setFont(font(18F));
                });
            }
        }
    }

    private Font font(float size)
    {
        return FontLoader.loadFont("assets/Roboto-Light.ttf", "Roboto-Light", size);
    }
}
