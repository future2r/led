package name.ulbricht.led;

import name.ulbricht.led.api.Color;
import name.ulbricht.led.api.LEDDisplay;
import name.ulbricht.led.api.LEDDisplayChangeEvent;
import org.junit.jupiter.api.Test;

import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.*;

final class LEDDisplayTest {

    private final LEDDisplay led = new LEDDisplayImpl(new LogImpl());

    private int eventCount;

    @Test
    void testInit() {
        assertDefaults();
    }

    @Test
    void testWidth() {
        this.led.setWidth(42);
        assertEquals(42, this.led.getWidth());
        assertEquals(LEDDisplay.DEFAULT_HEIGHT, this.led.getHeight());
    }

    @Test
    void testWidthLessMin() {
        assertThrows(IllegalArgumentException.class, () -> this.led.setWidth(0));
    }

    @Test
    void testWidthGreaterMax() {
        assertThrows(IllegalArgumentException.class, () -> this.led.setWidth(LEDDisplay.MAX_WIDTH + 1));
    }

    @Test
    void testWidthEvent() {
        this.led.addLEDDisplayChangeListener(e -> {
            this.eventCount++;
            assertDisplaySizeEvent(e);
        });

        this.led.setWidth(42);
        assertEquals(1, this.eventCount);
    }

    @Test
    void testHeight() {
        this.led.setHeight(42);
        assertEquals(42, this.led.getHeight());
        assertEquals(LEDDisplay.DEFAULT_WIDTH, this.led.getWidth());
    }

    @Test
    void testHeightLessMin() {
        assertThrows(IllegalArgumentException.class, () -> this.led.setHeight(0));
    }

    @Test
    void testHeightGreaterMax() {
        assertThrows(IllegalArgumentException.class, () -> this.led.setHeight(LEDDisplay.MAX_HEIGHT + 1));
    }

    @Test
    void testHeightEvent() {
        this.led.addLEDDisplayChangeListener(e -> {
            this.eventCount++;
            assertDisplaySizeEvent(e);
        });

        this.led.setHeight(42);
        assertEquals(1, this.eventCount);
    }

    @Test
    void testColor() {
        for (var color : Color.values()) {
            this.led.setColor(color);
            assertEquals(color, this.led.getColor());
            this.led.on(10, 5);
            assertEquals(color, this.led.getColor(10, 5));
        }

        assertThrows(IllegalArgumentException.class, () -> this.led.setColor(null));
    }

    @Test
    void testDelay() {
        this.led.setDelay(42);
        assertEquals(42, this.led.getDelay());
    }

    @Test
    void testDelayLessMin() {
        assertThrows(IllegalArgumentException.class, () -> this.led.setDelay(-1));
    }

    @Test
    void testDelayGreaterMax() {
        assertThrows(IllegalArgumentException.class, () -> this.led.setDelay(LEDDisplay.MAX_DELAY + 1));
    }

    @Test
    void testStrict() {
        this.led.setStrict(false);
        assertFalse(this.led.isStrict());

        this.led.setStrict(true);
        assertTrue(this.led.isStrict());
    }

    @Test
    void testReset() {
        this.led.setWidth(42);
        this.led.setHeight(42);
        this.led.setColor(Color.BLUE);
        this.led.setDelay(42);
        this.led.setStrict(false);
        this.led.on();

        this.led.reset();
        assertDefaults();
    }

    @Test
    void testResetEvent() {
        this.led.addLEDDisplayChangeListener(e -> {
            this.eventCount++;
            assertDisplaySizeEvent(e);
        });

        this.led.setHeight(42);
        assertEquals(1, this.eventCount);
    }

    @Test
    void testDelayFunction() {
        var start = System.currentTimeMillis();
        this.led.delay(500);
        assertTrue((System.currentTimeMillis() - start) >= 500);
    }

    @Test
    void testOn() {
        this.led.on();
        forEach(this.led, (x, y) -> assertTrue(this.led.get(x, y)));
    }

