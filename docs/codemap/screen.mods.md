# Arkea Code Map: `com.aryston.arkea.screen.mods`

Part of the [Arkea code map](../CODEMAP.md). Every class of this package has its entry here and nowhere else (rule D1).

### `com.aryston.arkea.screen.mods`

#### ArkModsScreen
- Path: `src/main/java/com/aryston/arkea/screen/mods/ArkModsScreen.java`
- Role: The mods window of the design (replaces the NeoForge `ModListScreen`): 340 wide mod list with search, details panel, Open Settings, Homepage and Report Issue (through the vanilla link confirmation), Open Mods Folder.
- Depends on: `ModEntry`, `ModIcons`, `ModRow`, `ModDetails`.

#### ModEntry
- Path: `src/main/java/com/aryston/arkea/screen/mods/ModEntry.java`
- Role: A mod container with its NeoForge `ModDisplayInfo` (custom or default), icon resource (the vanilla pack icon for Minecraft), config screen factory and update check result.
- Notes: For mods without a custom `ModDisplayInfo`, `authors` and `credits` are read from the mod metadata directly and accept a string or a list, because `DefaultModDisplayInfo` casts both to a string and throws a `ClassCastException` for lists (Distant Horizons). `VersionChecker.getResult` can return null, so `hasUpdate` and `latestVersion` treat a missing result as no update.

#### ModRow
- Path: `src/main/java/com/aryston/arkea/screen/mods/ModRow.java`
- Role: 56 high mod row with icon or letter tile, name, version and UPDATE and CONFIG tags.

#### ModDetails
- Path: `src/main/java/com/aryston/arkea/screen/mods/ModDetails.java`
- Role: Details panel: 72 pixel icon, name, version and loader chips, authors, mod id, license, credits, latest version, description and the buttons, or "No config screen".

#### ModIcons
- Path: `src/main/java/com/aryston/arkea/screen/mods/ModIcons.java`
- Role: Loads mod icons from their pack resources into dynamic textures, freed when the screen closes.
