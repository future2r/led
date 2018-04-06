package name.ulbricht.led;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public final class SourceFile {

    private final ReadOnlyObjectWrapper<Path> filePath = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyStringWrapper fileName = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper displayName = new ReadOnlyStringWrapper();
    private final StringProperty source = new SimpleStringProperty("");
    private final ReadOnlyBooleanWrapper dirty = new ReadOnlyBooleanWrapper(false);

    public SourceFile() {
        this.fileName.bind(Bindings.createStringBinding(this::createFileName, this.filePath));
        this.displayName.bind(Bindings.createStringBinding(this::createDisplayName, this.fileName, this.dirty));
        this.source.addListener((observable, oldValue, newValue) -> this.dirty.set(true));
    }

    private String createFileName() {
        return this.filePath.isNotNull().get() ? this.filePath.get().getFileName().toString()
                : Resources.getString("sourceFile.initialFileName");
    }

    private String createDisplayName() {
        return this.dirty.get() ? "*" + this.fileName.get() : this.fileName.get();
    }

    public final ReadOnlyObjectProperty<Path> filePathProperty() {
        return this.filePath.getReadOnlyProperty();
    }

    public final Path getFilePath() {
        return filePathProperty().get();
    }

    public final ReadOnlyStringProperty fileNameProperty() {
        return this.fileName.getReadOnlyProperty();
    }

    public final String getFileName() {
        return fileNameProperty().get();
    }

    public final ReadOnlyStringProperty displayNameProperty() {
        return this.displayName.getReadOnlyProperty();
    }

    public final String getDisplayName() {
        return displayNameProperty().get();
    }

    public final StringProperty sourceProperty() {
        return this.source;
    }

    public final String getSource() {
        return sourceProperty().get();
    }

    public final void setSource(String source) {
        sourceProperty().set(source);
    }

    public final ReadOnlyBooleanProperty dirtyProperty() {
        return this.dirty.getReadOnlyProperty();
    }

    public final boolean isDirty() {
        return dirtyProperty().get();
    }

    public void load(Path file) throws IOException {
        sourceProperty().set(performLoad(file));
        this.filePath.set(file);
        this.dirty.set(false);
    }

    private static String performLoad(Path file) throws IOException {
        Objects.requireNonNull(file);
        return Files.readAllLines(file, StandardCharsets.UTF_8).stream().collect(Collectors.joining("\n"));
    }

    public void store() throws IOException {
        performStore(this.filePath.get(), this.source.get());
        this.dirty.set(false);
    }

    public void store(Path file) throws IOException {
        performStore(file, this.source.get());
        this.filePath.set(file);
        this.dirty.set(false);
    }

    private static void performStore(Path file, String content) throws IOException {
        Objects.requireNonNull(file);
        Objects.requireNonNull(content);
        Files.write(file, Arrays.asList(content.split("\n")), StandardCharsets.UTF_8);
    }
}
