module name.ulbricht.led {

	// additional JDK modules
	requires java.prefs;
	requires java.xml;
	requires java.scripting;

	// JavaFX
	requires javafx.base;
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.web;

	// open the UI controllers to JavaFX
	opens name.ulbricht.led to javafx.fxml, javafx.graphics;

	// open the API implementations for scripting
	opens name.ulbricht.led.impl;
}