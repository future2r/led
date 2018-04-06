package name.ulbricht.led.api;

public interface Environment {

    void checkCancelled() throws InterruptedException;

    void exit() throws InterruptedException;
}
