package name.ulbricht.led;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.nio.file.Path;

final class FileChoosers {

    private static FileChooser openFileChooser;
    private static FileChooser saveFileChooser;

    static Path showOpenFileChooser(Window window) {
        var fileChooser = getOpenFileChooser();
        fileChooser.setInitialDirectory(Settings.getMRUOpenDir().toFile());
        var selectedFile = fileChooser.showOpenDialog(window);
        return selectedFile != null ? selectedFile.toPath() : null;
    }

    static Path showSaveFileChooser(Window window, Path file) {
        var fileChooser = getSaveFileChooser();
        fileChooser.setInitialDirectory(file.getParent().toFile());
        fileChooser.setInitialFileName(file.getFileName().toString());
        var selectedFile = fileChooser.showSaveDialog(window);
        return selectedFile != null ? selectedFile.toPath() : null;
    }

    private static synchronized FileChooser getOpenFileChooser() {
        if (openFileChooser == null) {
            openFileChooser = createFileChooser();
        }
        return openFileChooser;
    }

    private static synchronized FileChooser getSaveFileChooser() {
        if (saveFileChooser == null) {
            saveFileChooser = createFileChooser();
        }
        return saveFileChooser;
    }

    private static FileChooser createFileChooser() {
        var fileChooser = new FileChooser();
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter(Resources.getString("extensionFilter.js.description"),
                        Resources.getString("extensionFilter.js.pattern")));
        return fileChooser;
    }

    private FileChoosers() {
        // hidden
    }
}