    @Test
    void testOnEvent() {
        this.led.addLEDDisplayChangeListener(e -> {
            this.eventCount++;
            assertAllDotsEvent(e);
        });

        this.led.on();
        assertEquals(1, this.eventCount);
    }

    @Test
    void testOnAt() {
        this.led.setDelay(0);
        forEach(this.led, (x, y) -> {
            this.led.on(x, y);
            assertTrue(this.led.get(x, y));
        });
    }

    @Test
    public void testOnAtStrict() {
        this.led.setStrict(false);
        this.led.on(-42, -42);

        assertThrows(IllegalArgumentException.class, () -> {
            this.led.setStrict(true);
            this.led.on(-42, -42);
        });
    }

    @Test
    void testOnAtEvent() {
        this.led.addLEDDisplayChangeListener(e -> {
            this.eventCount++;
            assertSingleDotEvent(e, 10, 5);
        });

        this.led.on(10, 5);
        assertEquals(1, this.eventCount);
    }

    @Test
    void testOff() {
        this.led.on();
        this.led.off();
        forEach(this.led, (x, y) -> assertFalse(this.led.get(x, y)));
    }

    @Test
    void testOffEvent() {
        this.led.addLEDDisplayChangeListener(e -> {
            this.eventCount++;
            assertAllDotsEvent(e);
        });

        this.led.off();
        assertEquals(1, this.eventCount);
    }

    @Test
    void testOffAt() {
        this.led.setDelay(0);
        this.led.on();
        forEach(this.led, (x, y) -> {
            this.led.off(x, y);
            assertFalse(this.led.get(x, y));
        });
    }

    @Test
    void testOffAtStrict() {
        this.led.setStrict(false);
        this.led.off(-42, -42);

        assertThrows(IllegalArgumentException.class, () -> {
            this.led.setStrict(true);
            this.led.off(-42, -42);
        });
    }

    @Test
    void testOffAtEvent() {
        this.led.addLEDDisplayChangeListener(e -> {
            this.eventCount++;
            assertSingleDotEvent(e, 10, 5);
        });

        this.led.off(10, 5);
        assertEquals(1, this.eventCount);
    }

    @Test
    void testSetAtOn() {
        this.led.setDelay(0);
        forEach(this.led, (x, y) -> {
            this.led.set(x, y, true);
            assertTrue(this.led.get(x, y));
        });
    }

    @Test
    void testSetAtOff() {
        this.led.setDelay(0);
        this.led.on();
        forEach(this.led, (x, y) -> {
            this.led.set(x, y, false);
            assertFalse(this.led.get(x, y));
        });
    }

    @Test
    void testSetAtStrict() {
        this.led.setStrict(false);
        this.led.set(-42, -42, true);

        assertThrows(IllegalArgumentException.class, () -> {
            this.led.setStrict(true);
            this.led.set(-42, -42, true);
        });
    }

    @Test
    void testSetAtEvent() {
        this.led.addLEDDisplayChangeListener(e -> {
            this.eventCount++;
            assertSingleDotEvent(e, 10, 5);
        });

        this.led.set(10, 5, true);
        assertEquals(1, this.eventCount);
    }

    @Test
    void testInvert() {
        this.led.invert();
        forEach(this.led, (x, y) -> assertTrue(this.led.get(x, y)));
        this.led.invert();
        forEach(this.led, (x, y) -> assertFalse(this.led.get(x, y)));
    }

    @Test
    void testInvertEvent() {
        this.led.addLEDDisplayChangeListener(e -> {
            this.eventCount++;
            assertAllDotsEvent(e);
        });

        this.led.invert();
        assertEquals(1, this.eventCount);
    }

    @Test
    void testInvertAt() {
        this.led.setDelay(0);
        forEach(this.led, (x, y) -> {
            this.led.invert(x, y);
            assertTrue(this.led.get(x, y));
            this.led.invert(x, y);
            assertFalse(this.led.get(x, y));
        });
    }

