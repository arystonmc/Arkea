# Arkea Code Map: `com.aryston.arkea.api.clock`

Part of the [Arkea code map](../CODEMAP.md). Every class of this package has its entry here and nowhere else (rule D1).

### `com.aryston.arkea.api.clock`

Public library API. Another mod tells Arkea how to read the overworld clock; see `docs/API.md`.

#### WorldClockText
- Path: `src/main/java/com/aryston/arkea/api/clock/WorldClockText.java`
- Role: Turns overworld clock ticks into the time text of the info chip and pause card chip and the day number of the pause card. Holds one `Source`; `VANILLA` (tick 0 is 06:00, 1000 ticks an hour, day = ticks / 24000 + 1) is the default until a mod registers its own, which Equinox does so both clocks agree under seasonal day length.
- Members: `register`, `time`, `day`, `VANILLA`, `Source`.
