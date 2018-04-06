/*
  Lernprogramm
  Verschieben
*/

// Text schreiben und nach rechts rausschieben
led.write(1, 1, ON, "Hallo");
for (i = 0; i < 25; i++) led.move(2, 0, OFF);

//Text schreiben und nach oben rausschieben
led.write(1, 1, ON, "Hallo");
for (i = 0; i < 10; i++) led.move(0, -1, ON);