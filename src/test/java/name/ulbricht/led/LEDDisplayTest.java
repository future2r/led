package name.ulbricht.led;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.function.BiConsumer;

import name.ulbricht.led.api.Color;
import name.ulbricht.led.api.LEDDisplay;
import name.ulbricht.led.api.LEDDisplayChangeEvent;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

@SuppressWarnings("boxing")
public class LEDDisplayTest {

	private LEDDisplay led = new LEDDisplayImpl(new LogImpl());

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private int eventCount;

	@Test
	public void testInit() {
		assertDefaults();
	}

	@Test
	public void testWidth() {
		this.led.setWidth(42);
		assertEquals(42, this.led.getWidth());
		assertEquals(LEDDisplay.DEFAULT_HEIGHT, this.led.getHeight());
	}

	@Test
	public void testWidthLessMin() {
		this.thrown.expect(IllegalArgumentException.class);
		this.led.setWidth(0);
	}

	@Test
	public void testWidthGreaterMax() {
		this.thrown.expect(IllegalArgumentException.class);
		this.led.setWidth(LEDDisplay.MAX_WIDTH + 1);
	}

	@Test
	public void testWidthEvent() {
		this.led.addLEDDisplayChangeListener(e -> {
			this.eventCount++;
			assertDisplaySizeEvent(e);
		});

		this.led.setWidth(42);
		assertEquals(1, this.eventCount);
	}

	@Test
	public void testHeight() {
		this.led.setHeight(42);
		assertEquals(42, this.led.getHeight());
		assertEquals(LEDDisplay.DEFAULT_WIDTH, this.led.getWidth());
	}

	@Test
	public void testHeightLessMin() {
		this.thrown.expect(IllegalArgumentException.class);
		this.led.setHeight(0);
	}

	@Test
	public void testHeightGreaterMax() {
		this.thrown.expect(IllegalArgumentException.class);
		this.led.setHeight(LEDDisplay.MAX_HEIGHT + 1);
	}

	@Test
	public void testHeightEvent() {
		this.led.addLEDDisplayChangeListener(e -> {
			this.eventCount++;
			assertDisplaySizeEvent(e);
		});

		this.led.setHeight(42);
		assertEquals(1, this.eventCount);
	}

	@Test
	public void testColor() {
		for (Color color : Color.values()) {
			this.led.setColor(color);
			assertEquals(color, this.led.getColor());
			this.led.on(10, 5);
			assertEquals(color, this.led.getColor(10, 5));
		}

		this.thrown.expect(IllegalArgumentException.class);
		this.led.setColor(null);
	}

	@Test
	public void testDelay() {
		this.led.setDelay(42);
		assertEquals(42, this.led.getDelay());
	}

	@Test
	public void testDelayLessMin() {
		this.thrown.expect(IllegalArgumentException.class);
		this.led.setDelay(-1);
	}

	@Test
	public void testDelayGreaterMax() {
		this.thrown.expect(IllegalArgumentException.class);
		this.led.setDelay(LEDDisplay.MAX_DELAY + 1);
	}

	@Test
	public void testStrict() {
		this.led.setStrict(false);
		assertFalse(this.led.isStrict());

		this.led.setStrict(true);
		assertTrue(this.led.isStrict());
	}

