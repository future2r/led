/*
  Lernprogramm
  Abbrechen
*/

// Lange Operation, die auf Abbruch durch den Benutzer prüft
for (y = 0; y < led.height; y++) {
	for (x = 0; x < led.width; x++) {
		led.on(x, y);
		
		// Hat der Benutzer abgebrochen?
		env.checkCancelled();
	}
}