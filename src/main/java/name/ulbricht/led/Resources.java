package name.ulbricht.led;

import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class Resources {

    public static final ResourceBundle BUNDLE = ResourceBundle.getBundle("name.ulbricht.led.resources");

    public static String getString(String key) {
        try {
            return BUNDLE.getString(key);
        } catch (MissingResourceException ex) {
            return "!" + key + "!";
        }
    }

    public static Image getImage(String resourceName) {
        try (InputStream is = Resources.class.getResourceAsStream(resourceName)) {
            return new Image(is);
        } catch (Exception ex) {
            return null;
        }
    }

    private static final int[] ICON_SIZES = new int[]{16, 24, 32, 48, 64, 128, 256, 512};
    public static final String ICON_COLOR_WHITE = "ffffff";

    public static List<Image> getIcons(String iconName, String iconColor) {
        return IntStream.of(ICON_SIZES).mapToObj(iconSize -> getIconResourceName(iconName, iconSize, iconColor))
                .map(Resources::getImage).collect(Collectors.toCollection(ArrayList::new));
    }

    public static String getIconResourceName(String iconName, int iconSize, String iconColor) {
        return String.format("/name/ulbricht/led/icons/%1$s/%2$s-%1$s-%3$d.png", iconName, iconColor, iconSize);
    }

    private Resources() {
        // hidden
    }
}
