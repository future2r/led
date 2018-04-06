/*
  Lernprogram
  Ping Pong
 */

// Anfangspunkt
x = 10;
y = 1;

// Anfangsrichtung
xDir = 1;
yDir = 1;

// Farbenliste
colors = [ RED, GREEN, BLUE ];

for (i = 0; i < 500; i++) {

	// Farbe setzen und Lampe einschalten
	led.color = colors[(x + y) % 3];
	led.on(x, y);

	// x-Richtung am Rand umkehren
	if (x >= (led.width - 1)) xDir = -1;
	if (x <= 0) xDir = 1;

	// y-Richtung am Rand umkehren
	if (y >= (led.height - 1)) yDir = -1;
	if (y <= 0) yDir = 1;

	// Nächste Lampe laut Richtung
	x += xDir;
	y += yDir;
	
	// Überprüfen auf Abbruch
	env.checkCancelled();
}