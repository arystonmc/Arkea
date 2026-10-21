# Arkea Code Map: `com.aryston.arkea.screen.options.arkea`

Part of the [Arkea code map](../CODEMAP.md). Every class of this package has its entry here and nowhere else (rule D1).

### `com.aryston.arkea.screen.options.arkea`

#### ArkArkeaScreen
- Path: `src/main/java/com/aryston/arkea/screen/options/arkea/ArkArkeaScreen.java`
- Role: The Menu Backgrounds page: Import (native file picker) and Open Folder buttons, a three column gallery of the panorama, every imported background (delete with confirmation), running imports and an import tile. Files dropped on the window are imported. Rebuilds when the library changes.
- Depends on: `OptionsPageScreen`, `BackgroundLibrary`, `FileDialogs`, `ArkeaConfig`.

#### MediaTile
- Path: `src/main/java/com/aryston/arkea/screen/options/arkea/MediaTile.java`
- Role: Base gallery tile: 16:9 thumbnail, name and second line, selected glow and border. Clicks on its corner button do not reach the tile.

#### BackgroundTile
- Path: `src/main/java/com/aryston/arkea/screen/options/arkea/BackgroundTile.java`
- Role: Tile of the panorama or an imported background with its thumbnail, a play badge for videos, a check when selected and the length, frame rate and size; selects it on click.

#### ImportTile
- Path: `src/main/java/com/aryston/arkea/screen/options/arkea/ImportTile.java`
- Role: Dashed tile that opens the file picker.

#### JobTile
- Path: `src/main/java/com/aryston/arkea/screen/options/arkea/JobTile.java`
- Role: Tile of a running import with its percentage and progress bar, or the error when it failed.

#### TileRow
- Path: `src/main/java/com/aryston/arkea/screen/options/arkea/TileRow.java`
- Role: Grid row holding a tile and its optional corner button (delete, cancel, dismiss).
