/*
  Lernprogramm
  Verwendung von Schleifen
*/

// Eine horizontale Linie mit 10 Lampen (beginnend an Position x=5, y=2) einschalten
for (i = 0; i < 10; i++) led.on(5 + i, 2);

// Eine horizontale Linie mit 10 Lampen (beginnend an Position x=5, y=5)
// aber nur jede zweite Lampe einschalten
for (i = 0; i < 10; i+=2) led.on(5 + i, 5);

// Eine vertikale Linie mit 5 Lampen (beginnend an Position x=20, y=2) einschalten
for (i = 0; i < 5; i++) led.on(20, 2 + i);

// Eine diagonale Linie mit 8 Lampen (beginnend an Position x=25, y=1) einschalten
for (i = 0; i < 8; i++) led.on(25 + i, 1 + i);

// Eine doppelte horizontale Linie mit 20 Lampen (beginnend an Position x=35, y=5) einschalten
for (i = 0; i < 10; i++) {
    led.on(35 + i, 5);
    led.on(35 + i, 6);
}