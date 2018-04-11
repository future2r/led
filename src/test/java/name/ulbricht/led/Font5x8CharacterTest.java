package name.ulbricht.led;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

final class Font5x8CharacterTest {

    @ParameterizedTest
    @MethodSource("characterProvider")
    void testCharacter(Character c) {

        byte[] data = MatrixFont.FONT_5x8.getCharacter(c);

        assertNotNull(data, String.format("Unsupported character %s (%sh, %s)", c, Integer.toHexString(c),
                Integer.toString(c)));
        assertEquals(MatrixFont.FONT_5x8.width * MatrixFont.FONT_5x8.height, data.length);
    }

    private static Stream<Character> characterProvider() {
        var s = "!\"#$%&'()*+,-./" // special characters
                + "0123456789" // digits
                + ":;<=>?" // special characters
                + "ABCDEFGHIJKLMNOPQRSTUVWXYZ" // upper case letters
                + "[\\]_" // special characters
                + "abcdefghijklmnopqrstuvwxyz" // lower case letters
                + "{|}~€£§°" // special characters
                + "ÄÖÜäöü"; // German umlauts

        return s.chars().mapToObj(i -> Character.valueOf((char)i));
    }
}
