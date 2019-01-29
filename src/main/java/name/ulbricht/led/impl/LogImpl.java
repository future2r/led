package name.ulbricht.led.impl;

import java.util.ArrayList;
import java.util.List;

import name.ulbricht.led.api.Log;
import name.ulbricht.led.api.LogEvent;
import name.ulbricht.led.api.LogListener;

public final class LogImpl implements Log {

	private final List<LogListener> eventListeners = new ArrayList<>();

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
		this.eventListeners.add(l);
	}

	@Override
	public void removeLogListener(LogListener l) {
		this.eventListeners.remove(l);
	}

	private void fireLogAdded(Level level, String text) {
		if (!this.eventListeners.isEmpty()) {
		var listeners = this.eventListeners.toArray(LogListener[]::new);
			var event = new LogEvent(this, level, text);
			for (var listener : listeners) {
				listener.logAdded(event);
			}
		}
	}
}
