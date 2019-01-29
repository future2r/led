package name.ulbricht.led;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.w3c.dom.Element;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.web.WebView;

final class HyperlinkHandler implements ChangeListener<Worker.State>, EventListener {

	private static final String EVENT_TYPE_CLICK = "click";
	private static final String ELEMENT_NAME_LINK = "a";
	private static final String ATTRIBUTE_NAME_HREF = "href";

	private final WebView webView;
	private final Map<String, Runnable> handlers = new HashMap<>();

	public HyperlinkHandler(final WebView webView) {
		this.webView = webView;
		this.webView.getEngine().getLoadWorker().stateProperty().addListener(this);
	}

	void registerHandler(final String targetName, final Runnable handler) {
		this.handlers.put(targetName, handler);
	}

	@Override
	public void changed(final ObservableValue<? extends Worker.State> observable, final Worker.State oldValue,
			final Worker.State newValue) {
		if (newValue == Worker.State.SUCCEEDED) {
			var nodeList = webView.getEngine().getDocument().getElementsByTagName(ELEMENT_NAME_LINK);
			var nodeCount = nodeList.getLength();
			for (var i = 0; i < nodeCount; i++) {
				((EventTarget) nodeList.item(i)).addEventListener(EVENT_TYPE_CLICK, this, false);
			}
		}
	}

	@Override
	public void handleEvent(final Event evt) {
		if (evt.getType().equals(EVENT_TYPE_CLICK)) {
			Optional.ofNullable(((Element) evt.getTarget()).getAttribute(ATTRIBUTE_NAME_HREF)).ifPresent(
					href -> Optional.ofNullable(HyperlinkHandler.this.handlers.get(href)).ifPresent(Runnable::run));
		}
	}
}
