/*
  Lernprogramm
  Farben

  Eine Farbe wird gesetzt und gilt solange, bis eine andere Farbe gesetzt wird.

  Folgende Farben sind verfügbar:
  RED   = rot
  GREEN = grün
  BLUE  = blau
*/


// Grüne Lampe
led.color = GREEN;
led.on(5, 5);

// Blaue Lampe
led.color = BLUE;
led.on(7, 5);

// Rote Lampe
led.color = RED;
led.on(9, 5);