package name.ulbricht.led;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
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
        String fileName = Resources.getString(String.format("Tutorial.%s.fileName", name()));
        try (InputStream is = getClass().getResourceAsStream(String.format("tutorials/%s", fileName));
             InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(isr)) {

            StringBuilder buf = new StringBuilder();
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
