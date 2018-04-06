package name.ulbricht.led.api;

import java.util.EventObject;

public final class LogEvent extends EventObject {

    private static final long serialVersionUID = 1L;

    private final Log.Level level;
    private final String text;

    public LogEvent(Log source, Log.Level level, String text) {
        super(source);
        this.level = level;
        this.text = text;
    }

    @Override
    public Log getSource() {
        return (Log) super.getSource();
    }

    public Log.Level getLevel() {
        return this.level;
    }

    public String getText() {
        return this.text;
    }
}
