package name.ulbricht.led;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public final class SourceTabController implements Initializable {

    public static final String ID = SourceTabController.class.getName();

    @FXML
    private Tab tab;
    @FXML
    private SourceFile sourceFile;
    @FXML
    private TextArea textArea;
    @FXML
    private Label coordsLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.tab.textProperty().bind(this.sourceFile.displayNameProperty());
        this.tab.setOnCloseRequest(this::tabCloseRequest);

        this.textArea.textProperty().bindBidirectional(this.sourceFile.sourceProperty());
        this.textArea.caretPositionProperty().addListener((observable, oldValue, newValue) -> updateCoordsLabel());

        Platform.runLater(() -> {
            this.textArea.requestFocus();
            updateCoordsLabel();
        });
    }

    public Tab getTab() {
        return this.tab;
    }

    public SourceFile getSourceFile() {
        return this.sourceFile;
    }

    public boolean canClose() {
        if (this.sourceFile.isDirty()) {
            var result = Alerts.confirm(getWindow(), Resources.getString("alert.confirm.saveChanges.header"),
                    String.format(Resources.getString("alert.confirm.saveChanges.textPattern"),
                            this.sourceFile.getFileName()));
            if (result == ButtonType.YES) return save();
            return result == ButtonType.NO;
        }
        return true;
    }

    public boolean save() {
        if (this.sourceFile.getFilePath() == null) return saveAs();

        try {
            this.sourceFile.store();
            return true;
        } catch (IOException ex) {
            Alerts.exception(getWindow(), ex);
        }
        return false;
    }

    public boolean saveAs() {
        var filePath = this.sourceFile.getFilePath();
        if (filePath == null) {
            filePath = Settings.getMRUSaveDir().resolve(this.sourceFile.getFileName());
        }

        filePath = FileChoosers.showSaveFileChooser(getWindow(), filePath);
        if (filePath != null) {
            Settings.setMRUSaveDir(filePath.getParent());
            var fileName = filePath.getFileName().toString();
            var idx = fileName.indexOf('.');
            if (idx < 0) {
                filePath = filePath.getParent().resolve(fileName + ".js");
            }
            try {
                this.sourceFile.store(filePath);
                return true;
            } catch (IOException ex) {
                Alerts.exception(getWindow(), ex);
            }
        }
        return false;
    }

    public void cut() {
        this.textArea.cut();
    }

    public void copy() {
        this.textArea.copy();
    }

    public void paste() {
        this.textArea.paste();
    }

    public void delete() {
        var selection = this.textArea.getSelection();
        if (selection.getLength() > 0) this.textArea.deleteText(selection);
    }

    private void tabCloseRequest(Event e) {
        if (!canClose()) e.consume();
    }

    private Window getWindow() {
        return this.tab.getTabPane().getScene().getWindow();
    }

    private void updateCoordsLabel() {
        var caretPos = this.textArea.getCaretPosition();
        var content = this.textArea.getText();

        String[] lines = content.split("\n", Integer.MAX_VALUE);

        var scanPos = 0;
        int line;
        var column = 0;
        for (line = 0; line < lines.length; line++) {
            var currentLine = lines[line];
            var currentLineLength = currentLine.length() + 1; // one for the line break
            if ((scanPos + currentLineLength) > caretPos) {
                column = caretPos - scanPos;

                break;
            }
            scanPos += currentLineLength;
        }

        this.coordsLabel.setText(String.format(Resources.getString("source.coordsPattern"), line + 1,
                column + 1));
    }
}
