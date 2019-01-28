package name.ulbricht.led;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import jdk.nashorn.api.scripting.JSObject;
import name.ulbricht.led.api.Log;

public final class MainController implements Initializable {

	public class Welcome {

		public void createNewProgram() {
			MainController.this.newFile();
		}

		public void openProgram() {
			MainController.this.openFile();
		}

		public void openTutorial() {
			MainController.this.openTutorial();
		}

		public void showAPIDocumentation() {
			MainController.this.showAPIDocumentation();
		}
	}

	@FXML
	private Parent root;
	@FXML
	private TabPane sourceTabPane;
	@FXML
	private Menu mruMenu;
	@FXML
	private MenuItem mruNoneMenuItem;
	@FXML
	private Menu helpMenu;
	@FXML
	private WebView welcomeWebView;
	@FXML
	private TextArea logTextArea;

	private DisplayController displayController;
	private APIController apiController;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		var insertPos = 1;
		for (var category : TutorialCategory.values()) {
			var menu = new Menu(category.getDisplayName());

			for (var tutorial : Tutorial.values()) {
				if (tutorial.getCategory() == category) {
					var item = new MenuItem(tutorial.getDisplayName() + "...");
					item.getProperties().put(Tutorial.class, tutorial);
					item.setOnAction(e -> openTutorial(
							(Tutorial) ((MenuItem) e.getSource()).getProperties().get(Tutorial.class)));
					menu.getItems().add(item);
				}
			}
			this.helpMenu.getItems().add(insertPos++, menu);
		}

