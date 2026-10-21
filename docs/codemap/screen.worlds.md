# Arkea Code Map: `com.aryston.arkea.screen.worlds`

Part of the [Arkea code map](../CODEMAP.md). Every class of this package has its entry here and nowhere else (rule D1).

### `com.aryston.arkea.screen.worlds`

#### ArkWorldSelectScreen
- Path: `src/main/java/com/aryston/arkea/screen/worlds/ArkWorldSelectScreen.java`
- Role: The singleplayer window of the design (replaces `SelectWorldScreen`): world rows on the left, the details panel of the selected world on the right, search, a sort link that cycles last played, name and game mode (kept in the config), empty and loading states, and Create New World and Play Selected World in the footer. Double-click or Enter on the selected row plays, Delete opens the delete dialog, F5 reloads. The list reloads every time the screen is shown.
- Depends on: `WorldLibrary`, `WorldOperations`, `WorldDialogs`, `WorldRow`, `WorldDetails`, `WorldSort`, `JoinTarget`.

#### WorldLibrary
- Path: `src/main/java/com/aryston/arkea/screen/worlds/WorldLibrary.java`
- Role: Loads the level summaries asynchronously and counts versions so the screen rebuilds, loads world icons into favicon textures on demand and measures size and play time in the background.

#### WorldFacts
- Path: `src/main/java/com/aryston/arkea/screen/worlds/WorldFacts.java`
- Role: Folder size and the local player's play time (from `players/stats/<uuid>.json`) of a world.

#### WorldRow
- Path: `src/main/java/com/aryston/arkea/screen/worlds/WorldRow.java`
- Role: 72 high world row: 128 wide cover image, name with tags, mode, difficulty and version, last played, accent fill, border and play badge when selected. A click selects, a click on the badge, a double-click or Enter plays.

#### WorldDetails
- Path: `src/main/java/com/aryston/arkea/screen/worlds/WorldDetails.java`
- Role: The 380 wide details panel: 190 high cover, name, folder, info rows (game mode, difficulty, version, last played, play time, size), Play, Edit, Re-Create and Delete.

#### WorldImage
- Path: `src/main/java/com/aryston/arkea/screen/worlds/WorldImage.java`
- Role: Draws a world icon cropped to fill a box, or the dimmed vanilla panorama when the world has none.

#### WorldTags
- Path: `src/main/java/com/aryston/arkea/screen/worlds/WorldTags.java`
- Role: Tags of a world: hardcore, cheats, open elsewhere, incompatible, newer or older version, experimental.

#### WorldText
- Path: `src/main/java/com/aryston/arkea/screen/worlds/WorldText.java`
- Role: Game mode, difficulty and detail lines, relative last played times (today with the time, yesterday, days, months, years), sizes and play times.

#### WorldSort
- Path: `src/main/java/com/aryston/arkea/screen/worlds/WorldSort.java`
- Role: World orders with their labels: last played, name, game mode.

#### WorldDialogs
- Path: `src/main/java/com/aryston/arkea/screen/worlds/WorldDialogs.java`
- Role: The delete dialog with the world image and a backup checkbox, the edit dialog (rename, make backup, open folder, optimize, reset icon, backups folder) and the optimize confirmation with backup and erase cache options.

#### WorldOperations
- Path: `src/main/java/com/aryston/arkea/screen/worlds/WorldOperations.java`
- Role: Plays, creates, recreates, renames, backs up, deletes and optimizes worlds the way `WorldSelectionList` and `EditWorldScreen` do; slow work runs on the IO pool with busy toasts and ends in a success or error toast.

#### ArkCreateWorldScreen
- Path: `src/main/java/com/aryston/arkea/screen/worlds/ArkCreateWorldScreen.java`
- Role: The create world window of the design. Wraps the vanilla `CreateWorldScreen` that `ClientEvents` replaced and edits its `WorldCreationUiState` with Arkea controls in Game, World and More tabs; creating, game rules, data packs, experiments and the superflat editor run the vanilla code, so every return to the vanilla screen opens a new wrapper (the selected tab is kept per vanilla screen).
- Depends on: `CreateWorldScreenInvoker`, `SettingsPanel`, `ListCycleModel`, `ArkTab`.
