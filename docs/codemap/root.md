# Arkea Code Map: `com.aryston.arkea`

Part of the [Arkea code map](../CODEMAP.md). Every class of this package has its entry here and nowhere else (rule D1).

### `com.aryston.arkea`

#### Arkea
- Path: `src/main/java/com/aryston/arkea/Arkea.java`
- Role: Entry point annotated with `@Mod`. Registers the client config, the NeoForge config screen, the client events, the HUD layers and events, the chat sender listener and the tooltip components (the latter two on the mod event bus it receives).
- Members:
  - `MOD_ID`: the mod id.
  - `LOGGER`: shared logger for the whole mod.
- Depends on: `ArkeaConfig`, `ClientEvents`.