    @Test
    void testInvertAtStrict() {
        this.led.setStrict(false);
        this.led.invert(-42, -42);

        assertThrows(IllegalArgumentException.class, () -> {
            this.led.setStrict(true);
            this.led.invert(-42, -42);
        });
    }

    @Test
    void testInvertAtEvent() {
        this.led.addLEDDisplayChangeListener(e -> {
            this.eventCount++;
            assertSingleDotEvent(e, 10, 5);
        });

        this.led.invert(10, 5);
        assertEquals(1, this.eventCount);
    }

    @Test
    @SuppressWarnings("deprecation")
    void testWriteOn() {
        this.led.setDelay(0);
        this.led.writeOn(10, 2, "Hello");
        assertTrue(this.led.get(10, 2));
    }

    @Test
    @SuppressWarnings("deprecation")
    void testWriteOnNull() {
        this.led.setDelay(0);
        assertThrows(IllegalArgumentException.class, () -> {
            this.led.writeOn(10, 2, null);
        });
    }

    @Test
    @SuppressWarnings("deprecation")
    void testWriteOnEmpty() {
        this.led.setDelay(0);
        this.led.writeOn(10, 2, "");
    }

    @Test
    @SuppressWarnings("deprecation")
    void testWriteOnStrict() {
        this.led.setDelay(0);

        this.led.setStrict(false);
        this.led.writeOn(10, 5, "Hello");
        assertTrue(this.led.get(10, 5));

        this.led.setStrict(true);
        assertThrows(IllegalArgumentException.class, () -> {
            this.led.writeOn(10, 5, "Hello");
        });
    }

    @Test
    @SuppressWarnings("deprecation")
    void testWriteOnEvent() {
        this.led.addLEDDisplayChangeListener(e -> this.eventCount++);

        this.led.writeOn(10, 2, "H");
        assertEquals(17, this.eventCount);
    }

    @Test
    @SuppressWarnings("deprecation")
    void testWriteOff() {
        this.led.setDelay(0);
        this.led.on();
        this.led.writeOff(10, 2, "Hello");
        assertFalse(this.led.get(10, 2));
    }

    @Test
    @SuppressWarnings("deprecation")
    void testWriteOffNull() {
        this.led.setDelay(0);
        assertThrows(IllegalArgumentException.class, () -> {
            this.led.writeOff(10, 2, null);
        });
    }

    @Test
    @SuppressWarnings("deprecation")
    void testWriteOffEmpty() {
        this.led.setDelay(0);
        this.led.writeOff(10, 2, "");
    }

    @Test
    @SuppressWarnings("deprecation")
    void testWriteOffStrict() {
        this.led.setDelay(0);
        this.led.on();

        this.led.setStrict(false);
        this.led.writeOff(10, 5, "Hello");
        assertFalse(this.led.get(10, 5));

        this.led.setStrict(true);
        assertThrows(IllegalArgumentException.class, () -> {
            this.led.writeOff(10, 5, "Hello");
        });
    }

    @Test
    @SuppressWarnings("deprecation")
    void testWriteOffEvent() {
        this.led.addLEDDisplayChangeListener(e -> this.eventCount++);

        this.led.writeOff(10, 2, "H");
        assertEquals(17, this.eventCount);
    }

    @Test
    void testWrite() {
        this.led.setDelay(0);
        this.led.write(10, 2, true, "Hello");
        assertTrue(this.led.get(10, 2));
        this.led.write(10, 2, false, "Hello");
        assertFalse(this.led.get(10, 2));
    }

    @Test
    void testWriteNull() {
        this.led.setDelay(0);
        assertThrows(IllegalArgumentException.class, () -> {
            this.led.write(10, 2, true, null);
        });
    }

    @Test
    void testWriteEmpty() {
        this.led.setDelay(0);
        this.led.write(10, 2, true, "");
    }

