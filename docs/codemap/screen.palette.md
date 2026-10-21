# Arkea Code Map: `com.aryston.arkea.screen.palette`

Part of the [Arkea code map](../CODEMAP.md). Every class of this package has its entry here and nowhere else (rule D1).

### `com.aryston.arkea.screen.palette`

#### CommandPaletteScreen
- Path: `src/main/java/com/aryston/arkea/screen/palette/CommandPaletteScreen.java`
- Role: Ctrl+K command palette pushed as a screen layer over any menu: search field, grouped results (actions, pages, settings, worlds, servers), arrow keys, Enter, Esc and mouse.

#### PaletteEntry
- Path: `src/main/java/com/aryston/arkea/screen/palette/PaletteEntry.java`
- Role: Group, icon, label, detail and action of one palette result.

#### PaletteIndex
- Path: `src/main/java/com/aryston/arkea/screen/palette/PaletteIndex.java`
- Role: Builds palette entries: menu actions, folders, accent colors, options pages, every setting, recent worlds (async) and servers.

#### SettingsIndex
- Path: `src/main/java/com/aryston/arkea/screen/palette/SettingsIndex.java`
- Role: Lists every setting row of the options pages by building each page off screen once per language.
