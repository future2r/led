package name.ulbricht.led;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LEDApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        var loader = new FXMLLoader(getClass().getResource("main.fxml"), Resources.BUNDLE);
        Parent root = loader.load();
        MainController controller = loader.getController();

        var scene = new Scene(root, 900, 650);

        primaryStage.setTitle(Resources.getString("main.title"));
        primaryStage.getIcons().addAll(Resources.getIcons("led-diode", Resources.ICON_COLOR_WHITE));
        primaryStage.setScene(scene);

        primaryStage.setOnCloseRequest(e -> {
            if (!controller.canClose()) e.consume();
            else
                controller.cleanUp();
        });

        primaryStage.show();
    }
}
