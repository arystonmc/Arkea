# Arkea Architecture

How the Arkea interface engine works, what it relies on in Minecraft 26.3 and the rules every new screen follows. The class list is in `docs/CODEMAP.md`.

The sections live in `docs/architecture/`, one file per topic. Read the invariants below, then open only the section your task needs.

## Invariants

- A text field must announce text input to the game (`onTextInputFocusChange`), otherwise 26.3 delivers no typed characters.
- The frame in which a screen switches still draws the old screen, so a page switch never shows an empty frame.
- Nothing that loads a world or blocks for long runs inside `extractRenderState`; screens hand such actions to the main thread queue.
- Only one blur per frame exists in 26.3 (`blurBeforeThisStratum` throws on a second call). Screens never request blur themselves; `ArkScreen` does it for the dialog.
- Every widget is drawn exactly once per frame through `ArkWidget.render`, otherwise its hit box is stale.
- A widget moves its own content only through `contentShiftX` and the press offset of `ArkWidget`, never with its own pose translation, so the focus ring and hit box move with it.
- Widgets live in a screen's `buildUi`, which runs on every init; state that must survive a resize (the splash text) is kept in the screen.
- The design system never draws Mojang textures except vanilla's own panorama.

## Sections

| Section | File | Covers |
|---|---|---|
| Layers | [layers.md](architecture/layers.md) | `ui` never imports from `screen` or `config`; the accent reaches it through `Theme.setAccentSource`. |
| Coordinates | [coordinates.md](architecture/coordinates.md) | Screens are laid out in design pixels of the 1366 x 768 reference. |
| Frame Flow | [frame-flow.md](architecture/frame-flow.md) | `Screen.extractRenderStateWithTooltipAndSubtitles` calls `extractBackground` (the title screen draws the vanilla panorama there), opens a ... |
| Rendering | [rendering.md](architecture/rendering.md) | Everything is drawn through `UiGraphics`. |
| Window Screens | [window-screens.md](architecture/window-screens.md) | `ArkWindowScreen` draws the shell of every settings screen: 1238 x 704 window centered on the canvas, sidebar with brand and grouped ... |
| Menu Screens | [menu-screens.md](architecture/menu-screens.md) | Window screens without a sidebar (singleplayer, create world, multiplayer, mods) return `null` from `brand()`; the header then spans the ... |
| In-Game Screens and Vanilla Skins | [in-game-screens-and-vanilla-skins.md](architecture/in-game-screens-and-vanilla-skins.md) | `PauseScreen` and `DeathScreen` are never replaced: the game checks `instanceof PauseScreen` (toasts, pausing, F3 + Esc) and `instanceof ... |
| Config Windows | [config-windows.md](architecture/config-windows.md) | `ArkeaConfigScreen.Builder` collects groups, pages, sections and options into a `Definition`. |
| Menu Backgrounds | [menu-backgrounds.md](architecture/menu-backgrounds.md) | The player picks a background on the Arkea settings page. |
| Motion | [motion.md](architecture/motion.md) | Time comes from `Util.getMillis()`, never ticks. |
| Input, Focus and Narration | [input-focus-and-narration.md](architecture/input-focus-and-narration.md) | `ArkWidget` implements vanilla `GuiEventListener` and `NarratableEntry` and is registered with `Screen.addWidget`. |
| Vanilla Coupling | [vanilla-coupling.md](architecture/vanilla-coupling.md) | Every vanilla or NeoForge class Arkea relies on and what it is used for. |
| Porting Checklist for a New Minecraft Version | [porting-checklist.md](architecture/porting-checklist.md) | Check `GuiGraphicsExtractor`, `GuiElementRenderState`, `GuiRenderState` (layering and blur), `RenderPipelines.GUI` and `GUI_TEXTURED` ... |
| Testing Tools | [testing-tools.md](architecture/testing-tools.md) | `docs/API.md` is the developer guide; the Component Gallery (`ArkGalleryScreen`) shows every component and is part of the interface check ... |
