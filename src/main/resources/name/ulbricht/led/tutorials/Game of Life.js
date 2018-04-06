/*
  Lernprogram
  Game of Life (Conways Spiel des Lebens)

  Regeln:
  1. Eine tote Zelle mit genau drei lebenden Nachbarn wird neu geboren.
  2. Eine lebende Zelle mit weniger als zwei lebenden Nachbarn stibt an Einsamkeit.
  3. Eine lebende Zelle mit zwei oder drei lebenden Nachbarn bleibt am Leben.
  4. Eine lebende Zelle mit mehr als drei lebenden Nachbarn stirbt an Überbevölkerung.
*/

/*****************************************************
 * Zuerst werden alle benötigten Funktionen definert *
 *****************************************************/

// Eine Funktion zum Zählen der Nachbarn einer Zelle.
// Jede Zelle hat maximal 8 Nachbarn (Randzellen weniger).
function countNeighbors(x, y) {
    neighbors = 0; // Am Anfang sind es 0 Nachbarn

    // links oben
    if (x > 0 && y > 0 && led.get(x - 1, y - 1)) neighbors++;
    // oben
    if (y > 0 && led.get(x, y - 1)) neighbors++;
    // rechts oben
    if (x < (led.width - 1) && y > 0 && led.get(x + 1, y - 1)) neighbors++;
    // rechts
    if (x < (led.width - 1) && led.get(x + 1, y)) neighbors++;
    // rechts unten
    if (x < (led.width - 1) && y < (led.height - 1) && led.get(x + 1, y + 1)) neighbors++;
    // unten
    if (y < (led.height - 1) && led.get(x, y + 1)) neighbors++;
    // links unten
    if (x > 0 && y < (led.height - 1) && led.get(x - 1, y + 1)) neighbors++;
    // links
    if (x > 0 && led.get(x - 1, y)) neighbors++;

    return neighbors; // Ergebnis aus der Funktion zurückgeben
}

// Eine Funktion zum Auswerten der Regeln für eine Zelle
function checkRules(x, y) {
    // Anzahl der Nachbarn zählen
    neighbors = countNeighbors(x, y);

    if (led.get(x, y)) { // lebende Zelle
        if (neighbors < 2) return null;                    // 2. Regel
        if (neighbors == 2 || neighbors == 3) return BLUE; // 3. Regel
        if (neighbors > 3) return null;                    // 4. Regel
    } else { // tote Zelle
        if (neighbors == 3) return GREEN;                  // 1. Regel
    }

    return null; // sonst ist die Zelle tot
}

// Eine Funktion zum Berechnen einer kompletten nächsten Generation
function nextGeneration() {
    // Hier merken wir uns die Ergebnissse, da wir die Lampen nicht verändern dürfen,
    // solange wir noch auswerten.
    results = [];

    // für jede Zelle auswerten...
    for (x = 0; x < led.width; x++) {
        for (y = 0; y < led.height; y++) {
            // Regeln für diese Zelle auswerten und Ergebnis merken
            results[y * led.width + x] = checkRules(x, y);
        }
    }

    // für jede Zelle anzeigen...
    for (x = 0; x < led.width; x++) {
        for (y = 0; y < led.height; y++) {
            // Lese das gemerkte Ergebnis und schalte die Lampe entsprechend
            color = results[y * led.width + x];
            if (color != null) {
            	led.color = color;
            	led.on(x, y);
            } else {
            	led.off(x, y);
            }
        }
    }
}

/*****************************
 * Hier startet das Programm *
 *****************************/

led.delay = 2;
led.color = BLUE;

// Erstelle die Ausgangssituation

// "Blinker"
led.on(4, 5);
led.on(5, 5);
led.on(6, 5);

// "Gleiter"
led.on(15, 4);
led.on(16, 5);
led.on(14, 6);
led.on(15, 6);
led.on(16, 6);

// "Raumschiff"
led.on(26, 4);
led.on(27, 4);
led.on(28, 4);
led.on(29, 4);
led.on(25, 5);
led.on(29, 5);
led.on(29, 6);
led.on(25, 7);
led.on(28, 7);

// Berechne die Generationen
while (true) {
	nextGeneration();      // nächste Generation berechnen
	env.checkCancelled();  // auf Abbruch durch Benutzer prüfen
}
