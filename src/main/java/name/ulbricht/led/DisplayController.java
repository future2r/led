package name.ulbricht.led;

import javafx.application.Platform;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import name.ulbricht.led.api.LEDDisplay;
import name.ulbricht.led.api.LEDDisplayChangeEvent;
import name.ulbricht.led.api.Log;
import name.ulbricht.led.impl.LEDDisplayImpl;
import name.ulbricht.led.impl.LogImpl;
import name.ulbricht.led.impl.ProgramTask;

import java.net.URL;
import java.util.ResourceBundle;

public final class DisplayController implements Initializable {

    @FXML
    private Parent root;
    @FXML
    private Canvas canvas;
    @FXML
    private Label coordsLabel;
    @FXML
    private Label programLabel;
    @FXML
    private Label statusLabel;

    private MainController mainController;

    private ProgramTask currentTask;

    private LEDDisplay led;
    private Log log;

    private static final int LED_WIDTH = 20;
    private static final int LED_HEIGHT = 20;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.log = new LogImpl();

        this.led = new LEDDisplayImpl(this.log);
        this.led.addLEDDisplayChangeListener(this::displayChanged);

        this.canvas.setOnMouseExited(e -> updateCoordsLabel(0, 0));
        this.canvas
                .setOnMouseMoved(e -> updateCoordsLabel((int) (e.getX() / LED_WIDTH), (int) (e.getY() / LED_HEIGHT)));

        Platform.runLater(() -> {
            getStage().centerOnScreen();
            updateCoordsLabel(0, 0);
        });

        updateCanvasSize(this.led.getDots());
        updateAllDots(this.led.getDots());

        Platform.runLater(() -> getStage().centerOnScreen());
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        this.log.addLogListener(e -> this.mainController.addLogEntry(e.getLevel(), e.getText()));
    }

    public void execute(String programName, String source) throws IllegalStateException {
        if (this.currentTask != null && this.currentTask.isRunning())
            throw new IllegalStateException("A program is running already");

        this.led.reset();

        this.programLabel.setText(programName);

        this.currentTask = new ProgramTask(source, this.led, this.log);
        this.currentTask.setOnRunning(this::taskRunning);
        this.currentTask.setOnSucceeded(this::taskSucceeded);
        this.currentTask.setOnCancelled(this::taskCancelled);
        this.currentTask.setOnFailed(this::taskFailed);

        var thread = new Thread(this.currentTask);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    public void stop() {
        if (this.currentTask != null && this.currentTask.isRunning()) {
            this.currentTask.cancel();
        }
    }

    public Stage getStage() {
        return (Stage) this.root.getScene().getWindow();
    }

    private void displayChanged(LEDDisplayChangeEvent event) {
        var dots = event.getSource();
        switch (event.getType()) {
            case DISPLAY_SIZE:
                updateCanvasSize(dots);
                updateAllDots(dots);
                break;
            case SINGLE_DOT:
                updateSingleDot(dots, event.getX(), event.getY());
                break;
            case ALL_DOTS:
                updateAllDots(dots);
                break;
            default: // do nothing
        }
    }

    private void updateCanvasSize(LEDDisplay.DotMatrix dots) {
        Platform.runLater(() -> {
            var stage = getStage();

            // remember the current position
            var x = stage.getX();
            var y = stage.getY();

            // hide/show to trigger layout
            stage.hide();

            this.canvas.setWidth(dots.getWidth() * LED_WIDTH);
            this.canvas.setHeight(dots.getHeight() * LED_HEIGHT);

            stage.show();

            // restore the position
            stage.setX(x);
            stage.setY(y);
        });
    }

    private void updateSingleDot(LEDDisplay.DotMatrix dots, int x, int y) {
        Platform.runLater(() -> {
            GraphicsContext gc = this.canvas.getGraphicsContext2D();
            gc.drawImage(dots.getColor(x, y).getImage(), x * LED_WIDTH, y * LED_HEIGHT);
        });
    }

    private void updateAllDots(LEDDisplay.DotMatrix dots) {
        Platform.runLater(() -> {
            GraphicsContext gc = this.canvas.getGraphicsContext2D();
            for (var x = 0; x < dots.getWidth(); x++) {
                for (var y = 0; y < dots.getHeight(); y++) {
                    gc.drawImage(dots.getColor(x, y).getImage(), x * LED_WIDTH, y * LED_HEIGHT);
                }
            }
        });
    }

    private void taskRunning(@SuppressWarnings("unused") WorkerStateEvent e) {
        setStatusLabel(Resources.getString("display.status.running"));
    }

    private void taskSucceeded(@SuppressWarnings("unused") WorkerStateEvent e) {
        setStatusLabel(Resources.getString("display.status.succeeded"));
        this.currentTask = null;
    }

    private void taskCancelled(@SuppressWarnings("unused") WorkerStateEvent e) {
        setStatusLabel(Resources.getString("display.status.cancelled"));
        this.currentTask = null;
    }

    private void taskFailed(WorkerStateEvent e) {
        setStatusLabel(Resources.getString("display.status.failed"));

        var ex = e.getSource().getException();
        this.log.error(ex.toString());
        Alerts.exception(getStage(), ex);

        this.currentTask = null;
    }

    private void setStatusLabel(String text) {
        Platform.runLater(() -> this.statusLabel.setText(text));
    }

    private void updateCoordsLabel(int x, int y) {
        this.coordsLabel.setText(
                String.format(Resources.getString("display.coordsPattern"), x, y));
    }
}
