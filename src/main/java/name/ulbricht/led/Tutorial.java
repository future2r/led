package name.ulbricht.led;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static name.ulbricht.led.TutorialCategory.*;

public enum Tutorial {

    SWITCHING(BASICS), LOOPS(BASICS), SIZE(BASICS), SPEED(BASICS), COLORS(BASICS), TEXT(BASICS), MOVE(BASICS),

    FUNCTIONS(ADVANCED), STRICT(ADVANCED), LOG(ADVANCED), CANCEL(ADVANCED),

    PING_PONG(DEMO), GAME_OF_LIFE(DEMO);

    private final TutorialCategory category;

    Tutorial(TutorialCategory category) {
        this.category = category;
    }

    public TutorialCategory getCategory() {
        return this.category;
    }

    public String getDisplayName() {
        return Resources.getString(String.format("Tutorial.%s.displayName", name()));
    }

    public String getSource() {
        var fileName = Resources.getString(String.format("Tutorial.%s.fileName", name()));
        try (var is = getClass().getResourceAsStream(String.format("tutorials/%s", fileName));
             var isr = new InputStreamReader(is, StandardCharsets.UTF_8);
             var br = new BufferedReader(isr)) {

            var buf = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                buf.append(line);
                buf.append('\n');
            }
            return buf.toString();
        } catch (IOException ex) {
            throw new InternalError(ex);
        }
    }
}
