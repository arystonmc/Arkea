# Arkea Code Map: `com.aryston.arkea.integration`

Part of the [Arkea code map](../CODEMAP.md). Every class of this package has its entry here and nowhere else (rule D1).

### `com.aryston.arkea.integration`

#### ClientEvents
- Path: `src/main/java/com/aryston/arkea/integration/ClientEvents.java`
- Role: Swaps a newly opened vanilla `TitleScreen` for `ArkTitleScreen` (not in demo mode), `OptionsScreen` for `ArkOptionsScreen` and the video, sound, chat, accessibility, skin, controls, mouse, key binds, language and font screens for the Arkea pages, keeping the previous screen, in `ScreenEvent.Opening`, each unless the config turns it off. With the menu screens on it also replaces `SelectWorldScreen`, `CreateWorldScreen`, `JoinMultiplayerScreen` and the NeoForge `ModListScreen`, and in `ScreenEvent.Render.Pre` lets `LoadingSkin` draw the connecting and loading screens instead of vanilla (any other screen ends the loading session). Registers the `LoadingOutro` layer above every other GUI layer, updates it in `RenderGuiEvent.Pre` and hides the other HUD layers while the outro covers the screen. Only the exact vanilla classes are replaced, so subclasses from other mods stay.
- Also ticks `MenuBackground` every client tick so a background nobody draws frees its texture and decoder thread. Runs `UiCheck` when it is requested.
- Depends on: `ArkeaConfig`, `ArkTitleScreen`, `ArkOptionsScreen`, the options pages, `OptionsSubScreenAccessor`, `MenuBackground`, `LoadingSkin`, `LoadingOutro`.

#### KeyModifierRepair
- Path: `src/main/java/com/aryston/arkea/integration/KeyModifierRepair.java`
- Role: NeoForge 26.3.0.51-beta unbinds every key binding saved with a modifier (`key.keyboard.j:CONTROL`) while loading the options. Right after the options load it reads those lines again and restores bindings that NeoForge left unbound; once NeoForge is fixed the bindings are already set and it does nothing.
- Depends on: `OptionsMixin`.

#### FileDialogs
- Path: `src/main/java/com/aryston/arkea/integration/FileDialogs.java`
- Role: Opens the native "open file" dialog of the system through SDL (`SDL_ShowOpenFileDialog`) with a file type filter, and hands the chosen paths back on the render thread. Reports a failure when the system has no file picker.

#### HudEvents
- Path: `src/main/java/com/aryston/arkea/integration/HudEvents.java`
- Role: While an Arkea HUD style is on, cancels the vanilla HUD layers it replaces in `RenderGuiLayerEvent.Pre` (hotbar, health, armor, food, vehicle health, air, the experience bar and level when the contextual bar shows experience, the selected item name, effects, scoreboard, Tab player list) and draws the Arkea version in the same layer, so layers of other mods keep their order. Boss bars are restyled one by one through `CustomizeGuiOverlayEvent.BossEventProgress`. Spectator mode and the locator and jump bars stay vanilla; nothing is drawn while the HUD is hidden (F1).
- Registers the Arkea GUI layers on the mod bus (`RegisterGuiLayersEvent`): damage direction above the camera overlays, hit marker above the crosshair, hotbar extras above the hotbar, pickup feed above the item name, location chip above the effects, a reset of `BossBarStack` below the boss bars, target card above the boss bars, sound radar above the subtitles. Every boss bar, Arkea or vanilla, reports its bottom to `BossBarStack` from `CustomizeGuiOverlayEvent.BossEventProgress`. They draw in every HUD style, Vanilla included, and hide with F1. Ticks `HudTracker` and registers the `SoundRadar` listener once.
- Also keeps the player list visibility and its narration in vanilla `PlayerTabOverlay.setVisible` with the vanilla rule (Tab held, not alone in singleplayer without a list objective).
- Depends on: `HudPainter`, `HudEffects`, `HudBossBar`, `BossBarStack`, `HudSidebar`, `HudTabList`, `HudSnapshot`, `HudSettings`, `HotbarLayout`, `HudAccessor`, `PlayerTabOverlayAccessor`.
