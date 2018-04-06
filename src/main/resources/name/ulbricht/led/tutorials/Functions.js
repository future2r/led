/*
  Lernprogramm
  Definition und Verwendung von Funktionen
*/

// Eine Funktion für einen Smiley definieren (das wird erstmal nicht ausgeführt)
// Diese Funktion hat den Namen 'smiley' und hat zwei Parameter, 'x' und 'y'
function smiley(x, y, status) {
	// Augen
	led.set(x + 1, y + 1, status);
	led.set(x + 3, y + 1, status);

	// Mund
	led.set(x, y + 3, status);
	led.set(x + 1, y + 4, status);
	led.set(x + 2, y + 4, status);
	led.set(x + 3, y + 4, status);
	led.set(x + 4, y + 3, status);
}

// Diese Funktion jetzt an verschiedenen Positionen verwenden
smiley(5, 2, ON);
smiley(15, 3, ON);

led.delay(500);
led.on();
smiley(25, 4, OFF);
smiley(35, 2, OFF);