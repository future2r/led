package name.ulbricht.led.api;

import javafx.scene.image.Image;
import name.ulbricht.led.Resources;

public enum Color {
    OFF("off.png"),

    RED("on_red.png"),

    GREEN("on_green.png"),

    BLUE("on_blue.png");

    private Image image;

    Color(String imageName) {
        this.image = Resources.getImage(imageName);
    }

    public Image getImage() {
        return this.image;
    }
}
