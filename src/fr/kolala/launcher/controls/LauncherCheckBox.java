package fr.kolala.launcher.controls;

import fr.kolala.launcher.utils.NodeUtil;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.Pane;



public class LauncherCheckBox extends CheckBox {
    public LauncherCheckBox(Pane root) {
        new LauncherCheckBox(10, 10, 0, 0, root);
    }

    public LauncherCheckBox(int width, int height, int posX, int posY, Pane root) {
        NodeUtil.setSize(this, width, height);
        NodeUtil.setPosition(this, posX, posY);
        NodeUtil.setHover(this, event -> LauncherCheckBox.this.setOpacity(0.8));
        NodeUtil.setUnHover(this, event -> LauncherCheckBox.this.setOpacity(1.0));
        root.getChildren().add(this);
        // Add style for all checkboxes
        root.getScene().getStylesheets().add("resources/rememberme.css");
        LauncherCheckBox.this.getStyleClass().add("rememberme");
    }

    public final void addStyle(String value) {
        String finalValue = this.getStyle() + value;
        this.styleProperty().set(finalValue);
    }
}
