# Splendor rule-based assistant

Pametni asistent za igru Splendor zasnovan na Drools pravilima. Projekat prati `SplendorPP` proposal:

- generise legalne poteze za uzimanje zetona, rezervaciju karte, kupovinu vidljive karte i kupovinu rezervisane karte;
- koristi forward chaining u tri nivoa: analiza stanja, izvodjenje strateskih ciljeva, bodovanje poteza;
- koristi `accumulate` za sabiranje doprinosa u ukupni skor poteza;
- cuva objasnjenja preko `ScoreContribution` cinjenica i nudi backward-style query za trag preporuke;
- sadrzi Drools template primer za generisanje slicnih scoring pravila.

Pokretanje:

```powershell
mvn test
mvn exec:java
```

Glavni fajlovi:

- `src/main/resources/rules/splendor-rules.drl`
- `src/main/resources/templates/score-buy-card.drt`
- `src/main/java/com/splendor/assistant/service/RecommendationService.java`
