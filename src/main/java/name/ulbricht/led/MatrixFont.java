package name.ulbricht.led;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class MatrixFont {

    public static final MatrixFont FONT_5x8 = new MatrixFont(5, 8,
            ResourceBundle.getBundle("name.ulbricht.led.font_5x8"));

    public static final byte ON = 1;
    public static final byte OFF = 0;

    private static final int NUM_CHARACTERS = 256;

    public final int width;
    public final int height;
    private final byte[][] characters = new byte[NUM_CHARACTERS][];

    private MatrixFont(int width, int height, ResourceBundle bundle) {
        this.width = width;
        this.height = height;
        for (int ascii = 0; ascii < NUM_CHARACTERS; ascii++) {
            try {
                char[] pattern = bundle.getString(Integer.toString(ascii)).toCharArray();
                this.characters[ascii] = new byte[width * height];
                int idx = 0;
                for (char c : pattern) {
                    this.characters[ascii][idx++] = c == '#' ? ON : OFF;
                }
            } catch (MissingResourceException ex) {
                // unsupported character
            }
        }
    }

    public byte[] getCharacter(char c) {
        if (c == '€') return this.characters[128];
        if (c < NUM_CHARACTERS) return this.characters[c];
        return null;
    }
}
