package name.ulbricht.led.api;

import name.ulbricht.led.Resources;

public interface Log {

    enum Level {
        INFO, WARNING, ERROR;

        public String getDisplayName() {
            return Resources.getString("Log.Level." + name() + ".displayName");
        }
    }

    void info(String text);

    void warning(String text);

    void error(String text);

    void addLogListener(LogListener l);

    void removeLogListener(LogListener l);
}
