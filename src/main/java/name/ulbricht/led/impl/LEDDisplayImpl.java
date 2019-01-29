package name.ulbricht.led.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import name.ulbricht.led.Resources;
import name.ulbricht.led.api.Color;
import name.ulbricht.led.api.LEDDisplay;
import name.ulbricht.led.api.LEDDisplayChangeEvent;
import name.ulbricht.led.api.LEDDisplayChangeListener;
import name.ulbricht.led.api.Log;

public final class LEDDisplayImpl implements LEDDisplay {

	private static final class DotMatrixImpl implements DotMatrix {

		private final int width;
		private final int height;
		private final Color[] dots;

		private DotMatrixImpl(int width, int height, Color[] dots) {
			this.width = width;
			this.height = height;
			this.dots = dots;
		}

		@Override
		public int getWidth() {
			return this.width;
		}

		@Override
		public int getHeight() {
			return this.height;
		}

		@Override
		public boolean get(int x, int y) {
			return getColor(x, y) != Color.OFF;
		}

		@Override
		public Color getColor(int x, int y) {
			if (x < 0 || x >= this.width || y < 0 || y >= this.height)
				throw new IllegalArgumentException();
			return this.dots[y * this.width + x];
		}
	}

	private int width = DEFAULT_WIDTH;
	private int height = DEFAULT_HEIGHT;
	private Color color = DEFAULT_COLOR;
	private int delay = DEFAULT_DELAY;
	private boolean strict = DEFAULT_STRICT;

	private final Log log;

	private Color[] dots;

	private final List<LEDDisplayChangeListener> eventListeners = new ArrayList<>();

	public LEDDisplayImpl(Log log) {
		this.log = log;
		initDots();
	}

	/*
	 * ========== Properties ==========
	 */

	@Override
	public int getWidth() {
		return this.width;
	}

	@Override
	public void setWidth(int width) {
		this.width = checkWidth(width);
		initDots();
	}

	@Override
	public int getHeight() {
		return this.height;
	}

	@Override
	public void setHeight(int height) {
		this.height = checkHeight(height);
		initDots();
	}

	@Override
	public Color getColor() {
		return this.color;
	}

	@Override
	public void setColor(Color color) {
		if (color == null)
			throw new IllegalArgumentException(Resources.getString("error.colorRequired"));
		this.color = color;
	}

	@Override
	public int getDelay() {
		return this.delay;
	}

	@Override
	public void setDelay(int delay) {
		this.delay = checkDelay(delay);
	}

	@Override
	public boolean isStrict() {
		return this.strict;
	}

	@Override
	public void setStrict(boolean strict) {
		this.strict = strict;
	}

	/*
	 * ========== Methods ==========
	 */

	@Override
	public void reset() {
		this.width = DEFAULT_WIDTH;
		this.height = DEFAULT_HEIGHT;
		this.color = DEFAULT_COLOR;
		this.delay = DEFAULT_DELAY;
		this.strict = DEFAULT_STRICT;
		initDots();
	}

	@Override
	public void delay(int millis) {
		doDelay(checkDelay(millis));
	}

	@Override
	public void on() {
		setAllDots(true);
	}

	@Override
	public void on(int x, int y) {
		set(x, y, true);
	}

	@Override
	public boolean isOn(int x, int y) {
		this.log.warning(
				String.format(Resources.getString("warning.functionDeprecated.pattern"), "isOn(x, y)", "get(x, y)"));
		return get(x, y);
	}

	@Override
	public boolean get(int x, int y) {
		return getColor(x, y) != Color.OFF;
	}

	@Override
	public void set(int x, int y, boolean status) {
		if (checkXY(x, y)) {
			delay();
			this.dots[y * this.width + x] = status ? this.color : Color.OFF;
			fireSingleDotChanged(x, y);
		}
	}

	@Override
	public Color getColor(int x, int y) {
		if (checkXY(x, y)) {
			return this.dots[y * this.width + x];
		}
		return Color.OFF;
	}

	@Override
	public void off() {
		setAllDots(false);
	}

	@Override
	public void off(int x, int y) {
		set(x, y, false);
	}

	@Override
	public void invert() {
		invertAllDots();
	}

	@Override
	public void invert(int x, int y) {
		set(x, y, !get(x, y));
	}

	@Override
	public DotMatrix getDots() {
		return new DotMatrixImpl(this.width, this.height, this.dots.clone());
	}

	@Override
	public void writeOn(int x, int y, String text) {
		this.log.warning(String.format(Resources.getString("warning.functionDeprecated.pattern"), "writeOn(x, y, text)",
				"write(x, y, status, text)"));
		write(x, y, true, text);
	}

	@Override
	public void writeOff(int x, int y, String text) {
		this.log.warning(String.format(Resources.getString("warning.functionDeprecated.pattern"),
				"writeOff(x, y, text)", "write(x, y, status, text)"));
		write(x, y, false, text);
	}

