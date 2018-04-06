/*
  Lernprogramm
  Geschwindigkeit ändern
*/

// Eine Linie mit Standardgeschwindigkeit
for (i = 0; i < led.width; i++) led.on(i, 0);

// Geschwindigkeit verdoppeln (also Schaltzeit der Lampen halbieren)
led.delay = led.delay / 2;
for (i = 0; i < led.width; i++) led.on(i, 2);

// Jetzt mal gaaaaanz langsam
led.delay = 100;
for (i = 0; i < led.width; i++) led.on(i, 4);

// Jetzt so schnell wie möglich
led.delay = 0;
for (i = 0; i < led.width; i++) led.on(i, 6);