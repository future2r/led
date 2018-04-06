/*
  Lernprogramm
  Strict-Modus aktivieren/deaktivieren

  Der Strict-Modus (strict = streng) zeigt Fehler, wenn auf Lampen außerhalb der Anzeige zugegriffen wird.
  Wenn der Strict-Modus ausgeschalten ist, passiert einfach nichts.
  Standardmäßig ist der Strict-Modus aktiviert.

  true (wahr)    : eingeschalten
  false (falsch) : ausgeschalten
*/

// Strict-Modus ausschalten
led.strict = false;

// Eine horizontale Linie, die 5 Lampen über den rechten Rand hinausgeht
for (i = 0; i < (led.width + 5); i++) led.on(i, 2);