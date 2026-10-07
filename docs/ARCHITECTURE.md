# Arkea Architecture

How the Arkea interface engine works, what it relies on in Minecraft 26.3 and the rules every new screen follows. The class list is in `docs/CODEMAP.md`.

## Layers

| Layer | Package | Knows about |
|---|---|---|
| Theme | `ui.theme` | Design tokens only |
| Motion | `ui.anim` | Nothing but time in milliseconds |
| Layout | `ui.layout` | Window size and GUI scale |
| Render | `ui.render` | `GuiGraphicsExtractor`, GUI render states |
| Widgets and overlays | `ui.widget`, `ui.overlay` | Render, motion, vanilla input and narration interfaces |
| Screen base | `ui.screen` | Everything above, vanilla `Screen` |
| Screens | `screen.*` | The design system and vanilla data and screens |
| Wiring | `integration`, `config`, `mixin` | NeoForge events and config |

`ui` never imports from `screen`. A new screen only needs `ArkScreen`, widgets and `UiGraphics`.

## Coordinates

- Screens are laid out in design pixels of the 1366 x 768 reference. `UiScale` picks a scale in quarter steps so the whole design fits the window, and the canvas grows beyond 1366 x 768 on wider or taller windows. Layouts anchor to the canvas edges like the CSS of the design.
- `ArkScreen` scales the pose by `scale / guiScale`, so one unit is one design pixel. Mouse positions arrive in GUI units and are divided by the same factor.
- Rectangles are snapped to whole physical pixels while the pose has no rotation. Line thickness never drops below one physical pixel.
- Text uses the vanilla font. Its scale is rounded so every font pixel covers a whole number of physical pixels, which keeps glyphs crisp; small design sizes therefore share a size on low resolutions.

## Frame Flow

1. `Screen.extractRenderStateWithTooltipAndSubtitles` calls `extractBackground` (the title screen draws the vanilla panorama there), opens a new stratum and calls `ArkScreen.extractRenderState`.
2. `ArkScreen` starts a pending navigation when the exit animation has ended, otherwise builds a `UiGraphics` for the frame, pushes the design scale and calls `renderUi` of the screen with the mouse in canvas coordinates (outside the canvas while a dialog is open or the screen is leaving).
3. Widgets render themselves, store where their bounds landed on the canvas (`UiGraphics.canvasBox`) and update hover from that box, so hover and clicks follow enter animations.
4. The tooltip of the hovered widget renders on top.
5. An open dialog renders on the next stratum. The menu background blur is requested for that stratum while the dialog is shown.

## Rendering

- Everything is drawn through `UiGraphics`. It records `UiMeshRenderState`s (custom `GuiElementRenderState`s) with vertices already transformed into GUI coordinates.
- The GUI pipelines cull back faces. `MeshBuilder` stores every quad in the winding vanilla uses (negative signed area in GUI coordinates), whatever transform produced it.
- Vanilla puts every element that overlaps an earlier one on a higher layer, and each layer is a separate draw. Icons and shadows are therefore one render state each, holding all their quads.
- Soft shadows and glows are nine slices of one generated soft edge texture (Gaussian profile, CSS blur radius = two sigma). The vignette is a generated radial texture stretched over the canvas. Both are linear filtered.
- Icons are the SVG paths of the design, parsed once into polylines and drawn as 1.5 pixel quads with square caps or as filled convex polygons, so they stay sharp at every scale without textures.
- Alpha is a stack in `UiGraphics`: every color is multiplied by it. This is how fades work, because 26.x has no global shader color.

## Motion

- Time comes from `Util.getMillis()`, never ticks.
- `Presence` and `Timeline` implement "every enter has a shorter ease-in exit". Durations live in `Motion`, curves in `Easing`.
- Screen switches go through `ArkScreen.navigate` or `leave`: the screen plays its exit, then the switch happens. Input is ignored while leaving.
- `added()` restarts the enter animation each time a screen is shown again; a resize only lays out again.

## Input, Focus and Narration

- `ArkWidget` implements vanilla `GuiEventListener` and `NarratableEntry` and is registered with `Screen.addWidget`. Vanilla handles Tab, Shift+Tab, arrows and narration; widgets activate on Enter and Space when focused.
- A click activates on release over the widget, like a browser. The container forwards the release to the focused widget.
- The focus ring shows only when the last input came from the keyboard.
- While a dialog is open, `children()` returns only the dialog buttons, Escape cancels the dialog and narration reads the dialog title, body and focused button.

## Invariants

- Only one blur per frame exists in 26.3 (`blurBeforeThisStratum` throws on a second call). Screens never request blur themselves; `ArkScreen` does it for the dialog.
- Every widget is drawn exactly once per frame through `ArkWidget.render`, otherwise its hit box is stale.
- Widgets live in a screen's `buildUi`, which runs on every init; state that must survive a resize (the splash text) is kept in the screen.
- The design system never draws Mojang textures except vanilla's own panorama.

## Vanilla Coupling

| Vanilla or NeoForge | Use |
|---|---|
| `ScreenEvent.Opening` | Replace `TitleScreen` with `ArkTitleScreen` |
| `GuiGraphicsExtractor.submitGuiElementRenderState`, `peekScissorStack`, `pose`, `text`, `nextStratum`, `blurBeforeThisStratum`, `requestCursor` | All drawing |
| `Panorama.extractRenderState` | Title background |
| `SplashManager.getSplash` + `SplashRendererAccessor` | Splash text |
| `LevelStorageSource.findLevelCandidates`, `loadLevelSummaries`, `FaviconTexture.forWorld`, `WorldOpenFlows.openWorld` | Jump back in card |
| `Minecraft.allowsMultiplayer`, `isNameBanned`, `multiplayerBan` | Disabled multiplayer and Realms buttons |
| `ModList.get().size()`, `ModListScreen.create` | Mods button |

## Porting Checklist for a New Minecraft Version

1. Check `GuiGraphicsExtractor`, `GuiElementRenderState`, `GuiRenderState` (layering and blur), `RenderPipelines.GUI` and `GUI_TEXTURED` (vertex format, culling) and `Screen.extractRenderStateWithTooltipAndSubtitles`.
2. Check the input records (`MouseButtonEvent`, `KeyEvent`, `InputWithModifiers`) and `ContainerEventHandler` mouse release forwarding.
3. Check the fields of `SplashRenderer` for the accessor.
4. Run the client, open the title screen, hover, Tab through it, open and close the quit dialog, and compare with `screenshots/` of the design handoff.

## Testing Tools

- Unit tests cover motion, scaling, the path parser, shadow profiles, dates and language files.
- In a cloud session the client runs headless: `Xvfb :99`, Mesa `mesa-vulkan-drivers` (llvmpipe Vulkan), `DISPLAY=:99 ./gradlew runClient`. `xdotool` moves the mouse, clicks and types, `import -window root` takes screenshots, so hover, focus, dialogs and screen switches can be checked frame by frame.
