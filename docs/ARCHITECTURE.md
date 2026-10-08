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
| Library API | `api.config` | The design system; builds config windows for other mods |
| Screens | `screen.*` | The design system and vanilla data and screens |
| Menu backgrounds | `background` | Files, JCodec, STB, textures; no widgets |
| Wiring | `integration`, `config`, `mixin` | NeoForge events and config |

`ui` never imports from `screen` or `config`; the accent reaches it through `Theme.setAccentSource`. A new screen only needs `ArkScreen`, widgets and `UiGraphics`. `api` is the stable surface for other mods; `ui` is public too (see `docs/API.md`).

## Coordinates

- Screens are laid out in design pixels of the 1366 x 768 reference. `UiScale` picks a scale in quarter steps so the whole design fits the window, and the canvas grows beyond 1366 x 768 on wider or taller windows. Layouts anchor to the canvas edges like the CSS of the design.
- `ArkScreen` scales the pose by `scale / guiScale`, so one unit is one design pixel. Mouse positions arrive in GUI units and are divided by the same factor.
- Rectangles are snapped to whole physical pixels while the pose has no rotation. Line thickness never drops below one physical pixel.
- Text uses the vanilla font at the design size. When a font pixel comes within 0.15 of a whole number of physical pixels the scale snaps to it, so most text stays crisp; other sizes keep their exact design size so labels fit and the hierarchy of the design survives on every resolution.

## Frame Flow

1. `Screen.extractRenderStateWithTooltipAndSubtitles` calls `extractBackground` (the title screen draws the vanilla panorama there), opens a new stratum and calls `ArkScreen.extractRenderState`.
2. `ArkScreen` starts a pending navigation when the exit animation has ended, otherwise builds a `UiGraphics` for the frame, pushes the design scale and calls `renderUi` of the screen with the mouse in canvas coordinates (outside the canvas while a dialog is open or the screen is leaving).
3. Widgets render themselves, store where their bounds landed on the canvas (`UiGraphics.canvasBox`) and update hover from that box, so hover and clicks follow enter animations.
4. The tooltip of the hovered widget renders on top.
5. An open dialog renders on the next stratum. The menu background blur is requested for that stratum while the dialog is shown. Window screens blur their background instead (`blursBackground`), and the dialog then uses that blur.

## Rendering

- Everything is drawn through `UiGraphics`. It records `UiMeshRenderState`s (custom `GuiElementRenderState`s) with vertices already transformed into GUI coordinates.
- The GUI pipelines cull back faces. `MeshBuilder` stores every quad in the winding vanilla uses (negative signed area in GUI coordinates), whatever transform produced it.
- Vanilla puts every element that overlaps an earlier one on a higher layer, and each layer is a separate draw. Icons and shadows are therefore one render state each, holding all their quads.
- Soft shadows and glows are nine slices of one generated soft edge texture (Gaussian profile, CSS blur radius = two sigma). The vignette is a generated radial texture stretched over the canvas. Both are linear filtered.
- Icons are the SVG paths of the design, parsed once into polylines and drawn as 1.5 pixel quads with square caps or as filled convex polygons, so they stay sharp at every scale without textures.
- Alpha is a stack in `UiGraphics`: every color is multiplied by it. This is how fades work, because 26.x has no global shader color.

## Window Screens

- `ArkWindowScreen` draws the shell of every settings screen: 1238 x 704 window centered on the canvas, sidebar with brand and grouped navigation, header with back, breadcrumb, title and close, content area, footer with a note and buttons.
- Its background is the vanilla panorama on the title screen or the world in game, blurred once, with a dim layer that fades with the window.
- The window enters with opacity, a 16 pixel rise and a 0.98 scale (450 ms) and exits in reverse (220 ms); content rows stagger in with `renderRow`.
- A subclass builds its content in `buildContent` inside the content box, returns where it ends and draws it in `renderContent`. Content taller than the area scrolls: it is clipped with a scissor, translated by the eased offset of `ScrollArea`, and widgets take their hit box from the clipped box. Keyboard focus scrolls the focused row into view.
- `switchTo` moves between window screens without closing the window: the old content fades and rises 8 pixels in 160 ms, the next screen starts with the window already open and only its rows and title animate in.
- Settings pages lay out with `SettingsPanel` (the two column grid of the design) and get their controls from `OptionControls`, which reads the value set of each vanilla `OptionInstance`. Vanilla values, listeners and side effects stay in charge; the controls only read and set options. Every vanilla options page has an Arkea version; Helion opens its own config screen.
- Long pages only draw the rows inside the visible area. Pages with a search field (key binds, language, packs) rebuild their content on every change and keep the focus on the widget with the same key.
- Popups (dropdown menus) render after the content and take input first; a click outside closes them.

