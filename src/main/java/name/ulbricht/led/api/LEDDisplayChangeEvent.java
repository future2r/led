package name.ulbricht.led.api;

import name.ulbricht.led.api.LEDDisplay.DotMatrix;

import java.util.EventObject;

public final class LEDDisplayChangeEvent extends EventObject {

    private static final long serialVersionUID = 1L;

    public enum Type {
        DISPLAY_SIZE, SINGLE_DOT, ALL_DOTS
    }

    private final Type type;
    private final int x;
    private final int y;

    public LEDDisplayChangeEvent(DotMatrix source, Type type) {
        this(source, type, -1, -1);
    }

    public LEDDisplayChangeEvent(DotMatrix source, Type type, int x, int y) {
        super(source);
        this.type = type;
        this.x = x;
        this.y = y;
    }

    @Override
    public DotMatrix getSource() {
        return (DotMatrix) super.getSource();
    }

    public Type getType() {
        return this.type;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }
}
