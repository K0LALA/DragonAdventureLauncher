package fr.kolala.launcher.utils;

import fr.trxyy.alternative.alternative_api.utils.FontLoader;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class NodeUtil {
    public static void translateTransition(Node object, int time, Orientation orientation, double movement) {
        TranslateTransition translate = new TranslateTransition(Duration.millis(time), object);
        if(orientation == Orientation.HORIZONTAL)
            translate.setByX(movement);
        else
            translate.setByY(movement);
        translate.play();
    }

    public static void fadeDisable(Node object, int fadeTime, int translateTime, Orientation orientation, double translateMovement) {
        object.setDisable(true);
        fade(object, fadeTime, 0);
        translateTransition(object, translateTime, orientation, translateMovement);
    }

    public static void fadeEnable(Node object, int fadeTime, int translateTime, Orientation orientation, double translateMovement) {
        fade(object, fadeTime, 1);
        translateTransition(object, translateTime, orientation, translateMovement);
        object.setDisable(false);
    }

    public static void fade(Node object, int time, double finish) {
        FadeTransition ft = new FadeTransition(Duration.millis(time), object);
        ft.setFromValue(object.getOpacity());
        ft.setToValue(finish);
        ft.play();
    }

    public static void setSize(Control object, double width, double height) {
        object.setMinSize(width, height);
        object.setPrefSize(width, height);
        object.setMaxSize(width, height);
    }

    public static void setPosition(Control object, double posX, double posY) {
        object.setLayoutX(posX);
        object.setLayoutY(posY);
    }

    public static Font font(float size) {
        return FontLoader.loadFont("assets/Roboto-Light.ttf", "Roboto-Light", size);
    }

    public static void setHover(Control object, EventHandler<? super MouseEvent> value) {
        object.onMouseEnteredProperty().set(value);
    }

    public static void setUnHover(Control object, EventHandler<? super MouseEvent> value) {
        object.onMouseExitedProperty().set(value);
    }
}