	@Override
	public void write(int x, int y, boolean status, String text) {
		if (text == null)
			throw new IllegalArgumentException(Resources.getString("error.textRequired"));

		var font = MatrixFont.FONT_5x8;
		int pos = x;
		for (var c : text.toCharArray()) {
			byte[] charDots = font.getCharacter(c);
			if (charDots != null) {
				for (var i = 0; i < font.width; i++) {
					for (var j = 0; j < font.height; j++) {
						if (charDots[j * font.width + i] == MatrixFont.ON) {
							set(pos + i, y + j, status);
						}
					}
				}
			}
			pos = pos + font.width + 1;
		}
	}

	@Override
	public void move(int x, int y, boolean status) {
		delay();
		var srcDots = getDots();
		for (var col = 0; col < this.width; col++) {
			for (var row = 0; row < this.height; row++) {
				var srcX = col - x;
				var srcY = row - y;
				if (srcX < 0 || srcX >= srcDots.getWidth() || srcY < 0 || srcY >= srcDots.getHeight())
					this.dots[row * this.width + col] = status ? this.color : Color.OFF;
				else
					this.dots[row * this.width + col] = srcDots.getColor(srcX, srcY);
			}
		}
		fireAllDotsChanged();
	}

	/*
	 * ========== Events ==========
	 */

	@Override
	public void addLEDDisplayChangeListener(LEDDisplayChangeListener l) {
		this.eventListeners.add(l);

	}

	@Override
	public void removeLEDDisplayChangeListener(LEDDisplayChangeListener l) {
		this.eventListeners.remove(l);
	}

	/*
	 * ========== Internals ==========
	 */

	private void initDots() {
		this.dots = new Color[this.width * this.height];
		Arrays.fill(this.dots, Color.OFF);
		fireDisplaySizeChanged();
	}

	private void setAllDots(boolean on) {
		delay();
		Arrays.fill(this.dots, on ? this.color : Color.OFF);
		fireAllDotsChanged();
	}

	private void invertAllDots() {
		delay();
		for (var i = 0; i < (this.width * this.height); i++) {
			this.dots[i] = this.dots[i] == Color.OFF ? this.color : Color.OFF;
		}
		fireAllDotsChanged();
	}

	@SuppressWarnings("boxing")
	private boolean checkXY(int x, int y) {
		if (x < 0 || x >= this.width) {
			if (this.strict)
				throw new IllegalArgumentException(
						String.format(Resources.getString("error.xOutOfRange.pattern"), x, this.width - 1));
			return false;
		}
		if (y < 0 || y >= this.height) {
			if (this.strict)
				throw new IllegalArgumentException(
						String.format(Resources.getString("error.yOutOfRange.pattern"), y, this.height - 1));
			return false;
		}
		return true;
	}

	@SuppressWarnings("boxing")
	private static int checkWidth(int width) {
		if (width < 1 || width > MAX_WIDTH)
			throw new IllegalArgumentException(
					String.format(Resources.getString("error.widthOutOfRange,pattern"), width, MAX_WIDTH));
		return width;
	}

	@SuppressWarnings("boxing")
	private static int checkHeight(int height) {
		if (height < 1 || height > MAX_HEIGHT)
			throw new IllegalArgumentException(
					String.format(Resources.getString("error.heightOutOfRange,pattern"), height, MAX_HEIGHT));
		return height;
	}

	@SuppressWarnings("boxing")
	private static int checkDelay(int delay) {
		if (delay < 0 || delay > MAX_DELAY)
			throw new IllegalArgumentException(
					String.format(Resources.getString("error.delayOutOfRange.pattern"), delay, MAX_DELAY));
		return delay;
	}

	private void delay() {
		doDelay(this.delay);
	}

	private static void doDelay(int millis) {
		try {
			if (millis > 0)
				Thread.sleep(millis);
		} catch (InterruptedException e) {
			// ignore this
		}
	}

	private void fireDisplaySizeChanged() {
		fireDisplayChanged(LEDDisplayChangeEvent.Type.DISPLAY_SIZE, -1, -1);
	}

	private void fireSingleDotChanged(int x, int y) {
		fireDisplayChanged(LEDDisplayChangeEvent.Type.SINGLE_DOT, x, y);
	}

	private void fireAllDotsChanged() {
		fireDisplayChanged(LEDDisplayChangeEvent.Type.ALL_DOTS, -1, -1);
	}

	private void fireDisplayChanged(LEDDisplayChangeEvent.Type type, int x, int y) {
		if (!this.eventListeners.isEmpty()) {
			var listeners = this.eventListeners.stream().toArray(LEDDisplayChangeListener[]::new);
			var event = new LEDDisplayChangeEvent(this.getDots(), type, x, y);
			for (var listener : listeners) {
				listener.displayChanged(event);
			}
		}
	}
}
