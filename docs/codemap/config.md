# Arkea Code Map: `com.aryston.arkea.config`

Part of the [Arkea code map](../CODEMAP.md). Every class of this package has its entry here and nowhere else (rule D1).

### `com.aryston.arkea.config`

#### ArkeaConfig
- Path: `src/main/java/com/aryston/arkea/config/ArkeaConfig.java`
- Role: Client config spec (`arkea-client.toml`).
- Members:
  - `TITLE_SCREEN`: replaces the vanilla title screen when on.
  - `OPTIONS_SCREEN`: replaces the vanilla options screen when on.
  - `ACCENT`: accent theme of every Arkea screen.
  - `BACKGROUND`: folder name of the selected menu background, or `vanilla` for the panorama.
  - `BACKGROUND_PAN`: slow pan over still image backgrounds.
  - `MENU_SCREENS`: replaces the singleplayer, create world, multiplayer, mods and loading screens when on.
  - `JOIN_SOUND`: plays the level up chime when the loading outro of a world or server join starts.
  - `WORLD_SORT`: order of the singleplayer world list.
  - `HUD_STYLE`: in-game HUD style (`HudStyle`), Vanilla by default.
  - `HUD_OPACITY`, `HUD_SIZE`: plate transparency and HUD size in percent.
  - `TOOLTIPS`, `TOOLTIP_FOOD`, `TOOLTIP_DURABILITY`, `TOOLTIP_CONTAINERS`: Arkea tooltip card and its food, durability and container parts.
  - `TARGET_CARD`, `PICKUP_FEED`, `STACK_TOTAL`, `AMMO_COUNTER`, `INFO_CHIP`, `DEATH_POINT`, `DURABILITY_WARNING`, `ARMOR_ICONS`, `FOOD_PREVIEW`, `DAMAGE_DIRECTION`, `SLEEP_REMINDER`, `HIT_MARKER`, `SOUND_RADAR`: the HUD features (location chip, sleep reminder, hit marker and sound radar off by default).
  - `CROSSHAIR`: crosshair shape (`CrosshairStyle`).
  - `CHAT`: chat messages on Arkea plates (`ArkChat`), on by default.
  - `on`: reads a switch, false before the config loads.
  - `HUD_EFFECTS`, `HUD_BOSS_BARS`, `HUD_SCOREBOARD`, `HUD_TAB_LIST`: effect chips, slim boss bars, the scoreboard card and the player list card while an Arkea HUD style is on.
  - `set`: sets a value and saves the file at once (the Arkea settings page changes values live).
  - `SPEC`: the built spec.