	@Test
	public void testReset() {
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
	public void testResetEvent() {
		this.led.addLEDDisplayChangeListener(e -> {
			this.eventCount++;
			assertDisplaySizeEvent(e);
		});

		this.led.setHeight(42);
		assertEquals(1, this.eventCount);
	}

	@Test
	public void testDelayFunction() {
		long start = System.currentTimeMillis();
		this.led.delay(500);
		assertTrue((System.currentTimeMillis() - start) >= 500);
	}

	@Test
	public void testOn() {
		this.led.on();
		forEach(this.led, (x, y) -> assertTrue(this.led.get(x, y)));
	}

	@Test
	public void testOnEvent() {
		this.led.addLEDDisplayChangeListener(e -> {
			this.eventCount++;
			assertAllDotsEvent(e);
		});

		this.led.on();
		assertEquals(1, this.eventCount);
	}

	@Test
	public void testOnAt() {
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

		this.thrown.expect(IllegalArgumentException.class);
		this.led.setStrict(true);
		this.led.on(-42, -42);
	}

	@Test
	public void testOnAtEvent() {
		this.led.addLEDDisplayChangeListener(e -> {
			this.eventCount++;
			assertSingleDotEvent(e, 10, 5);
		});

		this.led.on(10, 5);
		assertEquals(1, this.eventCount);
	}

	@Test
	public void testOff() {
		this.led.on();
		this.led.off();
		forEach(this.led, (x, y) -> assertFalse(this.led.get(x, y)));
	}

	@Test
	public void testOffEvent() {
		this.led.addLEDDisplayChangeListener(e -> {
			this.eventCount++;
			assertAllDotsEvent(e);
		});

		this.led.off();
		assertEquals(1, this.eventCount);
	}

	@Test
	public void testOffAt() {
		this.led.setDelay(0);
		this.led.on();
		forEach(this.led, (x, y) -> {
			this.led.off(x, y);
			assertFalse(this.led.get(x, y));
		});
	}

	@Test
	public void testOffAtStrict() {
		this.led.setStrict(false);
		this.led.off(-42, -42);

		this.thrown.expect(IllegalArgumentException.class);
		this.led.setStrict(true);
		this.led.off(-42, -42);
	}

	@Test
	public void testOffAtEvent() {
		this.led.addLEDDisplayChangeListener(e -> {
			this.eventCount++;
			assertSingleDotEvent(e, 10, 5);
		});

		this.led.off(10, 5);
		assertEquals(1, this.eventCount);
	}

	@Test
	public void testSetAtOn() {
		this.led.setDelay(0);
		forEach(this.led, (x, y) -> {
			this.led.set(x, y, true);
			assertTrue(this.led.get(x, y));
		});
	}

	@Test
	public void testSetAtOff() {
		this.led.setDelay(0);
		this.led.on();
		forEach(this.led, (x, y) -> {
			this.led.set(x, y, false);
			assertFalse(this.led.get(x, y));
		});
	}

	@Test
	public void testSetAtStrict() {
		this.led.setStrict(false);
		this.led.set(-42, -42, true);

		this.thrown.expect(IllegalArgumentException.class);
		this.led.setStrict(true);
		this.led.set(-42, -42, true);
	}

	@Test
	public void testSetAtEvent() {
		this.led.addLEDDisplayChangeListener(e -> {
			this.eventCount++;
			assertSingleDotEvent(e, 10, 5);
		});

		this.led.set(10, 5, true);
		assertEquals(1, this.eventCount);
	}

	@Test
	public void testInvert() {
		this.led.invert();
		forEach(this.led, (x, y) -> assertTrue(this.led.get(x, y)));
		this.led.invert();
		forEach(this.led, (x, y) -> assertFalse(this.led.get(x, y)));
	}

	@Test
	public void testInvertEvent() {
		this.led.addLEDDisplayChangeListener(e -> {
			this.eventCount++;
			assertAllDotsEvent(e);
		});

		this.led.invert();
		assertEquals(1, this.eventCount);
	}

	@Test
	public void testInvertAt() {
		this.led.setDelay(0);
		forEach(this.led, (x, y) -> {
			this.led.invert(x, y);
			assertTrue(this.led.get(x, y));
			this.led.invert(x, y);
			assertFalse(this.led.get(x, y));
		});
	}

	@Test
	public void testInvertAtStrict() {
		this.led.setStrict(false);
		this.led.invert(-42, -42);

		this.thrown.expect(IllegalArgumentException.class);
		this.led.setStrict(true);
		this.led.invert(-42, -42);
	}

	@Test
	public void testInvertAtEvent() {
		this.led.addLEDDisplayChangeListener(e -> {
			this.eventCount++;
			assertSingleDotEvent(e, 10, 5);
		});

		this.led.invert(10, 5);
		assertEquals(1, this.eventCount);
	}

	@Test
	@SuppressWarnings("deprecation")
	public void testWriteOn() {
		this.led.setDelay(0);
		this.led.writeOn(10, 2, "Hello");
		assertTrue(this.led.get(10, 2));
	}

	@Test
	@SuppressWarnings("deprecation")
	public void testWriteOnNull() {
		this.led.setDelay(0);
		this.thrown.expect(IllegalArgumentException.class);
		this.led.writeOn(10, 2, null);
	}

	@Test
	@SuppressWarnings("deprecation")
	public void testWriteOnEmpty() {
		this.led.setDelay(0);
		this.led.writeOn(10, 2, "");
	}

	@Test
	@SuppressWarnings("deprecation")
	public void testWriteOnStrict() {
		this.led.setDelay(0);

		this.led.setStrict(false);
		this.led.writeOn(10, 5, "Hello");
		assertTrue(this.led.get(10, 5));

		this.led.setStrict(true);
		this.thrown.expect(IllegalArgumentException.class);
		this.led.writeOn(10, 5, "Hello");
	}

	@Test
	@SuppressWarnings("deprecation")
	public void testWriteOnEvent() {
		this.led.addLEDDisplayChangeListener(e -> {
			this.eventCount++;
		});

		this.led.writeOn(10, 2, "H");
		assertEquals(17, this.eventCount);
	}

	@Test
	@SuppressWarnings("deprecation")
	public void testWriteOff() {
		this.led.setDelay(0);
		this.led.on();
		this.led.writeOff(10, 2, "Hello");
		assertFalse(this.led.get(10, 2));
	}

	@Test
	@SuppressWarnings("deprecation")
	public void testWriteOffNull() {
		this.led.setDelay(0);
		this.thrown.expect(IllegalArgumentException.class);
		this.led.writeOff(10, 2, null);
	}

	@Test
	@SuppressWarnings("deprecation")
	public void testWriteOffEmpty() {
		this.led.setDelay(0);
		this.led.writeOff(10, 2, "");
	}

	@Test
	@SuppressWarnings("deprecation")
	public void testWriteOffStrict() {
		this.led.setDelay(0);
		this.led.on();

		this.led.setStrict(false);
		this.led.writeOff(10, 5, "Hello");
		assertFalse(this.led.get(10, 5));

		this.led.setStrict(true);
		this.thrown.expect(IllegalArgumentException.class);
		this.led.writeOff(10, 5, "Hello");
	}

	@Test
	@SuppressWarnings("deprecation")
	public void testWriteOffEvent() {
		this.led.addLEDDisplayChangeListener(e -> {
			this.eventCount++;
		});

		this.led.writeOff(10, 2, "H");
		assertEquals(17, this.eventCount);
	}

	@Test
	public void testWrite() {
		this.led.setDelay(0);
		this.led.write(10, 2, true, "Hello");
		assertTrue(this.led.get(10, 2));
		this.led.write(10, 2, false, "Hello");
		assertFalse(this.led.get(10, 2));
	}

	@Test
	public void testWriteNull() {
		this.led.setDelay(0);
		this.thrown.expect(IllegalArgumentException.class);
		this.led.write(10, 2, true, null);
	}

	@Test
	public void testWriteEmpty() {
		this.led.setDelay(0);
		this.led.write(10, 2, true, "");
	}

	@Test
	public void testWriteStrict() {
		this.led.setDelay(0);

		this.led.setStrict(false);
		this.led.write(10, 5, true, "Hello");
		assertTrue(this.led.get(10, 5));

		this.led.setStrict(true);
		this.thrown.expect(IllegalArgumentException.class);
		this.led.write(10, 5, true, "Hello");
	}

	@Test
	public void testWriteEvent() {
		this.led.addLEDDisplayChangeListener(e -> {
			this.eventCount++;
		});

		this.led.write(10, 2, true, "H");
		assertEquals(17, this.eventCount);
	}

	@Test
	public void testMoveRightOn() {
		testMoveRight(true);
	}

	@Test
	public void testMoveRightOff() {
		this.led.on();
		testMoveRight(false);
	}

	private void testMoveRight(boolean status) {
		this.led.move(2, 0, status);
		forEach(this.led, (x, y) -> assertEquals(x <= 1 ? status : !status, this.led.get(x, y)));
	}

	@Test
	public void testMoveLeftOn() {
		testMoveLeft(true);
	}

	@Test
	public void testMoveLeftOff() {
		this.led.on();
		testMoveLeft(false);
	}

	private void testMoveLeft(boolean status) {
		this.led.move(-2, 0, status);
		forEach(this.led, (x, y) -> assertEquals(x < (this.led.getWidth() - 2) ? !status : status, this.led.get(x, y)));
	}
	
	@Test
	public void testDownRightOn() {
		testMoveDown(true);
	}

	@Test
	public void testMoveDownOff() {
		this.led.on();
		testMoveDown(false);
	}

	private void testMoveDown(boolean status) {
		this.led.move(0, 2, status);
		forEach(this.led, (x, y) -> assertEquals(y <= 1 ? status : !status, this.led.get(x, y)));
	}

	@Test
	public void testMoveUpOn() {
		testMoveUp(true);
	}

	@Test
	public void testMoveUpOff() {
		this.led.on();
		testMoveUp(false);
	}

	private void testMoveUp(boolean status) {
		this.led.move(0, -2, status);
		forEach(this.led, (x, y) -> assertEquals(y < (this.led.getHeight() - 2) ? !status : status, this.led.get(x, y)));
	}
	
	@Test
	public void testMoveExtrem() {
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