## Menu Screens

- Window screens without a sidebar (singleplayer, create world, multiplayer, mods) return `null` from `brand()`; the header then spans the whole window. List screens keep their selection, sort and tab in screen fields and rebuild on every change, like the settings pages.
- Replaceable screens are swapped in `ScreenEvent.Opening` (exact class only) and keep the vanilla previous screen through an accessor. Screens whose vanilla instance holds the logic are wrapped instead: `ArkCreateWorldScreen` edits the `WorldCreationUiState` of the vanilla `CreateWorldScreen` and calls its private creation methods through an invoker. Vanilla sub screens (game rules, data packs, experiments, superflat, optimize) still return to the vanilla instance, which `ClientEvents` wraps again.
- Loading screens are never replaced, because game code depends on their classes (`instanceof LevelLoadingScreen` in the HUD, portals, music and packet handling) and `ConnectScreen` runs the connection. `ScreenEvent.Render.Pre` is cancelled for them and `LoadingSkin` draws the design from the screen state (accessors). The vanilla buttons stay the input targets: every frame they are moved to the boxes the design draws and painted with `ButtonPainter`.
- Slow world operations (backup, delete, rename, measuring sizes) run on the IO pool and report with menu toasts; dialogs carry custom content through `DialogForm`, and the delete dialog shows the world image as a hero.
- Menu toasts (`ArkToasts`) live outside any screen, so a toast started on one screen finishes on the next; every `ArkScreen` draws them above its content and below dialogs, and a click on a toast is handled before the content.

## Config Windows

- `ArkeaConfigScreen.Builder` collects groups, pages, sections and options into a `Definition`. `ArkConfigScreen.open` creates a `ConfigSession` and the screen of the first page that has options. Every page is its own `ArkConfigScreen` sharing the session, so switching pages plays the content animation of `switchTo` and keeps unapplied values, expanded sections and text field states.
- Controls never write to a config directly: they read `session.value(option)` (unapplied value first, then the binding) and call `session.stage`. `apply` writes all staged values, saves each binding once (a `ModConfigSpec` save fires the NeoForge reloading event) and runs the apply listeners. `ApplyMode.IMMEDIATE` writes and saves at once.
- Requirements are evaluated every frame (`setActive` on the control), so turning a switch off disables the rows that need it without a rebuild. Presets, reset, paste and collapsing rebuild the page.
- `ClientEvents` replaces the exact NeoForge `ConfigurationScreen` class with `ModConfigScreens.forMod` (reads the mod through `ConfigurationScreenAccessor`). `ModConfigScreens` walks `ModConfigSpec.getValues()` and `getSpec()`: top level values form a General page, every top level section a page, nested sections become sections. Unloaded configs and synced configs on a remote or LAN server show a notice instead of controls, like NeoForge.
- The context menu and popups (dropdowns, the color picker) live in `ArkScreen`: right-click asks `contextMenu(x, y)`, and an open popup receives clicks, drags, releases, wheel, keys and typed characters first. Dialogs and side sheets share the `Overlay` interface.

## Menu Backgrounds

