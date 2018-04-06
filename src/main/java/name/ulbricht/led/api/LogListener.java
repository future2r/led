package name.ulbricht.led.api;

import java.util.EventListener;

@FunctionalInterface
public interface LogListener extends EventListener {

    void logAdded(LogEvent event);
}
