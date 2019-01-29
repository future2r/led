package name.ulbricht.led.impl;

import org.junit.jupiter.api.Test;

import name.ulbricht.led.impl.MatrixFont;

import static org.junit.jupiter.api.Assertions.*;

final class Font5x8Test {

    @Test
    void testFont_5x8() {
        assertNotNull(MatrixFont.FONT_5x8);

        assertEquals(5, MatrixFont.FONT_5x8.width);
        assertEquals(8, MatrixFont.FONT_5x8.height);

        byte[] a = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0,
                1, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0};
        assertArrayEquals(a, MatrixFont.FONT_5x8.getCharacter('a'));
    }
}