- The player picks a background on the Arkea settings page. `PanoramaMixin` cancels `Panorama.extractRenderState` and `MenuBackground` draws the selected background instead, so it shows wherever vanilla would show the panorama: the title screen, the options window over it and other menus.
- Imports run once, on a worker thread: `BackgroundImporter` turns a video, GIF or picture into a folder with JPEG frames (or one JPEG), a thumbnail and `background.json`. Nothing is converted while the menu is open, and the original file is never needed again.
- Videos convert in parallel: `VideoConverter` splits them at their key frames, because every key frame to key frame part decodes on its own. Each `VideoSegment` decodes with its own JCodec decoder, skips non-reference frames that no output frame shows (half the frames of a 60 fps video with B frames), and `PictureScaler` shrinks the YUV planes before converting colors, so only 720p pixels are converted. `FrameWriter` encodes the JPEGs on separate threads. The number of decoders follows the cores and the free memory (a 4K decoder needs about 200 MB).
- Speed limits: decoding stays the cost. A 1080p clip of 20 seconds takes about 10 seconds, a 4K 60 fps clip of 49 seconds about a minute on 16 threads. Only a hardware or native decoder would bring 4K imports down to a few seconds.
- The Arkea page scans the library when it opens and polls the folder once a second; a dropped file is imported once `DropFolder` sees the same size and date twice and no program writes to it.
- Why JPEG frames: decoding H.264 in Java (JCodec) runs at about 30 to 50 frames per second for 720p, too close to the frame budget of the menu, and JCodec cannot re-encode fast enough. STB decodes a 720p JPEG in about 3.5 ms, the frames load lazily from disk and a GIF would only lose colors (256 per frame) and be larger. A 12 second 720p video takes about 6 MB.
- Playback: `BackgroundPlayer` decodes upcoming frames on a daemon thread into a queue of three `NativeImage`s; the render thread copies one into a `DynamicTexture` when it is due. A background not drawn for three seconds is released.
- The texture is drawn with a linear, clamp to edge sampler from `RenderSystem.getSamplerCache()` through `GuiGraphicsExtractor.blit`, cover fitted to the window.
- JCodec is embedded with Jar-in-Jar; only the importer touches it.

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

- A text field must announce text input to the game (`onTextInputFocusChange`), otherwise 26.3 delivers no typed characters.
- The frame in which a screen switches still draws the old screen, so a page switch never shows an empty frame.
- Only one blur per frame exists in 26.3 (`blurBeforeThisStratum` throws on a second call). Screens never request blur themselves; `ArkScreen` does it for the dialog.
- Every widget is drawn exactly once per frame through `ArkWidget.render`, otherwise its hit box is stale.
- A widget moves its own content only through `contentShiftX` and the press offset of `ArkWidget`, never with its own pose translation, so the focus ring and hit box move with it.
- Widgets live in a screen's `buildUi`, which runs on every init; state that must survive a resize (the splash text) is kept in the screen.
- The design system never draws Mojang textures except vanilla's own panorama.

## Vanilla Coupling

| Vanilla or NeoForge | Use |
|---|---|
| `ScreenEvent.Opening` | Replace `TitleScreen` with `ArkTitleScreen` |
| `GuiGraphicsExtractor.submitGuiElementRenderState`, `peekScissorStack`, `pose`, `text`, `nextStratum`, `blurBeforeThisStratum`, `requestCursor` | All drawing |
| `Panorama.extractRenderState` + `PanoramaMixin` | Title background, replaced by a custom background when one is selected |
| `DynamicTexture`, `NativeImage`, STB `stbi_load_from_memory`, `RenderSystem.getSamplerCache`, `GuiGraphicsExtractor.blit(GpuTextureView, GpuSampler, ...)`, `ClientTickEvent.Post` | Custom background playback and release |
| SDL `SDL_ShowOpenFileDialog`, `Window.handle`, `Screen.onFilesDrop`, `Blaze3D.openPath` | Importing backgrounds, opening the backgrounds folder |
| `SplashManager.getSplash` + `SplashRendererAccessor` | Splash text |
| `LevelStorageSource.findLevelCandidates`, `loadLevelSummaries`, `FaviconTexture.forWorld`, `WorldOpenFlows.openWorld` | Jump back in card |
| `Minecraft.allowsMultiplayer`, `isNameBanned`, `multiplayerBan` | Disabled multiplayer and Realms buttons |
| `ModList.get().size()`, `ModListScreen.create` | Mods button |
| `OptionsScreen.getLastScreen`, `Options.fov`, `Options.save`, the vanilla options sub-screens, `IConfigScreenFactory` | Options screen |
| `OptionInstance` (`values`, `toString`, `caption`, value set types) + `OptionInstanceAccessor`, `TooltipAccessor` | Option controls, labels, tooltips, defaults |
| `OptionsSubScreen` + `OptionsSubScreenAccessor`, `ScreenEvent.Opening` | Replacing the vanilla options pages |
| `Minecraft.onTextInputFocusChange`, `textInputManager().setTextInputArea` | Typed characters and IME position for `ArkTextField` |
| `KeyMapping` (`same`, `setKeyModifierAndCode`, `resetMapping`), NeoForge `KeyModifier`, `IKeyMappingExtension` | Key binds page |
| `Options.load` + `OptionsMixin` | Restoring modifier key bindings that NeoForge 26.3 beta drops |
| `LanguageManager`, `Minecraft.reloadResourcePacks` | Language page |
| `PackSelectionModel`, `PackDetector`, `Util.copyBetweenDirs`, `Options.updateResourcePacks` | Resource packs page |
| `GpuWarnlistManager`, `UnsupportedGraphicsWarningScreen`, `Window.changeFullscreenVideoMode`, `Minecraft.updateMaxMipLevel`, `delayTextureReload` | Video page side effects copied from `VideoSettingsScreen` |
| `SelectWorldScreen` + accessor, `LevelSummary`, `LevelStorageAccess` (`makeWorldBackup`, `deleteLevel`, `renameLevel`), `WorldOpenFlows` (`openWorld`, `recreateWorldData`), `OptimizeWorldScreen.create`, `CreateWorldScreen.openFresh`, `createFromExisting` | Singleplayer screen |
| `CreateWorldScreen` + `CreateWorldScreenInvoker`, `WorldCreationUiState`, `WorldCreationGameRulesScreen`, `PresetEditor` | Create world screen |
| `JoinMultiplayerScreen` + accessor, `ServerList`, `ServerData`, `ServerStatusPinger`, `LanServerDetection`, `ConnectScreen.startConnecting`, `ServerAddress` | Multiplayer screen |
| `ScreenEvent.Render.Pre`, `ConnectScreen`, `LevelLoadingScreen`, `LevelLoadTracker`, `ProgressScreen`, `GenericMessageScreen`, `DisconnectedScreen` + accessors | Loading skin |
| NeoForge `ModListScreen` + accessor, `ModDisplayInfo`, `DefaultModDisplayInfo`, `ImageResource`, `IConfigScreenFactory`, `VersionChecker`, `ConfirmLinkScreen` | Mods screen |