		this.welcomeWebView.getEngine().getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> {
			if (newState == State.SUCCEEDED) {
				JSObject win = (JSObject) MainController.this.welcomeWebView.getEngine().executeScript("window");
				win.setMember("welcome", new Welcome());
			}
		});

		Platform.runLater(() -> this.welcomeWebView.getEngine()
				.load(getClass().getResource(Resources.getString("main.tab.welcome.content")).toString()));
	}

	@FXML
	protected void newFile() {
		addFileTab(createFileTab());
	}

	@FXML
	protected void openFile() {
		var file = FileChoosers.showOpenFileChooser(getStage());
		if (file != null) {
			Settings.setMRUOpenDir(file.getParent());
			performOpen(file);
		}
	}

	@FXML
	protected void configureMRUMenu() {
		this.mruMenu.getItems().clear();

		var mruFiles = Settings.getMRUFiles();
		if (!mruFiles.isEmpty()) {
			for (Path file : mruFiles) {
				var item = new MenuItem(file.getFileName().toString());
				item.getProperties().put(Path.class, file);
				item.setOnAction(e -> performOpen((Path) ((MenuItem) e.getSource()).getProperties().get(Path.class)));
				this.mruMenu.getItems().add(item);
			}
		} else {
			this.mruMenu.getItems().add(this.mruNoneMenuItem);
		}
	}

	@FXML
	protected void closeFile() {
		getCurrentSourceTabController().ifPresent(controller -> {
			if (controller.canClose())
				this.sourceTabPane.getTabs().remove(controller.getTab());
		});
	}

	@FXML
	public void saveFile() {
		getCurrentSourceTabController().ifPresent(controller -> {
			if (controller.save()) {
				Settings.addMRUFile(controller.getSourceFile().getFilePath());
			}
		});
	}

	@FXML
	public void saveFileAs() {
		getCurrentSourceTabController().ifPresent(controller -> {
			if (controller.saveAs()) {
				Settings.addMRUFile(controller.getSourceFile().getFilePath());
			}
		});
	}

	@FXML
	protected void exit() {
		if (canClose()) {
			getStage().close();
			cleanUp();
		}
	}

	void cleanUp() {
		if (this.displayController != null) {
			this.displayController.getStage().close();
		}
		if (this.apiController != null) {
			this.apiController.getStage().close();
		}
	}

	@FXML
	protected void cut() {
		getCurrentSourceTabController().ifPresent(SourceTabController::cut);
	}

	@FXML
	protected void copy() {
		getCurrentSourceTabController().ifPresent(SourceTabController::copy);
	}

	@FXML
	protected void paste() {
		getCurrentSourceTabController().ifPresent(SourceTabController::paste);
	}

	@FXML
	protected void delete() {
		getCurrentSourceTabController().ifPresent(SourceTabController::delete);
	}

	@FXML
	protected void showDisplay() {
		var display = getDisplayController();
		display.getStage().show();
		display.getStage().toFront();
	}

	@FXML
	protected void execute() {
		getCurrentSourceTabController().ifPresent(this::performExecute);
	}

	@FXML
	protected void stop() {
		getCurrentSourceTabController().ifPresent(this::performStop);
	}

	protected void openTutorial() {
		Platform.runLater(() -> this.helpMenu.show());
	}

	@FXML
	protected void showAPIDocumentation() {
		var api = getAPIController();
		api.getStage().show();
		api.getStage().toFront();
	}

	@FXML
	protected void about() {
		var appVersion = getClass().getPackage().getImplementationVersion();
		var javaVersion = System.getProperty("java.version");
		var text = String.format(Resources.getString("alert.info.about.textPattern"), appVersion, javaVersion);
		Alerts.info(getStage(), Resources.getString("alert.info.about.header"), text);
	}

	@FXML
	protected void clearLog() {
		Platform.runLater(() -> this.logTextArea.clear());
	}

	boolean canClose() {
		var controllers = this.sourceTabPane.getTabs().stream().map(this::getSourceTabController)
				.filter(Optional::isPresent).map(Optional::get).collect(Collectors.toCollection(ArrayList::new));
		for (var controller : controllers) {
			if (!controller.canClose())
				return false;
		}
		return true;
	}

	private Tab createFileTab() {
		var loader = new FXMLLoader(getClass().getResource("source-tab.fxml"), Resources.BUNDLE);
		Tab tab;
		try {
			tab = loader.load();
		} catch (IOException ex) {
			throw new InternalError(ex);
		}

		setSourceTabController(tab, loader.getController());

		return tab;
	}

	private void addFileTab(Tab tab) {
		this.sourceTabPane.getTabs().add(tab);
		this.sourceTabPane.getSelectionModel().select(tab);
	}

	private Stage getStage() {
		return (Stage) this.root.getScene().getWindow();
	}

	private Optional<SourceTabController> getCurrentSourceTabController() {
		var tab = this.sourceTabPane.getSelectionModel().getSelectedItem();
		if (tab != null)
			return getSourceTabController(tab);
		return Optional.empty();
	}

	private Optional<SourceTabController> getSourceTabController(Tab tab) {
		return Optional.ofNullable((SourceTabController) tab.getProperties().get(SourceTabController.ID));
	}

	private static void setSourceTabController(Tab tab, SourceTabController controller) {
		tab.getProperties().put(SourceTabController.ID, controller);
	}

	private void performOpen(Path file) {
		var tab = createFileTab();
		var controller = (SourceTabController) tab.getProperties().get(SourceTabController.ID);

		try {
			controller.getSourceFile().load(file);
			Settings.addMRUFile(file);
		} catch (IOException ex) {
			Alerts.exception(getStage(), ex);
		}

		addFileTab(tab);
	}

	private void performExecute(SourceTabController controller) {
		showDisplay();
		var file = controller.getSourceFile();
		try {
			getDisplayController().execute(file.getFileName(), file.getSource());
		} catch (IllegalStateException ex) {
			Alerts.error(getStage(), Resources.getString("alert.canNotExecute.header"),
					Resources.getString("alert.canNotExecute.text"));
		}
	}

	private void performStop(SourceTabController controller) {
		getDisplayController().stop();
	}

	void addLogEntry(Log.Level level, String text) {
		Platform.runLater(() -> this.logTextArea.appendText(String.format("%s:\t %s\n", level.getDisplayName(), text)));
	}

	private synchronized DisplayController getDisplayController() {
		if (this.displayController == null) {
			var loader = new FXMLLoader(DisplayController.class.getResource("display.fxml"), Resources.BUNDLE);
			Parent display;
			try {
				display = loader.load();
			} catch (IOException ex) {
				throw new InternalError(ex);
			}

			var scene = new Scene(display);

			var stage = new Stage();
			stage.setTitle(Resources.getString("display.title"));
			stage.getIcons().addAll(getStage().getIcons());
			stage.setScene(scene);

			DisplayController controller = loader.getController();
			controller.setMainController(this);

			this.displayController = controller;
		}
		return this.displayController;
	}

	private synchronized APIController getAPIController() {
		if (this.apiController == null) {
			var loader = new FXMLLoader(DisplayController.class.getResource("api.fxml"), Resources.BUNDLE);
			Parent display;
			try {
				display = loader.load();
			} catch (IOException ex) {
				throw new InternalError(ex);
			}

			var scene = new Scene(display);

			var stage = new Stage();
			stage.setTitle(Resources.getString("api.title"));
			stage.getIcons().addAll(getStage().getIcons());
			stage.setScene(scene);

			this.apiController = loader.getController();
		}
		return this.apiController;
	}

	private void openTutorial(Tutorial tutorial) {
		var tab = createFileTab();
		getSourceTabController(tab).ifPresent(controller -> {
			controller.getSourceFile().setSource(tutorial.getSource());
			addFileTab(tab);
		});
	}
}
