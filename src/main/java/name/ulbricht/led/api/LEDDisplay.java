package name.ulbricht.led.api;

public interface LEDDisplay {

    interface DotMatrix {

        int getWidth();

        int getHeight();

        boolean get(int x, int y);

        Color getColor(int x, int y);
    }

    int MAX_WIDTH = 200;
    int DEFAULT_WIDTH = 50;

    int MAX_HEIGHT = 100;
    int DEFAULT_HEIGHT = 10;

    Color DEFAULT_COLOR = Color.RED;

    int DEFAULT_DELAY = 20;
    int MAX_DELAY = 60_000;

    boolean DEFAULT_STRICT = true;

    /*
     * ========== Properties ==========
     */

    int getWidth();

    void setWidth(int width);

    int getHeight();

    void setHeight(int height);

    Color getColor();

    void setColor(Color color);

    int getDelay();

    void setDelay(int delay);

    boolean isStrict();

    void setStrict(boolean strict);

    /*
     * ========== Methods ==========
     */

    void reset();

    void delay(int millis);

    void on();

    void on(int x, int y);

    @Deprecated
    boolean isOn(int x, int y);

    boolean get(int x, int y);

    void set(int x, int y, boolean status);

    Color getColor(int x, int y);

    void off();

    void off(int x, int y);

    void invert();

    void invert(int x, int y);

    @Deprecated
    void writeOn(int x, int y, String text);

    @Deprecated
    void writeOff(int x, int y, String text);

    void write(int x, int y, boolean status, String text);

    void move(int x, int y, boolean status);

    DotMatrix getDots();

    /*
     * ========== Events ==========
     */

    void addLEDDisplayChangeListener(LEDDisplayChangeListener l);

    void removeLEDDisplayChangeListener(LEDDisplayChangeListener l);
}
