/*
  Lernprogramm
  Text schreiben

  Für Buchstaben, die nicht unterstützt werden, wird ein Leerzeichen dargestellt.
*/

// Schreibe "Hallo" auf Position x=1, y=1
led.write(1, 1, ON, "Hallo");

// Schreibe "Welt" invertiert auf Position x=1, y=1
led.on();
led.write(1, 1, OFF, "Welt");