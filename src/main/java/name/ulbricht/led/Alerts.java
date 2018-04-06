package name.ulbricht.led;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.stage.Window;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public final class Alerts {

    private static String GLOBAL_CSS = Alerts.class.getResource("global.css").toExternalForm();

    public static void info(Window owner, String header, String message) {
        final Alert alert = new Alert(AlertType.INFORMATION);
        alert.getDialogPane().getStylesheets().add(GLOBAL_CSS);
        alert.initOwner(owner);
        alert.setTitle(Resources.getString("alerts.info.title"));
        alert.setHeaderText(header);
        alert.setContentText(message);

        alert.showAndWait();
    }

    public static void error(Window owner, String header, String message) {
        final Alert alert = new Alert(AlertType.INFORMATION);
        alert.getDialogPane().getStylesheets().add(GLOBAL_CSS);
        alert.initOwner(owner);
        alert.setTitle(Resources.getString("alerts.error.title"));
        alert.setHeaderText(header);
        alert.setContentText(message);

        alert.showAndWait();
    }

    public static ButtonType confirm(Window owner, String header, String message) {
        final Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.getDialogPane().getStylesheets().add(GLOBAL_CSS);
        alert.initOwner(owner);
        alert.setTitle(Resources.getString("alerts.confirm.title"));
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);

        return alert.showAndWait().orElse(ButtonType.CANCEL);
    }

    public static void exception(final Window owner, final Throwable exception) {
        String exceptionText;
        try (final StringWriter sw = new StringWriter(); final PrintWriter pw = new PrintWriter(sw)) {
            exception.printStackTrace(pw);
            exceptionText = sw.toString();
        } catch (final IOException ex) {
            exceptionText = ex.getMessage();
        }

        largeAlert(owner, AlertType.ERROR, Resources.getString("alerts.exception.title"),
                Resources.getString("alerts.exception.header"), exception.getMessage(), exceptionText);
    }

    private static void largeAlert(Window owner, AlertType alertType, String title, String headerText, String message,
                                   String details) {
        final Alert alert = new Alert(alertType);
        alert.getDialogPane().getStylesheets().add(GLOBAL_CSS);
        alert.initOwner(owner);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(message);

        final TextArea textArea = new TextArea(details);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);

        alert.getDialogPane().setExpandableContent(textArea);

        alert.showAndWait();
    }

    private Alerts() {
        // hidden
    }
}
