package name.ulbricht.led;

import name.ulbricht.led.api.Log;
import name.ulbricht.led.api.LogEvent;
import name.ulbricht.led.api.LogListener;

import javax.swing.event.EventListenerList;

public final class LogImpl implements Log {

    private final EventListenerList eventListeners = new EventListenerList();

    @Override
    public void info(String text) {
        fireLogAdded(Level.INFO, text);
    }

    @Override
    public void warning(String text) {
        fireLogAdded(Level.WARNING, text);
    }

    @Override
    public void error(String text) {
        fireLogAdded(Level.ERROR, text);
    }

    @Override
    public void addLogListener(LogListener l) {
        this.eventListeners.add(LogListener.class, l);
    }

    @Override
    public void removeLogListener(LogListener l) {
        this.eventListeners.remove(LogListener.class, l);
    }

    private void fireLogAdded(Level level, String text) {
        LogListener[] listeners = this.eventListeners.getListeners(LogListener.class);
        if (listeners.length > 0) {
            var event = new LogEvent(this, level, text);
            for (var listener : listeners) {
                listener.logAdded(event);
            }
        }
    }
}
