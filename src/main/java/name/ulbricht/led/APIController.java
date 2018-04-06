package name.ulbricht.led;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public final class APIController implements Initializable {

    @FXML
    private WebView webView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showHomePage();
    }

    public Stage getStage() {
        return (Stage) this.webView.getScene().getWindow();
    }

    @FXML
    protected void back() {
        try {
            this.webView.getEngine().getHistory().go(-1);
        } catch (IndexOutOfBoundsException ex) {
            // ignore this
        }
    }

    @FXML
    protected void forward() {
        try {
            this.webView.getEngine().getHistory().go(1);
        } catch (IndexOutOfBoundsException ex) {
            // ignore this
        }
    }

    @FXML
    protected void showHomePage() {
        loadResource(Resources.getString("api.content"));
    }

    private void loadResource(String name) {
        this.webView.getEngine().load(getClass().getResource(name).toString());
    }
}
