# Arkea Code Map: `com.aryston.arkea.screen.options.packs`

Part of the [Arkea code map](../CODEMAP.md). Every class of this package has its entry here and nowhere else (rule D1).

### `com.aryston.arkea.screen.options.packs`

#### ArkPacksScreen
- Path: `src/main/java/com/aryston/arkea/screen/options/packs/ArkPacksScreen.java`
- Role: Resource packs page on the vanilla `PackSelectionModel`: available and selected columns, add, move and remove buttons, incompatible packs confirmed in a dialog, search, drag and drop of pack files (checked for forbidden links like vanilla), folder watching, Open Pack Folder. Applies the packs when the page is left, only if the selection changed.

#### PackRow
- Path: `src/main/java/com/aryston/arkea/screen/options/packs/PackRow.java`
- Role: 64 high pack row with icon, name, INCOMPATIBLE or REQUIRED tag, first description line, actions and the full description as tooltip.

#### DropHintRow
- Path: `src/main/java/com/aryston/arkea/screen/options/packs/DropHintRow.java`
- Role: Dashed "Drop .zip packs here" hint under the available packs.

#### PackIcons
- Path: `src/main/java/com/aryston/arkea/screen/options/packs/PackIcons.java`
- Role: Loads and caches `pack.png` of each pack as a texture, like vanilla.

#### PackFolderWatcher
- Path: `src/main/java/com/aryston/arkea/screen/options/packs/PackFolderWatcher.java`
- Role: Watches the resource pack folder so new or removed packs appear without reopening the page.
