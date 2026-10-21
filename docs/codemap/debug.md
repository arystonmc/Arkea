# Arkea Code Map: `com.aryston.arkea.debug`

Part of the [Arkea code map](../CODEMAP.md). Every class of this package has its entry here and nowhere else (rule D1).

### `com.aryston.arkea.debug`

#### UiCheckGameToasts
- Path: `src/main/java/com/aryston/arkea/debug/UiCheckGameToasts.java`
- Role: Check helper that raises sample system and tutorial toasts.

#### UiCheck
- Path: `src/main/java/com/aryston/arkea/debug/UiCheck.java`
- Role: Developer check (`-Define arkea.uiCheck=all` or a comma list of groups: title, options, worlds, create, servers, mods, loading, toasts, gallery, config, arkea, helion, aervium, vanilla, game, hud, features). `Step.await` waits for a condition such as a loaded world; `Step.quick` takes its screenshot after a given number of ticks. Opens screens, presses widgets by key, sends keys, saves a screenshot after every step as `screenshots/arkea_<step>.png` and closes the game. Does nothing in production.

#### UiCheckToasts, UiCheckLoading, UiCheckServers, UiCheckConfig, UiCheckGame

#### UiCheckToasts, UiCheckLoading, UiCheckServers, UiCheckGame
- Path: `src/main/java/com/aryston/arkea/debug/UiCheckToasts.java`, `src/main/java/com/aryston/arkea/debug/UiCheckLoading.java`, `src/main/java/com/aryston/arkea/debug/UiCheckServers.java`, `src/main/java/com/aryston/arkea/debug/UiCheckGame.java`
- Role: Check helpers: sample toasts, a progress screen and a connection to an unroutable address (cancelled again), temporary sample servers that are removed at the end, and the statistics, death and F3 screens in a fresh world, then three chat messages sent by the player, one more captured mid-entrance with a slowed clock, the chat screen and its command suggestions (`/t`, which works without cheats), then thirty history lines and one wheel step up, captured mid-scroll with a slowed clock and at rest.

#### UiCheckConfig
- Path: `src/main/java/com/aryston/arkea/debug/UiCheckConfig.java`
- Role: Opens a named installed mod's `IConfigScreenFactory` screen for the interface check, returning to the parent when the mod or factory is absent. Used for Helion and Aervium instead of a Helion-only helper.

#### UiCheckHud
- Path: `src/main/java/com/aryston/arkea/debug/UiCheckHud.java`
- Role: Check helpers for the `hud` group: fills a fresh world with sample items, armor, effects, experience, a scoreboard and a boss bar through commands, switches the HUD style before each screenshot (changing the selected slot so the item name shows), sets low health and poison for the danger shot, slows the HUD clock tenfold (`slowClock`, shared with the chat check) and changes the slot, a stack and the health to capture the animations midway, holds the player list key with a sample header and footer, scrolls the HUD settings page to the style cards and switches back to Vanilla at the end.

#### UiCheckFeatures, UiCheckTooltipScreen
- Path: `src/main/java/com/aryston/arkea/debug/UiCheckFeatures.java`, `src/main/java/com/aryston/arkea/debug/UiCheckTooltipScreen.java`
- Role: Check helpers for the `features` group: a worn pickaxe, stacks, food, a bow with arrows and worn armor, an ore wall to look at, hunger, item and experience pickups, a hit from the left with a slowed clock, a horse, nightfall, a death with a respawn and a walk away, a boss bar with the target card, sample chat lines with the chat open and closed; and a screen that shows one item tooltip (food, worn tool, filled shulker box). Switches the features it turned on back off at the end.
