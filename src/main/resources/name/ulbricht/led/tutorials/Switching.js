/*
  Lernprogramm
  Grundlegende Befehle
*/

// Eine Lampe an Position x=5, y=2 einschalten
led.on(5, 2);

// Eine Sekunde (1000 Millisekunden) warten
led.delay(1000);

// Die Lampe wieder ausschalten
led.off(5, 2);
led.delay(1000);

// Eine Lampe invertieren (wenn an dann aus, wenn aus dann an)
led.invert(10, 5);
led.delay(1000);

// Alle Lampen invertieren
led.invert();
led.delay(1000);

// Alle Lamepn ausschalten
led.off();
led.delay(1000);

// Alle Lampen einschalten
led.on();