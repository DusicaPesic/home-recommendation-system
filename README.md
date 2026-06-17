# Splendor Rule-Based Assistant

Pametni asistent za igru Splendor zasnovan na Drools pravilima. Sistem analizira trenutno stanje igre za 2 igraca, generise legalne poteze, boduje ih i prikazuje objasnjenje za izabrani potez.

## Sta sistem radi

- generise legalne poteze za uzimanje zetona, rezervaciju karte, kupovinu vidljive karte i kupovinu rezervisane karte;
- koristi setup za 2 igraca: 4 zetona svake osnovne boje, 5 zlatnih zetona i 3 noble plocice;
- na tabli koristi 12 vidljivih karata, po 4 karte za svaki nivo;
- koristi forward chaining u tri nivoa: analiza stanja, izvodjenje strateskih ciljeva i bodovanje poteza;
- koristi `MoveScoreFact` cinjenice za pojedinacne doprinose skoru;
- koristi Drools `accumulate` za sabiranje svih `MoveScoreFact` cinjenica u ukupan skor poteza;
- koristi `DecisionFact` meta cinjenice za cuvanje veza izmedju zakljucaka i njihovih uzroka;
- koristi rekurzivni Drools backward chaining query za proveru uticaja pojedinacnih cinjenica na izabrani potez.

## Pravila

Drools pravila su podeljena po nivoima zakljucivanja:

```text
src/main/resources/rules/
  01-analysis-rules.drl
  02-strategy-rules.drl
  03-scoring-rules.drl
  04-explanation-queries.drl
```

`01-analysis-rules.drl` izvodi analiticke cinjenice kao sto su faza igre, efikasnost karte, dominantna boja, kupive karte, skoro kupive karte, pretnje protivnika, potrebne boje i rizik limita zetona.

`02-strategy-rules.drl` izvodi strateske ciljeve:

```text
BUY_CARD(card)
RESERVE_CARD(card)
BUILD_DOMINANT_COLOR(color)
BLOCK_OPPONENT(card)
COLLECT_TOKENS(color)
PLAY_RESERVED_CARD(card)
MANAGE_TOKEN_LIMIT
```

`03-scoring-rules.drl` dodeljuje poene legalnim potezima preko `MoveScoreFact` cinjenica. Na kraju `accumulate` pravilo sabira poene za svaki potez i upisuje ukupan skor u `Move`.

`04-explanation-queries.drl` sadrzi backward chaining query `explainsDecision(conclusion, prerequisite)`, koji rekurzivno proverava da li zakljucak zavisi od neke prethodne cinjenice.

## Objasnjenje odluke

Tokom forward chaininga pravila, pored domenskih cinjenica, kreiraju se i `DecisionFact` meta cinjenice. One povezuju zakljucak sa cinjenicom ili zakljuckom koji ga je izazvao.

Primer toka:

```text
base fact
-> analysis fact
-> strategic goal
-> move score
```

Java deo sistema koristi te cinjenice za prikaz stabla bodovanja, a Drools backward chaining query za impact proveru. Za izabrani potez prikazuju se:

- ukupan skor;
- pozitivni razlozi;
- negativni razlozi;
- impact pitanja sa odgovorima YES/NO;
- stablo bodovanja.

## Pokretanje

Pokretanje testova:

```powershell
mvn clean test
```

Pokretanje web aplikacije:

```powershell
mvn "-Dmaven.test.skip=true" spring-boot:run
```

Aplikacija se otvara na `http://localhost:8080`. Ako je port zauzet:

```powershell
mvn "-Dmaven.test.skip=true" spring-boot:run "-Dspring-boot.run.arguments=--server.port=8000"
```

Web aplikacija prikazuje stanje partije za 2 igraca, tablu, legalne poteze, preporuceni potez i detaljno objasnjenje za selektovani potez.

## Glavni fajlovi

- `src/main/java/com/splendor/assistant/WebApplication.java`
- `src/main/java/com/splendor/assistant/game/`
- `src/main/java/com/splendor/assistant/web/`
- `src/main/java/com/splendor/assistant/service/RecommendationService.java`
- `src/main/java/com/splendor/assistant/service/MoveGenerator.java`
- `src/main/java/com/splendor/assistant/model/facts/analysis/`
- `src/main/java/com/splendor/assistant/model/facts/strategy/`
- `src/main/java/com/splendor/assistant/model/facts/scoring/`
- `src/main/java/com/splendor/assistant/model/facts/explanation/`
- `src/main/java/com/splendor/assistant/model/explanation/`
- `src/main/resources/rules/`
