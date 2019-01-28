module name.ulbricht.led {
	
	requires java.prefs;
	
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.web;
	
	requires jdk.scripting.nashorn;

	opens name.ulbricht.led to javafx.graphics;
}