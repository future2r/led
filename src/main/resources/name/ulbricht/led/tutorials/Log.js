/*
  Lernprogramm
  Protokollierung
*/

// Aktuelle Größe des LED-Displays protokollieren
log.info("Breite: " + led.width + ", Höhe: " + led.height);

// Line zeichnen und Zähler dabei protokollieren
for (i = 0; i < 10; i++) {
	led.on(i, 5);
	log.info(i);
}