    @Test
    void testWriteStrict() {
        this.led.setDelay(0);

        this.led.setStrict(false);
        this.led.write(10, 5, true, "Hello");
        assertTrue(this.led.get(10, 5));

        this.led.setStrict(true);
        assertThrows(IllegalArgumentException.class, () -> {
            this.led.write(10, 5, true, "Hello");
        });
    }

    @Test
    void testWriteEvent() {
        this.led.addLEDDisplayChangeListener(e -> this.eventCount++);

        this.led.write(10, 2, true, "H");
        assertEquals(17, this.eventCount);
    }

    @Test
    void testMoveRightOn() {
        testMoveRight(true);
    }

    @Test
    void testMoveRightOff() {
        this.led.on();
        testMoveRight(false);
    }

    private void testMoveRight(boolean status) {
        this.led.move(2, 0, status);
        forEach(this.led, (x, y) -> assertEquals(x <= 1 == status, this.led.get(x, y)));
    }

    @Test
    void testMoveLeftOn() {
        testMoveLeft(true);
    }

    @Test
    void testMoveLeftOff() {
        this.led.on();
        testMoveLeft(false);
    }

    private void testMoveLeft(boolean status) {
        this.led.move(-2, 0, status);
        forEach(this.led, (x, y) -> assertEquals(x < (this.led.getWidth() - 2) != status, this.led.get(x, y)));
    }

    @Test
    void testDownRightOn() {
        testMoveDown(true);
    }

    @Test
    void testMoveDownOff() {
        this.led.on();
        testMoveDown(false);
    }

    private void testMoveDown(boolean status) {
        this.led.move(0, 2, status);
        forEach(this.led, (x, y) -> assertEquals(y <= 1 == status, this.led.get(x, y)));
    }

    @Test
    void testMoveUpOn() {
        testMoveUp(true);
    }

    @Test
    void testMoveUpOff() {
        this.led.on();
        testMoveUp(false);
    }

    private void testMoveUp(boolean status) {
        this.led.move(0, -2, status);
        forEach(this.led, (x, y) -> assertEquals(y < (this.led.getHeight() - 2) != status, this.led.get(x, y)));
    }

    @Test
    void testMoveExtrem() {
        this.led.move(1000, 1000, true);
        forEach(this.led, (x, y) -> assertTrue(this.led.get(x, y)));
    }

    private void assertDefaults() {
        assertEquals(LEDDisplay.DEFAULT_WIDTH, this.led.getWidth());
        assertEquals(LEDDisplay.DEFAULT_HEIGHT, this.led.getHeight());
        assertEquals(LEDDisplay.DEFAULT_COLOR, this.led.getColor());
        assertEquals(LEDDisplay.DEFAULT_DELAY, this.led.getDelay());
        assertEquals(LEDDisplay.DEFAULT_STRICT, this.led.isStrict());
        forEach(this.led, (x, y) -> assertFalse(this.led.get(x, y)));
    }

    private static void assertDisplaySizeEvent(LEDDisplayChangeEvent e) {
        assertNotNull(e.getSource());
        assertEquals(LEDDisplayChangeEvent.Type.DISPLAY_SIZE, e.getType());
        assertEquals(-1, e.getX());
        assertEquals(-1, e.getY());
    }

    private static void assertAllDotsEvent(LEDDisplayChangeEvent e) {
        assertNotNull(e.getSource());
        assertEquals(LEDDisplayChangeEvent.Type.ALL_DOTS, e.getType());
        assertEquals(-1, e.getX());
        assertEquals(-1, e.getY());
    }

    private static void assertSingleDotEvent(LEDDisplayChangeEvent e, int x, int y) {
        assertNotNull(e.getSource());
        assertEquals(LEDDisplayChangeEvent.Type.SINGLE_DOT, e.getType());
        assertEquals(x, e.getX());
        assertEquals(y, e.getY());
    }

    private static void forEach(LEDDisplay led, BiConsumer<Integer, Integer> consumer) {
        for (int x = 0; x < led.getWidth(); x++) {
            for (int y = 0; y < led.getHeight(); y++) {
                consumer.accept(x, y);
            }
        }
    }
}
