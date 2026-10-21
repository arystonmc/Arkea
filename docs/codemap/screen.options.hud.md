# Arkea Code Map: `com.aryston.arkea.screen.options.hud`

Part of the [Arkea code map](../CODEMAP.md). Every class of this package has its entry here and nowhere else (rule D1).

### `com.aryston.arkea.screen.options.hud`

#### ArkHudScreen
- Path: `src/main/java/com/aryston/arkea/screen/options/hud/ArkHudScreen.java`
- Role: The HUD Preview page: a large live preview of the chosen style and four style cards with their own previews.
- Depends on: `OptionsPageScreen`, `ArkeaConfig`, `HudSettings`.

#### HudStyleCard
- Path: `src/main/java/com/aryston/arkea/screen/options/hud/HudStyleCard.java`
- Role: Selectable card of one style: 2.4:1 preview with the current transparency and size, name, two lines of description and a check badge when chosen. Widget key `hud:<style>`.

#### HudStyleRow, HudPreviewRow
- Path: `src/main/java/com/aryston/arkea/screen/options/hud/HudStyleRow.java`, `src/main/java/com/aryston/arkea/screen/options/hud/HudPreviewRow.java`
- Role: Panel rows: one style card with a height that follows its width, and the wide live preview (Minimal switches between a full and a hurt player every few seconds to show the fade).