## Porting Checklist for a New Minecraft Version

1. Check `GuiGraphicsExtractor`, `GuiElementRenderState`, `GuiRenderState` (layering and blur), `RenderPipelines.GUI` and `GUI_TEXTURED` (vertex format, culling) and `Screen.extractRenderStateWithTooltipAndSubtitles`.
2. Check the input records (`MouseButtonEvent`, `KeyEvent`, `InputWithModifiers`) and `ContainerEventHandler` mouse release forwarding.
3. Check that `Panorama.extractRenderState` still exists with that name (otherwise `PanoramaMixin` fails to apply and the custom background never shows) and that the `blit` overload with a texture view and a sampler is still there. Check whether NeoForge still drops modifier key bindings on load (the "Invalid keyMapping" warning); remove `KeyModifierRepair` and `OptionsMixin` once it does not. Check the fields of `SplashRenderer`, `OptionInstance`, `Tooltip` and `OptionsSubScreen` for the accessors, the option lists of the vanilla options sub-screens (new options must be added to the pages) and the side effects of `VideoSettingsScreen`.
4. Check the private fields and methods behind the accessors and the invoker (`lastScreen` of the singleplayer, multiplayer and mods screens, `onCreate` and the pack screens of `CreateWorldScreen`, the state fields of the loading screens) and whether game code still checks the loading screen classes.
5. Run `-Define arkea.uiCheck=all` and compare the screenshots with the design handoff. Run the client, open the title screen, hover, Tab through it, open and close the quit dialog, and compare with `screenshots/` of the design handoff.

## Testing Tools

- `docs/API.md` is the developer guide; the Component Gallery (`ArkGalleryScreen`) shows every component and is part of the interface check (`gallery`).

- Unit tests cover motion, scaling, the path parser, shadow profiles, dates, language files and background import (image, GIF and video conversion).
- `-Define arkea.uiCheck=all` (or groups such as `worlds,servers,loading`) opens every Arkea screen and popup, saves `screenshots/arkea_<step>.png` and closes the game; run it in a 1366 x 768 window to compare with the design at one to one.
- A file dropped into `run/arkea/backgrounds` is imported within about two seconds while the Arkea page is open, which tests imports without a file picker (the SDL picker needs a desktop portal on Linux, so it reports "unavailable" headless).
- In a cloud session the client runs headless: `Xvfb :99`, Mesa `mesa-vulkan-drivers` (llvmpipe Vulkan), `DISPLAY=:99 ./gradlew runClient`. `xdotool` moves the mouse, clicks and types, `import -window root` takes screenshots, so hover, focus, dialogs and screen switches can be checked frame by frame.
