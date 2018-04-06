/*
  Lernprogramm
  Größe der Anzeige ändern
*/

// Die neue Breite soll 20 sein
led.width = 20;

// Die neue Höhe soll das doppelte der aktuellen Höhe sein
led.height = 2 * led.height;

// Einen Rahmen um alles anschalten

// Zuerst horizontal oben und unten...
for (i = 0; i < led.width; i++) {
    led.on(i, 0);
    led.on(i, led.height - 1);
}

// ...dann vertikal links und rechts
for (i = 0; i < led.height; i++) {
    led.on(0, i);
    led.on(led.width - 1, i);
}