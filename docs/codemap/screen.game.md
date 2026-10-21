# Arkea Code Map: `com.aryston.arkea.screen.game`

Part of the [Arkea code map](../CODEMAP.md). Every class of this package has its entry here and nowhere else (rule D1).

### `com.aryston.arkea.screen.game`

#### PauseSkin
- Path: `src/main/java/com/aryston/arkea/screen/game/PauseSkin.java`
- Role: Pause menu of the design over the vanilla `PauseScreen` (kept because the game checks `instanceof PauseScreen`): world name, title, Back to Game, a grid of tiles for every vanilla and mod button, small icon buttons (bug report, feedback, friends, player reporting), Mods and the red Save and Quit, plus the world card. Rows stagger in.
- Notes: F3 + Esc (no menu) stays vanilla.

#### PauseCard
- Path: `src/main/java/com/aryston/arkea/screen/game/PauseCard.java`
- Role: World card of the pause menu: world icon or server icon, day, clock and weather, game mode, difficulty, players, dimension, and whether the world is local.

#### PauseExtras
- Path: `src/main/java/com/aryston/arkea/screen/game/PauseExtras.java`
- Role: Adds a Helion Graphics button to the pause menu when Helion is installed.

#### DeathSkin
- Path: `src/main/java/com/aryston/arkea/screen/game/DeathSkin.java`
- Role: Death screen of the design over the vanilla `DeathScreen` (kept for the game's `instanceof` checks): red tint and vignette, large title, cause, score and the death coordinates (with the death point switch, not with reduced debug info) fading in late, Respawn and Title Screen buttons that keep the vanilla one second delay.

#### ArkStatsScreen
- Path: `src/main/java/com/aryston/arkea/screen/game/ArkStatsScreen.java`
- Role: Statistics window of the design: world name, General, Items and Mobs tabs, two column general stats, item table with sortable columns and item icons, mob rows with kill bars; requests the stats like vanilla and shows a spinner until they arrive.

#### StatsData
- Path: `src/main/java/com/aryston/arkea/screen/game/StatsData.java`
- Role: Reads general, item and mob statistics from the `StatsCounter`.
