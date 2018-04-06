package name.ulbricht.led.api;

import java.util.EventListener;

@FunctionalInterface
public interface LEDDisplayChangeListener extends EventListener {

    void displayChanged(LEDDisplayChangeEvent event);
}
