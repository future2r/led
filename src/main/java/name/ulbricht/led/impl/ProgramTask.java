package name.ulbricht.led.impl;

import javafx.concurrent.Task;
import name.ulbricht.led.api.Color;
import name.ulbricht.led.api.Environment;
import name.ulbricht.led.api.LEDDisplay;
import name.ulbricht.led.api.Log;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public final class ProgramTask extends Task<Void> implements Environment {

    private final String source;

    private final ScriptEngine engine;

    public ProgramTask(String source, LEDDisplay led, Log log) {
        this.source = source;

        var engineManager = new ScriptEngineManager();
        this.engine = engineManager.getEngineByMimeType("text/javascript");

        // inject environments
        var bindings = this.engine.createBindings();

        bindings.put("led", led);

        bindings.put("ON", Boolean.TRUE);
        bindings.put("OFF", Boolean.FALSE);

        bindings.put("RED", Color.RED);
        bindings.put("GREEN", Color.GREEN);
        bindings.put("BLUE", Color.BLUE);

        bindings.put("log", log);

        bindings.put("env", this);

        this.engine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
    }

    @Override
    public void checkCancelled() throws InterruptedException {
        if (this.isCancelled()) {
            throw new InterruptedException("Interrupted by user");
        }
    }

    @Override
    public void exit() throws InterruptedException {
        this.cancel();
        throw new InterruptedException("Interrupted by script");
    }

    @Override
    protected Void call() throws Exception {
        try {
            this.engine.eval(this.source);
        } catch (RuntimeException ex) {
            // there was an error during script execution
            var cause = ex.getCause();
            if (!(cause instanceof InterruptedException)) {
                // the script was not interrupted
                throw ex;
            }
        }
        return null;
    }
}
