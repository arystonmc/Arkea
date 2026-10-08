# Arkea Code Map

Every class and source file of Arkea with its purpose. Find the right file here before opening any code. Keep this file in sync with the code in the same commit (rule D1). The big picture is in `docs/ARCHITECTURE.md`.

## Quick Lookup

| I want to change | Go to |
|---|---|
| Mod id, logger, startup | `Arkea` |
| Which vanilla screens Arkea replaces | `ClientEvents`, `ArkeaConfig` |
| Colors, accent themes | `ArkColors`, `Accent`, `Theme` |
| Animation curves and durations | `Easing`, `CubicBezier`, `Motion` |
| Enter and exit animations | `Presence`, `Timeline`, `Transition`, `Oscillation` |
| Design pixels to screen scaling | `UiScale` |
| Drawing rectangles, gradients, borders, shadows, icons, text | `UiGraphics`, `TextMetrics`, `TextStyle` |
| Icons | `Icons` (catalogue), `SvgPathParser` |
| Hover, press, focus, narration of every widget | `ArkWidget` |
| Buttons | `ArkButton`, `ButtonVariant`, `ArkIconButton`, `IconButtonStyle`, `ArkMenuButton`, `MenuButtonStyle`, `ArkTextLink` |
| Popups and tooltips | `ArkDialog`, `ArkTooltip` |
| Base of every Arkea screen (scaling, dialogs, exit animation) | `ArkScreen` |
| Settings window: sidebar, header, footer, window and row animations | `ArkWindowScreen`, `NavGroup`, `SidebarBrand` |
| Sliders, switches, cycle selectors, banners | `ArkSlider`, `SliderModel`, `ArkSwitch`, `ArkCycle`, `CycleModel`, `ArkBanner` |
| Sidebar items, tiles, setting rows | `ArkNavItem`, `ArkTile`, `SettingRow` |
| Two column settings layout, scrolling content | `SettingsPanel`, `SettingsSection`, `ScrollArea` |
| Options screen and the pages it links to | `ArkOptionsScreen`, `OptionsPage` |
| Options pages (video, sound, chat, accessibility, skin, controls, language) | `OptionsPageScreen`, `ArkVideoScreen`, `ArkSoundScreen`, `ArkChatScreen`, `ArkAccessibilityScreen`, `ArkSkinScreen`, `ArkControlsScreen`, `ArkLanguageScreen` |
| Key binds page: search, filters, find by key, binding, conflicts | `ArkKeyBindsScreen`, `KeyBindRow`, `KeyConflicts`, `ArkKeycap` |
| Resource packs page | `ArkPacksScreen`, `PackRow`, `PackIcons`, `PackFolderWatcher` |
| Search fields, dropdowns, segmented filters, choice tiles | `ArkTextField`, `TextFieldState`, `SearchText`, `ArkDropdown`, `DropdownMenu`, `ArkSegmented`, `ArkChoiceTile` |
| Rows of the settings grid | `PanelRow`, `SettingRow`, `WidgetRow`, `ToolbarRow`, `NoticeRow` |
| Key bindings with modifiers lost on start (NeoForge bug) | `KeyModifierRepair`, `OptionsMixin` |
| Which control a vanilla option gets, value labels, descriptions | `OptionControls`, `OptionText` |
| Title screen layout and actions | `ArkTitleScreen` |
| Custom menu backgrounds: library, import, playback | `BackgroundLibrary`, `BackgroundImporter`, `BackgroundPlayer`, `MenuBackground`, `PanoramaMixin` |
| Arkea settings page (background gallery, accent, screen toggles) | `ArkArkeaScreen`, `BackgroundTile`, `ImportTile`, `JobTile`, `ArkSwatch` |
| Native file picker | `FileDialogs` |
| Singleplayer: world list, details, delete and edit dialogs | `ArkWorldSelectScreen`, `WorldRow`, `WorldDetails`, `WorldDialogs`, `WorldOperations` |
| Create world | `ArkCreateWorldScreen`, `CreateWorldScreenInvoker` |
| Multiplayer: servers, LAN, pings, server dialogs | `ArkMultiplayerScreen`, `ServerRow`, `ServerPings`, `ServerDialogs` |
| Connecting, loading, disconnected screens | `LoadingSkin`, `LoadingViews` |
| Mods list | `ArkModsScreen`, `ModEntry`, `ModDetails` |
| Menu toasts | `ArkToasts`, `MenuToast`, `ToastTone` |
| Dialog content (forms, hero image, thumbnail) | `ArkDialog`, `DialogForm`, `DialogBody` |
| Checkbox, tabs, tags, letter tiles, empty states | `ArkCheckbox`, `ArkTab`, `Tag`, `LetterTile`, `EmptyState`, `PixelSpinner` |
| Automated screenshots of screens | `UiCheck` |
| Config screen library for other mods | `ArkeaConfigScreen`, `ConfigOption`, `ModConfigScreens`, `ArkConfigScreen` |
| Radio, stepper, chips, range, color picker, context menu, side sheet, preview | `ArkRadio`, `ArkStepper`, `ArkChip`, `ArkRangeSlider`, `ColorPickerPopup`, `ArkContextMenu`, `ArkSideSheet`, `ArkCompareView` |
| Component gallery | `ArkGalleryScreen` |
| Arkea look of every other screen and of other mods | `VanillaTheme`, theme mixins, `DialogSkin` |
| Pause, death and statistics screens | `PauseSkin`, `PauseCard`, `DeathSkin`, `ArkStatsScreen` |
| F3 debug cards, chat bar | `DebugCards`, `DebugScreenOverlayMixin`, `ChatScreenMixin`, `VanillaTheme.chatBar` |
| Jump back in card, splash text | `JumpBackInCard`, `SplashText`, `RecentWorld`, `LastPlayed` |
| Texts | `src/main/resources/assets/arkea/lang/` |
| Mod name, version, loader versions, license, authors | `gradle.properties` |
| Mod list metadata and dependencies | `neoforge.mods.toml` |
| Mod logo | `branding/logo.png` |
| Build setup, run configurations | `build.gradle` |

## Overview

- Side: client only (`dist = Dist.CLIENT`). `displayTest="IGNORE_ALL_VERSION"` lets players join servers without Arkea.
- Root package: `com.aryston.arkea`.
- `ui` is the reusable design system and never references a concrete screen. `screen` holds the screens built with it.

## Classes

### `com.aryston.arkea`

#### Arkea
- Path: `src/main/java/com/aryston/arkea/Arkea.java`
- Role: Entry point annotated with `@Mod`. Registers the client config, the NeoForge config screen and the client events.
- Members:
  - `MOD_ID`: the mod id.
  - `LOGGER`: shared logger for the whole mod.
- Depends on: `ArkeaConfig`, `ClientEvents`.

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
  - `WORLD_SORT`: order of the singleplayer world list.
  - `set`: sets a value and saves the file at once (the Arkea settings page changes values live).
  - `SPEC`: the built spec.

### `com.aryston.arkea.integration`

#### ClientEvents
- Path: `src/main/java/com/aryston/arkea/integration/ClientEvents.java`
- Role: Swaps a newly opened vanilla `TitleScreen` for `ArkTitleScreen` (not in demo mode), `OptionsScreen` for `ArkOptionsScreen` and the video, sound, chat, accessibility, skin, controls, mouse, key binds, language and font screens for the Arkea pages, keeping the previous screen, in `ScreenEvent.Opening`, each unless the config turns it off. With the menu screens on it also replaces `SelectWorldScreen`, `CreateWorldScreen`, `JoinMultiplayerScreen` and the NeoForge `ModListScreen`, and in `ScreenEvent.Render.Pre` lets `LoadingSkin` draw the connecting and loading screens instead of vanilla. Only the exact vanilla classes are replaced, so subclasses from other mods stay.
- Also ticks `MenuBackground` every client tick so a background nobody draws frees its texture and decoder thread. Runs `UiCheck` when it is requested.
- Depends on: `ArkeaConfig`, `ArkTitleScreen`, `ArkOptionsScreen`, the options pages, `OptionsSubScreenAccessor`, `MenuBackground`.

#### KeyModifierRepair
- Path: `src/main/java/com/aryston/arkea/integration/KeyModifierRepair.java`
- Role: NeoForge 26.3.0.51-beta unbinds every key binding saved with a modifier (`key.keyboard.j:CONTROL`) while loading the options. Right after the options load it reads those lines again and restores bindings that NeoForge left unbound; once NeoForge is fixed the bindings are already set and it does nothing.
- Depends on: `OptionsMixin`.

#### FileDialogs
- Path: `src/main/java/com/aryston/arkea/integration/FileDialogs.java`
- Role: Opens the native "open file" dialog of the system through SDL (`SDL_ShowOpenFileDialog`) with a file type filter, and hands the chosen paths back on the render thread. Reports a failure when the system has no file picker.

### `com.aryston.arkea.background`

#### BackgroundLibrary
- Path: `src/main/java/com/aryston/arkea/background/BackgroundLibrary.java`
- Role: The menu backgrounds in `<game folder>/arkea/backgrounds`: scans the entry folders when the Arkea page opens and polls the folder once a second while it is open (`poll`), imports files one at a time on a background worker (into a temporary folder, then an atomic move), auto imports loose media files dropped into the folder once `DropFolder` reports them settled (the originals move to `imported/`, or `failed/`), removes leftover temporary folders only on the first scan, selects the new entry after an import, deletes entries and counts changes in `version` so the settings page rebuilds.
- Depends on: `BackgroundImporter`, `BackgroundEntry`, `ImportJob`, `DropFolder`, `WorkerThreads`, `ArkeaConfig`, `MenuBackground`.

#### DropFolder
- Path: `src/main/java/com/aryston/arkea/background/DropFolder.java`
- Role: Tells which loose files in the backgrounds folder have finished copying: same size and modification time on two polls in a row, and no other program holds the file open for writing. Files whose import was cancelled are ignored until they change.

#### BackgroundEntry
- Path: `src/main/java/com/aryston/arkea/background/BackgroundEntry.java`
- Role: One imported background: name, kind (video or image), size, frame rate, frame count, bytes and date, stored as `background.json` next to `thumbnail.jpg` and either `image.jpg` or `frames/00000.jpg`...

#### BackgroundImporter
- Path: `src/main/java/com/aryston/arkea/background/BackgroundImporter.java`
- Role: Converts MP4, M4V, MOV, GIF, PNG, JPG and BMP into a background folder. Videos and animations become a JPEG frame sequence at their own frame rate (at most 30 fps, 1280 x 720 and 60 seconds); Videos go to `VideoConverter`, GIFs are converted frame by frame here; stills and single frame files become one image of at most 1920 x 1080. Writes a 384 x 216 thumbnail and the manifest, reports progress and stops when cancelled.
- Notes: Playing JPEG frames needs no video decoder at runtime; JPEG decoding through STB is about ten times faster than decoding H.264 in Java, so playback stays cheap.

#### MediaFrames
- Path: `src/main/java/com/aryston/arkea/background/MediaFrames.java`
- Role: A source of decoded frames with their time, for the importer.

#### VideoConverter
- Path: `src/main/java/com/aryston/arkea/background/VideoConverter.java`
- Role: Converts an MP4 or MOV into the JPEG frame sequence in parallel. Splits the video at its key frames into `VideoSegment`s, decodes them on a pool of low priority threads (one less than the cores, fewer when the free memory cannot hold that many decoders) and hands the frames to `FrameWriter`. Stops every thread on cancel or the first error and reports that error.
- Depends on: `VideoSegment`, `VideoPlan`, `FrameWriter`, `WorkerThreads`.
- Notes: JCodec decodes about 8 frames per second of 4K H.264 on one core; the parallel segments and the skipped frames make a 17 second 4K 60 fps clip take about 20 seconds instead of over three minutes.

#### VideoSegment
- Path: `src/main/java/com/aryston/arkea/background/VideoSegment.java`
- Role: One key frame to key frame part of a video, decoded with its own file channel and JCodec decoder. Reads the presentation times of its packets first, maps them to output frames with `FrameSlots`, then decodes in decode order, skips non-reference frames that no output frame shows without decoding them, and scales only shown frames with `PictureScaler`.

#### VideoPlan
- Path: `src/main/java/com/aryston/arkea/background/VideoPlan.java`
- Role: Time origin, output frame rate, frame limit and codec of a video, shared by its segments.

#### FrameSlots
- Path: `src/main/java/com/aryston/arkea/background/FrameSlots.java`
- Role: Maps the presentation times of source frames to the output frames each one fills at the output frame rate (a 60 fps frame between two 30 fps outputs fills none, a slow frame fills several).

#### NalUnits
- Path: `src/main/java/com/aryston/arkea/background/NalUnits.java`
- Role: Reads the first slice header of an Annex B H.264 packet and tells whether no other frame references it, so it can be skipped.

#### PictureScaler
- Path: `src/main/java/com/aryston/arkea/background/PictureScaler.java`
- Role: Turns a decoded JCodec picture into an RGB image of at most the given size: area averages the YUV 4:2:0 planes inside the crop and converts only the small result (BT.709 for HD, BT.601 otherwise, full range for JPEG YUV). Other pixel formats go through JCodec's color transform and `BackgroundImporter.fit`.

#### FrameWriter
- Path: `src/main/java/com/aryston/arkea/background/FrameWriter.java`
- Role: Encodes scaled frames to JPEG on its own threads and writes them to every output frame they fill, with a bounded queue so decoders wait instead of filling the memory. Writes the thumbnail from output frame 0 and keeps the first error. `close` waits for running encoders, so a cancelled import can delete its folder.

#### WorkerThreads
- Path: `src/main/java/com/aryston/arkea/background/WorkerThreads.java`
- Role: Thread factory for numbered, low priority daemon threads of the importer.

#### GifFrames
- Path: `src/main/java/com/aryston/arkea/background/GifFrames.java`
- Role: Reads animated GIFs with ImageIO and composes every frame on the logical screen with its offset, delay and disposal method.

#### ImportJob
- Path: `src/main/java/com/aryston/arkea/background/ImportJob.java`
- Role: State and progress of one running import, shown as a tile; can be cancelled.

#### JpegImages
- Path: `src/main/java/com/aryston/arkea/background/JpegImages.java`
- Role: Decodes a JPEG file into a `NativeImage` with STB (`NativeImage.read` only accepts PNG).

#### BackgroundPlayer
- Path: `src/main/java/com/aryston/arkea/background/BackgroundPlayer.java`
- Role: The texture of the selected background. An image is loaded once; a video decodes the next frames on a daemon thread into a queue of three and uploads one frame when it is due, looping at the end.

#### MenuBackground
- Path: `src/main/java/com/aryston/arkea/background/MenuBackground.java`
- Role: Draws the selected background instead of the vanilla panorama, cover fitted and linear filtered, with a slow pan and zoom over still images when enabled. Frees the player three seconds after the last draw and remembers a background that failed to load so it falls back to the panorama.

#### BackgroundThumbnails
- Path: `src/main/java/com/aryston/arkea/background/BackgroundThumbnails.java`
- Role: Thumbnail textures of the gallery, freed when the settings page closes.

### `com.aryston.arkea.ui.theme`

#### ArkColors
- Path: `src/main/java/com/aryston/arkea/ui/theme/ArkColors.java`
- Role: Every color token of the design (`01-design-tokens.json`) as an ARGB int, plus color math.
- Members: `rgb`, `rgba`, `withAlpha`, `alpha`, `multiplyAlpha` (used for every fade), `brighten` (CSS `brightness()` on hover), `lerp`.

#### Accent
- Path: `src/main/java/com/aryston/arkea/ui/theme/Accent.java`
- Role: The four accent themes (green, emerald, gold, stone) with base, light, border, glow and tint colors.

#### Theme
- Path: `src/main/java/com/aryston/arkea/ui/theme/Theme.java`
- Role: The accent in use. The design system asks a supplier set with `setAccentSource` (Arkea sets `ArkeaConfig::accent`), so `ui` never depends on the Arkea config.

#### Hsv
- Path: `src/main/java/com/aryston/arkea/ui/theme/Hsv.java`
- Role: Hue, saturation and value of a color with conversion from and to RGB, for the color picker.

### `com.aryston.arkea.ui.anim`

#### Easing
- Path: `src/main/java/com/aryston/arkea/ui/anim/Easing.java`
- Role: Easing function interface with the design curves: `STANDARD` (enter), `EXIT` (ease-in), `EASE` (CSS default for hover), `EASE_OUT`, `EASE_IN_OUT`, `SPRING` (switch knob), `LINEAR`.

#### CubicBezier
- Path: `src/main/java/com/aryston/arkea/ui/anim/CubicBezier.java`
- Role: CSS `cubic-bezier()` easing, solved with Newton steps and a bisection fallback.

#### Motion
- Path: `src/main/java/com/aryston/arkea/ui/anim/Motion.java`
- Role: Animation durations and delays in milliseconds from the design tokens.

#### Transition
- Path: `src/main/java/com/aryston/arkea/ui/anim/Transition.java`
- Role: A value that eases toward a target; retargeting starts from the current value. Drives hover, press and zoom blends.

#### Presence
- Path: `src/main/java/com/aryston/arkea/ui/anim/Presence.java`
- Role: Shown or hidden state with an eased enter and a shorter ease-in exit. Keeps the element alive until the exit ends (`isGone`) and reverses smoothly when toggled midway.

#### Timeline
- Path: `src/main/java/com/aryston/arkea/ui/anim/Timeline.java`
- Role: Enter progress with a delay (staggered lists) and exit progress from elapsed time.

#### Oscillation
- Path: `src/main/java/com/aryston/arkea/ui/anim/Oscillation.java`
- Role: Smooth 0 to 1 to 0 loop for pulses (splash text).

### `com.aryston.arkea.ui.layout`

#### UiScale
- Path: `src/main/java/com/aryston/arkea/ui/layout/UiScale.java`
- Role: Maps design pixels (1366 x 768 reference) to GUI units: scale in quarter steps so the design always fits, canvas size, mouse conversion, snapping of line thickness to whole physical pixels.

#### Box
- Path: `src/main/java/com/aryston/arkea/ui/layout/Box.java`
- Role: Rectangle in design pixels with edges, center, hit test, emptiness, offset, inset and intersection.

### `com.aryston.arkea.ui.render`

#### UiGraphics
- Path: `src/main/java/com/aryston/arkea/ui/render/UiGraphics.java`
- Role: The only drawing API of Arkea screens. Wraps `GuiGraphicsExtractor` with a pose and alpha stack; draws pixel snapped rectangles, horizontal and vertical gradients, borders, top highlights, soft shadows and glows, the vignette, textures, icons and text in design pixels.
- Members: `push`/`pop`, `translate`, `scaleAround`, `rotateAround`, `fade`, `fill`, `gradientHorizontal`, `gradientVertical`, `border`, `topHighlight`, `shadow`, `vignette`, `image`, `icon`, `text`, `richText` (a component with its own colors, clipped with an ellipsis), `imageCover` (a texture cropped to fill a box), `dashedBorder`, `metrics`, `canvasBox` (where a local box ends up on the canvas, used for hit tests), `clip`/`endClip` (scissor a local box), `visible` (the part of a canvas box inside the current clip), `cursor`.
- Depends on: `MeshBuilder`, `UiMeshRenderState`, `GeneratedTextures`, `TextMetrics`.
- Notes: The GUI pipelines cull back faces, so every quad goes through `MeshBuilder`, which fixes the winding. A whole icon or shadow is one render state, because vanilla puts every overlapping element on its own layer. Text is moved to the physical pixel grid before drawing, because the vanilla font is sampled nearest and a half pixel offset drops a row of every glyph.

#### UiMeshRenderState
- Path: `src/main/java/com/aryston/arkea/ui/render/UiMeshRenderState.java`
- Role: Custom `GuiElementRenderState` with any number of quads in GUI coordinates, colored or textured, with scissor and bounds.

#### MeshBuilder
- Path: `src/main/java/com/aryston/arkea/ui/render/MeshBuilder.java`
- Role: Collects quads for one render state and stores them in the winding vanilla uses.

#### QuadGeometry
- Path: `src/main/java/com/aryston/arkea/ui/render/QuadGeometry.java`
- Role: Scratch quad: four corners with position, texture coordinates and color, signed area.

#### GeneratedTextures
- Path: `src/main/java/com/aryston/arkea/ui/render/GeneratedTextures.java`
- Role: Builds the soft edge texture (nine slice shadows and glows) and the radial vignette at runtime as linear filtered `DynamicTexture`s, so no blurred images ship.

#### SoftEdgeProfile
- Path: `src/main/java/com/aryston/arkea/ui/render/SoftEdgeProfile.java`
- Role: Gaussian edge coverage (CSS box-shadow blur) and the linear radial fade of the vignette.

#### TextMetrics
- Path: `src/main/java/com/aryston/arkea/ui/render/TextMetrics.java`
- Role: Measures text in design pixels: font scale (the design size, snapped to whole physical pixels per font pixel when it is within 0.15 of one so the vanilla font stays crisp), cap height, width with letter spacing, ellipsis, word wrap.

#### TextStyle
- Path: `src/main/java/com/aryston/arkea/ui/render/TextStyle.java`
- Role: Design font size, letter spacing and an optional hard text shadow.

#### Icon
- Path: `src/main/java/com/aryston/arkea/ui/render/Icon.java`
- Role: Parsed icon: polylines, view box size and stroke or fill style.

#### IconStyle
- Path: `src/main/java/com/aryston/arkea/ui/render/IconStyle.java`
- Role: Stroke (1.5 wide lines with square caps) or fill (convex polygons, pixel glyphs).

#### Icons
- Path: `src/main/java/com/aryston/arkea/ui/render/Icons.java`
- Role: Catalogue of every line icon of the design (`IC` map of `MC-Menu.dc.html`) plus close, search, play and the pixel chevrons and check.

#### SvgPathParser
- Path: `src/main/java/com/aryston/arkea/ui/render/SvgPathParser.java`
- Role: Turns SVG path data (move, line, horizontal, vertical, cubic, quadratic, arc, close, absolute and relative) into polylines.
- Depends on: `PathTokenizer`.

#### PathTokenizer
- Path: `src/main/java/com/aryston/arkea/ui/render/PathTokenizer.java`
- Role: Reads commands, numbers in compact SVG notation and arc flags.

#### Polyline
- Path: `src/main/java/com/aryston/arkea/ui/render/Polyline.java`
- Role: Point list with a closed flag.

#### PixelSpinner
- Path: `src/main/java/com/aryston/arkea/ui/render/PixelSpinner.java`
- Role: The pixel spinner of the design: eight squares on a 24 pixel ring whose brightness runs around once every 0.8 s.

#### EmptyState
- Path: `src/main/java/com/aryston/arkea/ui/render/EmptyState.java`
- Role: The dashed 280 high panel of empty lists (no servers, LAN scanning) with an optional spinner, a title and a hint.

#### LetterTile
- Path: `src/main/java/com/aryston/arkea/ui/render/LetterTile.java`
- Role: Colored square with the first letter of a name and a darker bottom edge, used when a server or mod has no icon; the color comes from the name, so it stays the same.

#### Meter
- Path: `src/main/java/com/aryston/arkea/ui/render/Meter.java`
- Role: Small painters of the design: ten segment meter (GPU impact), cost pips (1 to 3 squares), progress bar, step dots and the plain, labeled ("OR") and dashed dividers.

#### ItemSlot
- Path: `src/main/java/com/aryston/arkea/ui/render/ItemSlot.java`
- Role: Pixel item slot of the design (40 x 40, inset bevel, white frame when selected) with a real `ItemStack` drawn through the vanilla item renderer.

### `com.aryston.arkea.ui.text`

#### SearchText
- Path: `src/main/java/com/aryston/arkea/ui/text/SearchText.java`
- Role: Normalizes text for search (lower case, accents and marks removed, ı, ß and similar letters mapped) and matches a query against several fields.

### `com.aryston.arkea.ui.widget`

#### UiHost
- Path: `src/main/java/com/aryston/arkea/ui/widget/UiHost.java`
- Role: What a widget needs from its screen: scale, text metrics, time and whether keyboard focus is shown.

#### ArkWidget
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkWidget.java`
- Role: Base of every interactive element. Implements vanilla `GuiEventListener` and `NarratableEntry`, so Tab and arrow navigation and narration work like vanilla. Tracks hover and press transitions, takes its hit box from where it was drawn (animations included), clicks on release over the widget, activates on Enter or Space when focused, draws the accent focus ring for keyboard focus, sets the pointer cursor, plays the click sound and carries an optional tooltip. Applies the press offset and the horizontal content shift of a subclass (`contentShiftX`) around both the content and the focus ring, and widens the hit box by `contentReachX` so a sliding widget stays hoverable. The hit box is the drawn box cut to the current clip, so rows scrolled out of view cannot be clicked; `localX` maps a canvas position back into the widget layout.

#### ArkButton
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkButton.java`
- Role: Standard 32 high button with label and optional leading and trailing icons; drawn by `ButtonPainter`.

#### ButtonPainter
- Path: `src/main/java/com/aryston/arkea/ui/widget/ButtonPainter.java`
- Role: Draws a button of any variant (fill, border, primary glow, hover brightness, disabled fade, label and icons, pixel icons keep their aspect) and measures its width. Shared by `ArkButton` and the loading screens, which paint vanilla buttons in the Arkea style.

#### ArkCheckbox
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkCheckbox.java`
- Role: 16 pixel checkbox with a label: accent fill and pixel check when on, accent border on hover, fills in 200 ms.

#### ArkTab
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkTab.java`
- Role: Tab of a tab row: optional icon, label, optional count badge and a 2 pixel accent underline that fades in on the selected tab.

#### ListCycleModel
- Path: `src/main/java/com/aryston/arkea/ui/widget/ListCycleModel.java`
- Role: `CycleModel` over a list of values with a getter, setter and label function (difficulties, world presets).

#### Tag
- Path: `src/main/java/com/aryston/arkea/ui/widget/Tag.java`
- Role: 12 high badge with 8 pixel spaced caps on a tone tint (HARDCORE, CHEATS, OUTDATED, CONFIG); measures and draws itself next to a name.

#### ButtonVariant
- Path: `src/main/java/com/aryston/arkea/ui/widget/ButtonVariant.java`
- Role: Fill, border, text and glow colors of primary, secondary, subtle, ghost and danger buttons.

#### ArkIconButton
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkIconButton.java`
- Role: Icon only button with hover fill and icon color blends; icons may be non-square (pixel chevrons).

#### IconButtonStyle
- Path: `src/main/java/com/aryston/arkea/ui/widget/IconButtonStyle.java`
- Role: Fill and hover fill, border, icon colors and icon size of an icon button: `BACK` and `CLOSE` of the window header, `brightening` for buttons that brighten on hover.

#### ArkMenuButton
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkMenuButton.java`
- Role: Large menu entry that slides right on hover: icon tile, label, description and chevron, or an inline icon and label.

#### MenuButtonStyle
- Path: `src/main/java/com/aryston/arkea/ui/widget/MenuButtonStyle.java`
- Role: Colors, sizes and layout of menu buttons: `primary`, `secondary`, `dangerInline`.

#### ItemContent
- Path: `src/main/java/com/aryston/arkea/ui/widget/ItemContent.java`
- Role: Icon, label and optional description of a menu button, tile or setting row.

#### MenuButtonLayout
- Path: `src/main/java/com/aryston/arkea/ui/widget/MenuButtonLayout.java`
- Role: Tile or inline content layout.

#### ArkTextLink
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkTextLink.java`
- Role: Clickable text that turns white and underlines on hover.

#### ArkSlider
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkSlider.java`
- Role: Slider: track, accent fill with glow, handle and value label as wide as the widest end label. Click or drag sets the value, arrow keys step and Shift steps ten times, the fill eases on changes that do not come from dragging. Narrates like a vanilla slider.
- Depends on: `SliderModel`.

#### SliderModel
- Path: `src/main/java/com/aryston/arkea/ui/widget/SliderModel.java`
- Role: What a slider controls: position as a fraction, drag, release, keyboard step and value labels.

#### IntSliderModel
- Path: `src/main/java/com/aryston/arkea/ui/widget/IntSliderModel.java`
- Role: `SliderModel` for an integer range with a getter, setter and label (field of view on the overview).

#### SliderRange
- Path: `src/main/java/com/aryston/arkea/ui/widget/SliderRange.java`
- Role: Minimum, maximum and step of a slider with fraction and snapping math.

#### ArkSwitch
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkSwitch.java`
- Role: On/off switch with its label: 32 x 18 track that fades to the accent in 180 ms and a square knob that springs across in 200 ms. Click, Enter or Space toggles.

#### ArkCycle
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkCycle.java`
- Role: 160 x 30 cycle selector: previous and next arrows with their own hover, the value sliding in from the side of the pressed arrow, and a progress dot per value (a bar above 16 values). Left and right keys step, Enter goes forward and Shift+Enter back; it wraps around.
- Depends on: `CycleModel`.

#### CycleModel
- Path: `src/main/java/com/aryston/arkea/ui/widget/CycleModel.java`
- Role: Value count, current index, selection and labels of a cycle selector.

#### ArkDropdown
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkDropdown.java`
- Role: 160 x 30 dropdown: value and chevron, accent border while open; opens a `DropdownMenu` on the screen's popup layer.
- Depends on: `CycleModel`, `DropdownMenu`.

#### ArkTextField
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkTextField.java`
- Role: 30 high text field with search icon, placeholder, blinking caret, select all, clear button, clipboard, word jumps; Escape clears it. Tells the game that text input starts and where the caret is, which 26.3 needs before it delivers typed characters and places the IME window.
- Depends on: `TextFieldState`.

#### TextFieldState
- Path: `src/main/java/com/aryston/arkea/ui/widget/TextFieldState.java`
- Role: Text, caret and select-all of a text field, kept by the screen so typing survives a rebuild.

#### ArkSegmented
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkSegmented.java`
- Role: Segmented control of the design (selected segment in the accent with glow); click, left and right keys choose a segment.

#### ArkKeycap
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkKeycap.java`
- Role: 140 x 30 key binding button: the bound key, a pulsing "Press a key..." state while listening, red conflict and faint unbound states; narrates like the vanilla key binding buttons.

#### ArkChoiceTile
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkChoiceTile.java`
- Role: 48 high selectable tile with name, second line and a check when selected; optional double click action.

#### ArkBanner
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkBanner.java`
- Role: Clickable accent banner with a preview image, logo, title, subtitle and an action button with an external arrow (the "Helion is installed" banner of the video page).

#### ArkSwatch
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkSwatch.java`
- Role: Row of color swatches with a white outline and a check on the chosen one; left and right arrows change the choice.

#### ArkNavItem
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkNavItem.java`
- Role: Sidebar entry: icon and label, accent fill with marker and glow when selected, hover fill, external link arrow.

#### NavEntry
- Path: `src/main/java/com/aryston/arkea/ui/widget/NavEntry.java`
- Role: Id, icon, optional logo texture (drawn instead of the icon), label and external flag of a sidebar entry.

#### ArkTile
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkTile.java`
- Role: 72 high link tile with accent icon box, name, description and chevron.

#### PanelRow
- Path: `src/main/java/com/aryston/arkea/ui/widget/PanelRow.java`
- Role: A row of the settings grid: height, placement, its widgets, drawing, hover tooltip and hiding when scrolled out of view.

#### WidgetRow
- Path: `src/main/java/com/aryston/arkea/ui/widget/WidgetRow.java`
- Role: A grid row that is one widget filling the cell (language tiles).

#### ToolbarRow
- Path: `src/main/java/com/aryston/arkea/ui/widget/ToolbarRow.java`
- Role: A frameless row of widgets aligned left or right (key bind filters and find button).

#### NoticeRow
- Path: `src/main/java/com/aryston/arkea/ui/widget/NoticeRow.java`
- Role: Tinted notice with a tone icon, text and an optional action button (key conflicts, empty search results).

#### SettingRow
- Path: `src/main/java/com/aryston/arkea/ui/widget/SettingRow.java`
- Role: Frame of a setting: icon box, name, description and hover; not focusable. `tags` and `cost` draw badges and cost pips after the name, `resettable` adds an undo button that shows while the value differs from its default, `accessory` adds other icon buttons (preview). `controlSlot` places the control widget on its right and keeps the text clear of it, `litWhen` turns the icon from gray to the accent while the setting is on, `tooltip` supplies the hover text of the whole row, and the content fades when the control is disabled. As a `PanelRow` it places and draws the control given to `control`.

#### ArkRadio
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkRadio.java`
- Role: Radio button with the square dot of the design that pops in (280 ms spring); selecting it runs the group callback.

#### ArkStepper
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkStepper.java`
- Role: Number stepper (140 x 30): minus, value, plus; arrow keys step (Shift for ten), holding a button repeats after 400 ms every 60 ms. Uses a `SliderModel`.

#### ArkChip
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkChip.java`
- Role: Chip of a multi-select (28 high): accent tint and pixel check when selected; `add` makes the dashed "+ Add" chip.

#### ChipsRow
- Path: `src/main/java/com/aryston/arkea/ui/widget/ChipsRow.java`
- Role: Setting row with a name, description and wrapping chips below; its height follows the width (`PanelRow.height(width)`).

#### ArkRangeSlider
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkRangeSlider.java`
- Role: Slider with two handles for a range; the handle closest to the click moves, Enter switches the handle for the arrow keys.

#### RangeModel, IntRangeModel
- Path: `src/main/java/com/aryston/arkea/ui/widget/RangeModel.java`
- Role: Values of a range slider; `IntRangeModel` keeps two integers in a `SliderRange` and the low value below the high one. Path of the second file: `src/main/java/com/aryston/arkea/ui/widget/IntRangeModel.java`.

#### DoubleSliderModel
- Path: `src/main/java/com/aryston/arkea/ui/widget/DoubleSliderModel.java`
- Role: `SliderModel` over a decimal value with min, max and step; ticks for five or fewer steps.

#### ArkColorButton
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkColorButton.java`
- Role: Row control of a color (140 x 30, swatch and hex) that opens the `ColorPickerPopup`.

#### ArkDisclosure
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkDisclosure.java`
- Role: Header of a collapsible section (40 high): rotating chevron, label and a count or tag on the right.

#### ArkPagination
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkPagination.java`
- Role: Previous and next arrows around "page / pages"; arrow keys work while focused.

#### ArkPresetTile
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkPresetTile.java`
- Role: Preset tile (84 high): icon box, title and description, lifts 2 pixels on hover, accent frame, glow and check badge when selected.

#### ArkCompareView
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkCompareView.java`
- Role: Before and after images with a draggable split handle and Before, Split and After modes (the split animates 450 ms); used by preview dialogs.

### `com.aryston.arkea.ui.overlay`

#### ArkDialog
- Path: `src/main/java/com/aryston/arkea/ui/overlay/ArkDialog.java`
- Role: Modal popup: scrim, tone icon, title, wrapped body, optional custom content and right aligned buttons, with the pop enter and exit animations. Can refuse to close on a scrim click for destructive actions. A `hero` image fills the top 110 pixels and fades into the dialog (delete world), a `thumbnail` replaces the tone icon (edit world).
- Members: `WIDTH_SMALL`, `WIDTH_MEDIUM`, `WIDTH_LARGE`, `button`, `keepOpenOnScrimClick`, `hero`, `thumbnail`, `content`, `open`, `close`, `cancel`, `layout`, `render`, `widgets` (content widgets and buttons, for focus and input).

#### DialogBody
- Path: `src/main/java/com/aryston/arkea/ui/overlay/DialogBody.java`
- Role: Custom content of a dialog: its height for a width, layout, drawing and widgets.

#### DialogForm
- Path: `src/main/java/com/aryston/arkea/ui/overlay/DialogForm.java`
- Role: `DialogBody` built from rows: caps field labels, full width fields, equal columns of buttons, a label with a control on the right, and a message line that appears when its supplier returns text (validation errors).

#### DialogImage
- Path: `src/main/java/com/aryston/arkea/ui/overlay/DialogImage.java`
- Role: Something that draws itself into a box, for dialog hero images and thumbnails.

#### GameToast, GameToastIcon
- Path: `src/main/java/com/aryston/arkea/ui/overlay/GameToast.java`, `src/main/java/com/aryston/arkea/ui/overlay/GameToastIcon.java`
- Role: In-game toast of the design (300 x 56 or larger, icon tile 40, yellow title, optional progress) drawn inside a vanilla toast slot; icons for items and tones.

#### ArkToasts
- Path: `src/main/java/com/aryston/arkea/ui/overlay/ArkToasts.java`
- Role: The menu toasts of the design: a stack at the bottom center of every Arkea screen, 40 high with a 38 wide tone column, at most three, newest at the bottom, enter with a 16 pixel rise and scale 0.95 (320 ms), leave with 12 pixels (200 ms), stay 2.4 s; errors, progress and busy toasts stay until finished. Optional action chip (Undo, Show), percentage with a 2 pixel bar, or a moving bar while busy. A click dismisses a toast and runs its action. Toasts survive screen switches.
- Members: `show`, `progress`, `busy`, `click`, `contains`, `render`.

#### MenuToast
- Path: `src/main/java/com/aryston/arkea/ui/overlay/MenuToast.java`
- Role: One toast: tone, message, optional action, progress or busy state, its presence and stack lift animations; `finish` turns a running toast into a result.

#### ToastTone
- Path: `src/main/java/com/aryston/arkea/ui/overlay/ToastTone.java`
- Role: Success (accent light), info, warning and error, each with its icon and color.

#### DialogContent
- Path: `src/main/java/com/aryston/arkea/ui/overlay/DialogContent.java`
- Role: Title, body, icon and tone color of a dialog.

#### ArkTooltip
- Path: `src/main/java/com/aryston/arkea/ui/overlay/ArkTooltip.java`
- Role: Tooltip of the hovered widget or row after the design delay, wrapped to 280 pixels, kept on screen, with fade in and out.

#### Popup
- Path: `src/main/java/com/aryston/arkea/ui/overlay/Popup.java`
- Role: Something drawn above the screen content that takes clicks, drags, wheel, keys and typed characters first while open (dropdown menus, context menus, the color picker).

#### DropdownMenu
- Path: `src/main/java/com/aryston/arkea/ui/overlay/DropdownMenu.java`
- Role: Menu of a dropdown: opens below the control (above when there is no room) with a 180 ms grow, rows of 28 with hover, check on the selected value, up to eight rows with wheel scrolling, arrow keys, Enter and Escape.

#### TooltipHint
- Path: `src/main/java/com/aryston/arkea/ui/overlay/TooltipHint.java`
- Role: Owner and text of a tooltip; a new owner restarts the delay.

#### Overlay
- Path: `src/main/java/com/aryston/arkea/ui/overlay/Overlay.java`
- Role: Modal layer of an `ArkScreen` (dialogs and side sheets): layout, open, close, cancel, widgets and drawing.

#### ArkSideSheet
- Path: `src/main/java/com/aryston/arkea/ui/overlay/ArkSideSheet.java`
- Role: Side sheet of the design: 420 wide panel on the right with title, subtitle, close button, optional body and footer buttons; slides 24 pixels with a fade over a scrim.

#### ArkContextMenu, ContextMenuItem
- Path: `src/main/java/com/aryston/arkea/ui/overlay/ArkContextMenu.java`
- Role: Context menu at the cursor (260 wide, rows 32, separators, right aligned shortcuts, disabled and danger items) with a 140 ms drop-in and 120 ms fade; arrow keys and Enter work. Path of the item record: `src/main/java/com/aryston/arkea/ui/overlay/ContextMenuItem.java`.

#### ColorPickerPopup
- Path: `src/main/java/com/aryston/arkea/ui/overlay/ColorPickerPopup.java`
- Role: Color picker of the design: saturation and value field, hue bar, swatch with editable hex and six Minecraft friendly presets; drag and type input come through `Popup`.

#### DialogList
- Path: `src/main/java/com/aryston/arkea/ui/overlay/DialogList.java`
- Role: Dialog body with rows of an icon, title, detail and tag: what's new, change reviews and profile lists.

#### DialogProgress
- Path: `src/main/java/com/aryston/arkea/ui/overlay/DialogProgress.java`
- Role: Dialog body with a step label, percentage and an eased progress bar.

### `com.aryston.arkea.api.config`

Public library API. Other mods build Arkea screens with these classes; see `docs/API.md`.

#### ArkeaConfigScreen
- Path: `src/main/java/com/aryston/arkea/api/config/ArkeaConfigScreen.java`
- Role: Entry point of the config screen API: a builder with brand, sidebar groups, pages, apply mode, apply listeners and a sidebar meter; `build` returns the screen, `factory` an `IConfigScreenFactory`.
- Members: `builder`, `page`, `Builder.brand`, `group`, `page`, `applyMode`, `onApply`, `meter`, `build`, `factory`, `Definition`, `Group`.

#### ConfigPage
- Path: `src/main/java/com/aryston/arkea/api/config/ConfigPage.java`
- Role: A sidebar page: sections, presets and an optional notice.

#### ConfigSection
- Path: `src/main/java/com/aryston/arkea/api/config/ConfigSection.java`
- Role: A titled group of options, half or full width, optionally collapsible.

#### ConfigOption
- Path: `src/main/java/com/aryston/arkea/api/config/ConfigOption.java`
- Role: One setting: id, `Binding`, kind and how it looks (name, description, icon, tooltip, tags, cost, restart, requirement, before and after preview, choice style, value labels).
- Members: `toggle`, `choice`, `enumChoice`, `slider` (int and double), `stepper`, `text`, `color`, `multi`, `action`.

#### OptionKind
- Path: `src/main/java/com/aryston/arkea/api/config/OptionKind.java`
- Role: Sealed kinds of option values: toggle, choice, int range, double range, text, color, multi-select and action.

#### ChoiceStyle
- Path: `src/main/java/com/aryston/arkea/api/config/ChoiceStyle.java`
- Role: How a choice is shown: automatic (cycle up to six values, dropdown above), cycle, dropdown or segmented.

#### Binding
- Path: `src/main/java/com/aryston/arkea/api/config/Binding.java`
- Role: Where an option reads and writes its value; `of(ConfigValue)` binds a `ModConfigSpec` value (saved and restart aware), `of(getter, setter, default)` anything else.

#### SpecBinding
- Path: `src/main/java/com/aryston/arkea/api/config/SpecBinding.java`
- Role: `Binding` of a `ModConfigSpec.ConfigValue`: raw value, default, `save` and the restart type of the spec.

#### ConfigPreset
- Path: `src/main/java/com/aryston/arkea/api/config/ConfigPreset.java`
- Role: A named set of option values shown as a preset tile; active while every value matches.

#### ConfigMeter
- Path: `src/main/java/com/aryston/arkea/api/config/ConfigMeter.java`
- Role: Sidebar meter (label, value, fraction and note) computed from the values shown on screen, such as an estimated GPU impact.

#### ConfigValues
- Path: `src/main/java/com/aryston/arkea/api/config/ConfigValues.java`
- Role: Read access to the values on screen, including changes that are not applied yet.

#### ApplyMode
- Path: `src/main/java/com/aryston/arkea/api/config/ApplyMode.java`
- Role: `ON_APPLY` keeps changes until Apply (default), `IMMEDIATE` writes and saves every change at once.

#### SpecOptions
- Path: `src/main/java/com/aryston/arkea/api/config/SpecOptions.java`
- Role: Turns a `ModConfigSpec.ConfigValue` into an option: switch, choice (`TranslatableEnum` names), slider or stepper by range, decimal slider, text for strings, longs, unbounded decimals and lists; names and descriptions from the translation key or comment.

#### ModConfigScreens
- Path: `src/main/java/com/aryston/arkea/api/config/ModConfigScreens.java`
- Role: Builds an Arkea screen for every `ModConfigSpec` of a mod: one sidebar group per config type, a page per top level section, sections per nested group. Unloaded configs and server configs that cannot be edited show a notice.
- Members: `forMod`, `factory`, `pages`, `hasEditableConfig`.

### `com.aryston.arkea.ui.screen`

#### ArkScreen
- Path: `src/main/java/com/aryston/arkea/ui/screen/ArkScreen.java`
- Role: Base of every Arkea screen. Computes `UiScale` on every init, scales the pose to design pixels, converts the mouse, renders the tooltip, runs one dialog on its own stratum with the menu blur, routes input and focus to the dialog while it is open, closes it with Escape, plays the exit animation before `navigate` or `leave` switches screens, and narrates dialogs.
- Depends on: `UiScale`, `UiGraphics`, `ArkWidget`, `ArkDialog`, `ArkTooltip`.
- Notes: a subclass that blurs its background reports it with `blursBackground`, so the dialog does not request the single blur of the frame again. `openPopup` shows a popup above the content; `rebuild` lays the screen out again and gives focus back to the widget with the same `key`. `hoveredHint` decides which tooltip shows; the frame that switches screens still draws this screen, so no empty frame appears between two screens.

#### ArkWindowScreen
- Path: `src/main/java/com/aryston/arkea/ui/screen/ArkWindowScreen.java`
- Role: Base of settings windows: the 1238 x 704 window centered on the canvas with sidebar (brand, grouped navigation), header (back, breadcrumb, title, close), content area and footer (note, buttons). Draws the dim layer over the blurred panorama or world, plays the window enter and exit, the title slide and staggered rows (`renderRow`). Back and Escape return to the previous screen, close returns to the game or the title screen. The content is clipped to the area between header and footer and scrolls with the wheel or the scrollbar; keyboard focus scrolls the focused row into view (`revealBox`). `switchTo` moves to another window screen by fading only the content (160 ms) while the window stays. A screen with `searchHint` gets a search field in the header (`/` focuses it) whose text survives rebuilds; `afterLayout` and `scrollToReveal` let a page scroll to a row once it is laid out.
- Depends on: `ArkScreen`, `ScrollArea`, `ArkNavItem`, `ArkIconButton`, `ArkButton`, `NavGroup`, `SidebarBrand`.

#### ScrollArea
- Path: `src/main/java/com/aryston/arkea/ui/screen/ScrollArea.java`
- Role: Scroll offset of a viewport with eased wheel scrolling, page jumps on the track, a draggable 4 pixel thumb and `reveal` for focus.

#### SettingsPanel
- Path: `src/main/java/com/aryston/arkea/ui/screen/SettingsPanel.java`
- Role: The settings grid of the design: half sections side by side, full sections across with two (or any number of) rows per line, section labels with optional right hints, rows of any `PanelRow` type, staggered rows, row tooltips, the row of a control for scrolling. Rows outside the visible area are skipped, so long lists stay fast.

#### SettingsSection
- Path: `src/main/java/com/aryston/arkea/ui/screen/SettingsSection.java`
- Role: Optional title and hint, width, columns and rows of one section.

#### NavGroup
- Path: `src/main/java/com/aryston/arkea/ui/screen/NavGroup.java`
- Role: Label and entries of a sidebar group.

#### SidebarBrand
- Path: `src/main/java/com/aryston/arkea/ui/screen/SidebarBrand.java`
- Role: Icon, title and subtitle at the top of the sidebar.

### `com.aryston.arkea.screen.config`

#### ArkConfigScreen
- Path: `src/main/java/com/aryston/arkea/screen/config/ArkConfigScreen.java`
- Role: The window built from an `ArkeaConfigScreen.Definition`: sidebar pages with an unsaved marker, search across every page, preset tiles, collapsible sections, rows with reset and preview buttons, restart and page notices, right-click menu (reset, copy, paste, preview), sidebar meter, Reset to Defaults, Discard and Apply, and the unsaved changes dialog on leaving. Each page is its own screen sharing one `ConfigSession`, so pages switch with the content animation.

#### ConfigSession
- Path: `src/main/java/com/aryston/arkea/screen/config/ConfigSession.java`
- Role: State shared by the pages of one config window: current page, values not applied yet, expanded sections and text field states; applies, discards, resets and applies presets.

#### ConfigControls
- Path: `src/main/java/com/aryston/arkea/screen/config/ConfigControls.java`
- Role: Picks the widget of an option: switch, cycle, dropdown or segmented, slider or stepper, text field, color button or action button.

#### ConfigClipboard
- Path: `src/main/java/com/aryston/arkea/screen/config/ConfigClipboard.java`
- Role: Copies an option value as text and parses pasted text back for every kind.

### `com.aryston.arkea.screen.title`

#### ArkTitleScreen
- Path: `src/main/java/com/aryston/arkea/screen/title/ArkTitleScreen.java`
- Role: The title screen of `MC-Menu.dc.html`: vanilla panorama with the design shading and vignette, MINECRAFT lockup, splash, five menu buttons (singleplayer, multiplayer, Realms, mods, options), quit, language, accessibility and friends buttons, jump back in card, version and credits footer. Opens the same vanilla screens as the vanilla title screen and asks before quitting.
- Depends on: `ArkScreen`, the widgets, `ArkDialog`, `SplashText`, `JumpBackInCard`, `RecentWorld`, `LastPlayed`, `MultiplayerAccess`, `SplashRendererAccessor`.

#### JumpBackInCard
- Path: `src/main/java/com/aryston/arkea/screen/title/JumpBackInCard.java`
- Role: Card of the most recently played world: icon cropped to cover, zoom on hover, game mode badge, name, last played time and play button.

#### SplashText
- Path: `src/main/java/com/aryston/arkea/screen/title/SplashText.java`
- Role: Rotated, pulsing yellow splash; a click shows another splash. Hit testing follows the rotation.

#### RecentWorld
- Path: `src/main/java/com/aryston/arkea/screen/title/RecentWorld.java`
- Role: Loads the playable world with the latest last played time and its icon bytes in the background.

#### LastPlayed
- Path: `src/main/java/com/aryston/arkea/screen/title/LastPlayed.java`
- Role: Turns a last played time into today, yesterday or an earlier localized date.

#### MultiplayerAccess
- Path: `src/main/java/com/aryston/arkea/screen/title/MultiplayerAccess.java`
- Role: The reason multiplayer and Realms are disabled (name ban, account ban, disabled), like the vanilla title screen.

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

### `com.aryston.arkea.screen.servers`

#### ArkMultiplayerScreen
- Path: `src/main/java/com/aryston/arkea/screen/servers/ArkMultiplayerScreen.java`
- Role: The multiplayer window of the design (replaces `JoinMultiplayerScreen`): Servers and LAN Games tabs with counts, Refresh, search, server rows with move up and down, copy address, edit and delete on the selected row, LAN rows from the vanilla LAN detector, empty and scanning states, and Direct Connect, Add Server and Join Server in the footer. Shift and the arrow keys reorder, F5 refreshes. The pinger and icons are created every time the screen is shown and released when it closes.
- Depends on: `ServerPings`, `ServerIcons`, `ServerDialogs`, `ServerRow`, `LanRow`, `JoinTarget`.

#### ServerRow
- Path: `src/main/java/com/aryston/arkea/screen/servers/ServerRow.java`
- Role: 64 high server row: icon or letter tile, name with an outdated tag, colored MOTD (or the server version, or the error in red), player count and four ping bars with milliseconds (pulsing while pinging, red when offline). Its hit box leaves out the action buttons on top of it.

#### LanRow
- Path: `src/main/java/com/aryston/arkea/screen/servers/LanRow.java`
- Role: Row of a LAN world with its MOTD and address (hidden when the option hides server addresses).

#### ServerPings
- Path: `src/main/java/com/aryston/arkea/screen/servers/ServerPings.java`
- Role: Pings servers on five daemon threads with the vanilla `ServerStatusPinger` and marks servers that cannot be resolved or reached as unreachable, which the vanilla pinger leaves in the pinging state for unresolvable hosts.

#### ServerIcons
- Path: `src/main/java/com/aryston/arkea/screen/servers/ServerIcons.java`
- Role: Favicon textures of servers, uploaded again whenever the icon bytes change.

#### ServerDialogs
- Path: `src/main/java/com/aryston/arkea/screen/servers/ServerDialogs.java`
- Role: Add and edit server (name, address with validation, resource pack mode), direct connect (remembers the last address) and remove server dialogs.

### `com.aryston.arkea.screen.loading`

#### LoadingSkin
- Path: `src/main/java/com/aryston/arkea/screen/loading/LoadingSkin.java`
- Role: Draws the connecting and loading design over vanilla loading screens without replacing them: background, spinner or error tile, kicker, title, detail, step label with percentage and an 8 pixel progress bar, step list, buttons and a tip card. The vanilla buttons keep handling input; every frame they are moved to where the design draws them and painted with `ButtonPainter`.
- Notes: The vanilla screens cannot be replaced: game code checks `instanceof LevelLoadingScreen` (HUD, portals, music, packet handling) and `ConnectScreen` runs the connection itself.

#### LoadingViews
- Path: `src/main/java/com/aryston/arkea/screen/loading/LoadingViews.java`
- Role: Builds the view of a supported vanilla screen: `ConnectScreen` (steps from the connection status), `LevelLoadingScreen` (world, server terrain or portal travel), `ProgressScreen`, `GenericMessageScreen` and `DisconnectedScreen` (error with the reason).

#### LoadingView
- Path: `src/main/java/com/aryston/arkea/screen/loading/LoadingView.java`
- Role: Mood, texts, progress and steps of one loading frame.

#### Reconnect
- Path: `src/main/java/com/aryston/arkea/screen/loading/Reconnect.java`
- Role: Reconnect button added to the disconnected screen after a server connection.

#### JoinTarget
- Path: `src/main/java/com/aryston/arkea/screen/loading/JoinTarget.java`
- Role: Name of the world or server the player last chose, shown as the loading title.

### `com.aryston.arkea.screen.mods`

#### ArkModsScreen
- Path: `src/main/java/com/aryston/arkea/screen/mods/ArkModsScreen.java`
- Role: The mods window of the design (replaces the NeoForge `ModListScreen`): 340 wide mod list with search, details panel, Open Settings, Homepage and Report Issue (through the vanilla link confirmation), Open Mods Folder.
- Depends on: `ModEntry`, `ModIcons`, `ModRow`, `ModDetails`.

#### ModEntry
- Path: `src/main/java/com/aryston/arkea/screen/mods/ModEntry.java`
- Role: A mod container with its NeoForge `ModDisplayInfo` (custom or default), icon resource (the vanilla pack icon for Minecraft), config screen factory and update check result.

#### ModRow
- Path: `src/main/java/com/aryston/arkea/screen/mods/ModRow.java`
- Role: 56 high mod row with icon or letter tile, name, version and UPDATE and CONFIG tags.

#### ModDetails
- Path: `src/main/java/com/aryston/arkea/screen/mods/ModDetails.java`
- Role: Details panel: 72 pixel icon, name, version and loader chips, authors, mod id, license, credits, latest version, description and the buttons, or "No config screen".

#### ModIcons
- Path: `src/main/java/com/aryston/arkea/screen/mods/ModIcons.java`
- Role: Loads mod icons from their pack resources into dynamic textures, freed when the screen closes.

### `com.aryston.arkea.screen.options`

#### ArkOptionsScreen
- Path: `src/main/java/com/aryston/arkea/screen/options/ArkOptionsScreen.java`
- Role: The options overview of `MC-Menu.dc.html`: quick settings (field of view slider, online settings), a tile for every options page, more (telemetry, credits) and the "changes save automatically" footer. Saves the options when it closes like vanilla.
- Depends on: `ArkWindowScreen`, `OptionsPage`, `SettingRow`, `ArkSlider`, `ArkTile`.
- Notes: Minecraft 26.3 moved difficulty out of the options screen, so the quick settings show the online settings instead of the difficulty of the design.

#### OptionsPage
- Path: `src/main/java/com/aryston/arkea/screen/options/OptionsPage.java`
- Role: Every options page with its sidebar group, icon, title (vanilla text without the trailing dots), label, description and the Arkea window page it opens. Helion appears only when it registers a config screen and opens its own screen. Arkea has its own page in the Content group. Arkea and Helion show their logos (`ARKEA_LOGO`, `HELION_LOGO`) in the sidebar, as in the design.

#### OptionsNavigation
- Path: `src/main/java/com/aryston/arkea/screen/options/OptionsNavigation.java`
- Role: Sidebar brand and groups shared by the overview and every page.

#### OptionsPageScreen
- Path: `src/main/java/com/aryston/arkea/screen/options/OptionsPageScreen.java`
- Role: Base of the options pages: breadcrumb, shared sidebar, optional banner, `SettingsPanel` content, `option` (a row with the right control for a vanilla option), `toggle` and `link` rows, "Reset to Defaults" with a confirmation dialog, Done, row tooltips, saving on close. Sidebar moves between pages swap only the content; overview always means the screen the pages were opened from.
- Depends on: `ArkWindowScreen`, `SettingsPanel`, `OptionControls`, `OptionText`.

#### ArkVideoScreen
- Path: `src/main/java/com/aryston/arkea/screen/options/ArkVideoScreen.java`
- Role: Video settings in display, world, quality and preference sections, with the vanilla side effects: fullscreen resolution cycle per monitor, unsupported graphics warning, anisotropy only with anisotropic filtering, texture reload after mipmap or filtering changes, applying the fullscreen mode on close, Ctrl+wheel GUI scale, restart note for the graphics API. Shows the Helion banner when Helion is installed.

#### ArkSoundScreen
- Path: `src/main/java/com/aryston/arkea/screen/options/ArkSoundScreen.java`
- Role: Master volume and device, a slider per sound category, subtitles, directional audio, music frequency and music toast.

#### ArkChatScreen
- Path: `src/main/java/com/aryston/arkea/screen/options/ArkChatScreen.java`
- Role: Chat behaviour and chat appearance options.

#### ArkAccessibilityScreen
- Path: `src/main/java/com/aryston/arkea/screen/options/ArkAccessibilityScreen.java`
- Role: Narration and text, motion and effects, links to the controls page and the accessibility guide. Disables high contrast without its pack and minecart rotation without the feature flag, like vanilla.

#### ArkLanguageScreen
- Path: `src/main/java/com/aryston/arkea/screen/options/ArkLanguageScreen.java`
- Role: Every game language as three columns of tiles with search (accents ignored, so "turk" finds Türkçe), scrolled to the current language, and the font options. The chosen language applies when the page is left, like vanilla; a double click applies it right away.

#### ArkSkinScreen
- Path: `src/main/java/com/aryston/arkea/screen/options/ArkSkinScreen.java`
- Role: A switch per player model part and the main hand.

#### ArkControlsScreen
- Path: `src/main/java/com/aryston/arkea/screen/options/ArkControlsScreen.java`
- Role: Mouse and movement options of the vanilla controls and mouse screens in one page, plus the key binds link.

### `com.aryston.arkea.screen.options.keys`

#### ArkKeyBindsScreen
- Path: `src/main/java/com/aryston/arkea/screen/options/keys/ArkKeyBindsScreen.java`
- Role: Key binds page: search by action, key or category, filters (all, changed, conflicts, unbound) with counts, "Find by key" (press a key or mouse button to list what uses it), a conflict notice with a shortcut to the conflicts, categories as sections. A keycap listens for the next key, combination with Ctrl, Shift or Alt (NeoForge modifiers) or mouse button; Escape cancels, Backspace or Delete unbinds. Reset per key (tooltip shows the default) and Reset Keys with confirmation.

#### KeyBindRow
- Path: `src/main/java/com/aryston/arkea/screen/options/keys/KeyBindRow.java`
- Role: 44 high row with action name, conflict badge, keycap and reset button.

#### KeyConflicts
- Path: `src/main/java/com/aryston/arkea/screen/options/keys/KeyConflicts.java`
- Role: Which bindings clash, using the NeoForge rules (`same`, modifier conflicts), except debug keys (F3 combinations) only against other debug keys and only when one was changed, and spectator keys only against spectator keys, so the vanilla defaults show no false conflicts.

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

### `com.aryston.arkea.screen.options.arkea`

#### ArkArkeaScreen
- Path: `src/main/java/com/aryston/arkea/screen/options/arkea/ArkArkeaScreen.java`
- Role: The Arkea settings page: Import (native file picker) and Open Folder buttons, a three column gallery of the panorama, every imported background (delete with confirmation), running imports and an import tile, then slow pan, accent color and the title and options screen switches. Files dropped on the window are imported. Rebuilds when the library changes.
- Depends on: `OptionsPageScreen`, `BackgroundLibrary`, `FileDialogs`, `ArkeaConfig`.

#### MediaTile
- Path: `src/main/java/com/aryston/arkea/screen/options/arkea/MediaTile.java`
- Role: Base gallery tile: 16:9 thumbnail, name and second line, selected glow and border. Clicks on its corner button do not reach the tile.

#### BackgroundTile
- Path: `src/main/java/com/aryston/arkea/screen/options/arkea/BackgroundTile.java`
- Role: Tile of the panorama or an imported background with its thumbnail, a play badge for videos, a check when selected and the length, frame rate and size; selects it on click.

#### ImportTile
- Path: `src/main/java/com/aryston/arkea/screen/options/arkea/ImportTile.java`
- Role: Dashed tile that opens the file picker.

#### JobTile
- Path: `src/main/java/com/aryston/arkea/screen/options/arkea/JobTile.java`
- Role: Tile of a running import with its percentage and progress bar, or the error when it failed.

#### TileRow
- Path: `src/main/java/com/aryston/arkea/screen/options/arkea/TileRow.java`
- Role: Grid row holding a tile and its optional corner button (delete, cancel, dismiss).

### `com.aryston.arkea.screen.options.control`

#### OptionControls
- Path: `src/main/java/com/aryston/arkea/screen/options/control/OptionControls.java`
- Role: Picks the control of a vanilla `OptionInstance`: a switch for on/off booleans, a cycle selector for enums, cyclable ranges and other booleans (Hold/Toggle), a slider for sliderable values; reads the default for resets.

#### OptionControl
- Path: `src/main/java/com/aryston/arkea/screen/options/control/OptionControl.java`
- Role: Widget, size, icon state and reset action of one option.

#### OptionSliderModel
- Path: `src/main/java/com/aryston/arkea/screen/options/control/OptionSliderModel.java`
- Role: `SliderModel` over a sliderable option; options that apply late (render distance) apply on release, or 600 ms after the last key step, like vanilla.

#### OptionCycleModel
- Path: `src/main/java/com/aryston/arkea/screen/options/control/OptionCycleModel.java`
- Role: `CycleModel` over the value list of an option.

#### OptionText
- Path: `src/main/java/com/aryston/arkea/screen/options/control/OptionText.java`
- Role: Turns vanilla "Caption: value" labels into the value alone, reads the vanilla tooltip of the current value, and finds the short Arkea description (`arkea.option.*`) and name overrides (`.name`).

### `com.aryston.arkea.screen.gallery`

#### ArkGalleryScreen
- Path: `src/main/java/com/aryston/arkea/screen/gallery/ArkGalleryScreen.java`
- Role: Component gallery for mod developers, opened from the Arkea page: every input, feedback part and overlay of the design, live.

#### PaintRow
- Path: `src/main/java/com/aryston/arkea/screen/gallery/PaintRow.java`
- Role: Gallery row that runs a painter inside a row frame.

### `com.aryston.arkea.screen.vanilla`

#### VanillaTheme
- Path: `src/main/java/com/aryston/arkea/screen/vanilla/VanillaTheme.java`
- Role: Arkea look for the widgets of every screen Arkea does not replace (vanilla and other mods): buttons (Done, Yes, Proceed and Continue as primary), slider tracks and handles, text fields and text areas, checkboxes, list backgrounds, header and footer separators, scrollbars, menu tabs, the menu background and the chat input bar, with hover animations and the accent color. Vanilla keeps the labels, layout and behavior. Inactive in container screens and when the Arkea Style Everywhere switch is off.

#### VanillaSkin
- Path: `src/main/java/com/aryston/arkea/screen/vanilla/VanillaSkin.java`
- Role: Base of screens that keep their vanilla instance but are drawn by Arkea: cancels nothing itself, draws the background, opens a design pixel frame and offers `Frame` helpers that move vanilla widgets to design boxes (so they stay the input targets) and paint them.

#### VanillaSkins
- Path: `src/main/java/com/aryston/arkea/screen/vanilla/VanillaSkins.java`
- Role: The skins `ClientEvents` asks in `ScreenEvent.Render.Pre`: dialogs, pause and death.

#### DialogSkin
- Path: `src/main/java/com/aryston/arkea/screen/vanilla/DialogSkin.java`
- Role: Draws confirm, link, alert, popup, warning (multiplayer safety) and backup screens as an Arkea dialog: tone icon, wrapped title, the texts of the screen, its checkboxes and its buttons right aligned. Only screens whose children are buttons and text widgets are drawn this way.

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
- Role: Death screen of the design over the vanilla `DeathScreen` (kept for the game's `instanceof` checks): red tint and vignette, large title, cause and score fading in late, Respawn and Title Screen buttons that keep the vanilla one second delay.

#### ArkStatsScreen
- Path: `src/main/java/com/aryston/arkea/screen/game/ArkStatsScreen.java`
- Role: Statistics window of the design: world name, General, Items and Mobs tabs, two column general stats, item table with sortable columns and item icons, mob rows with kill bars; requests the stats like vanilla and shows a spinner until they arrive.

#### StatsData
- Path: `src/main/java/com/aryston/arkea/screen/game/StatsData.java`
- Role: Reads general, item and mob statistics from the `StatsCounter`.

### `com.aryston.arkea.screen.debug`

#### DebugCards
- Path: `src/main/java/com/aryston/arkea/screen/debug/DebugCards.java`
- Role: Draws the F3 debug lines as compact cards: every group of lines between empty lines becomes a card with an accent bar on the screen edge, the first line as its title. Follows the debug screen scale option. Active while the Arkea Debug Screen switch is on.

### `com.aryston.arkea.mixin`

#### SplashRendererAccessor
- Path: `src/main/java/com/aryston/arkea/mixin/SplashRendererAccessor.java`
- Role: Reads the private splash text of `SplashRenderer`.
- Notes: `SplashManager` only hands out a `SplashRenderer`, which draws itself at a fixed spot, angle and size. The design draws the splash in its own place and style, and no API exposes the text.

#### OptionInstanceAccessor
- Path: `src/main/java/com/aryston/arkea/mixin/OptionInstanceAccessor.java`
- Role: Reads the private tooltip supplier and default value of an `OptionInstance` for row tooltips and Reset to Defaults.

#### TooltipAccessor
- Path: `src/main/java/com/aryston/arkea/mixin/TooltipAccessor.java`
- Role: Reads the message of a vanilla `Tooltip`, which only exposes wrapped lines.

#### OptionsMixin
- Path: `src/main/java/com/aryston/arkea/mixin/OptionsMixin.java`
- Role: Runs `KeyModifierRepair` at the end of `Options.load`.

#### PanoramaMixin
- Path: `src/main/java/com/aryston/arkea/mixin/PanoramaMixin.java`
- Role: Draws the selected custom background instead of the vanilla panorama wherever the game draws it (title screen, menus over it), and lets the panorama draw when the panorama is selected.

#### OptionsSubScreenAccessor
- Path: `src/main/java/com/aryston/arkea/mixin/OptionsSubScreenAccessor.java`
- Role: Reads the previous screen of a vanilla options sub screen when `ClientEvents` replaces it.

#### SelectWorldScreenAccessor, JoinMultiplayerScreenAccessor, ModListScreenAccessor
- Path: `src/main/java/com/aryston/arkea/mixin/SelectWorldScreenAccessor.java`, `src/main/java/com/aryston/arkea/mixin/JoinMultiplayerScreenAccessor.java`, `src/main/java/com/aryston/arkea/mixin/ModListScreenAccessor.java`
- Role: Read the private previous screen of the singleplayer, multiplayer and NeoForge mods screens when `ClientEvents` replaces them.

#### SystemToastMixin, AdvancementToastMixin, RecipeToastMixin, TutorialToastMixin, RecipeToastEntryAccessor
- Path: `src/main/java/com/aryston/arkea/mixin/SystemToastMixin.java`, `src/main/java/com/aryston/arkea/mixin/AdvancementToastMixin.java`, `src/main/java/com/aryston/arkea/mixin/RecipeToastMixin.java`, `src/main/java/com/aryston/arkea/mixin/TutorialToastMixin.java`, `src/main/java/com/aryston/arkea/mixin/RecipeToastEntryAccessor.java`
- Role: Draw vanilla toasts in the Arkea style and size them; vanilla keeps timing and updates. No API exists to restyle toasts.

#### CreateWorldScreenInvoker
- Path: `src/main/java/com/aryston/arkea/mixin/CreateWorldScreenInvoker.java`
- Role: Calls the private `onCreate`, `openExperimentsScreen` and `openDataPackSelectionScreen` of `CreateWorldScreen`, so `ArkCreateWorldScreen` keeps the whole vanilla creation flow.

#### ConnectScreenAccessor, LevelLoadingScreenAccessor, ProgressScreenAccessor, DisconnectedScreenAccessor
- Path: `src/main/java/com/aryston/arkea/mixin/ConnectScreenAccessor.java`, `src/main/java/com/aryston/arkea/mixin/LevelLoadingScreenAccessor.java`, `src/main/java/com/aryston/arkea/mixin/ProgressScreenAccessor.java`, `src/main/java/com/aryston/arkea/mixin/DisconnectedScreenAccessor.java`
- Role: Read the status, load tracker and reason, header, stage and progress, and disconnection details that `LoadingViews` shows; none of these screens exposes them.

#### ConfigurationScreenAccessor
- Path: `src/main/java/com/aryston/arkea/mixin/ConfigurationScreenAccessor.java`
- Role: Reads the mod of a NeoForge `ConfigurationScreen` so `ClientEvents` can open the Arkea screen of the same mod. No event or getter exposes it.

#### AbstractButtonMixin, AbstractSliderButtonMixin, EditBoxMixin, CheckboxMixin, AbstractTextAreaWidgetMixin, AbstractSelectionListMixin, AbstractScrollAreaMixin, MenuTabButtonMixin, ScreenMixin, GuiGraphicsExtractorMixin
- Path: `src/main/java/com/aryston/arkea/mixin/AbstractButtonMixin.java`, `src/main/java/com/aryston/arkea/mixin/AbstractSliderButtonMixin.java`, `src/main/java/com/aryston/arkea/mixin/EditBoxMixin.java`, `src/main/java/com/aryston/arkea/mixin/CheckboxMixin.java`, `src/main/java/com/aryston/arkea/mixin/AbstractTextAreaWidgetMixin.java`, `src/main/java/com/aryston/arkea/mixin/AbstractSelectionListMixin.java`, `src/main/java/com/aryston/arkea/mixin/AbstractScrollAreaMixin.java`, `src/main/java/com/aryston/arkea/mixin/MenuTabButtonMixin.java`, `src/main/java/com/aryston/arkea/mixin/ScreenMixin.java`, `src/main/java/com/aryston/arkea/mixin/GuiGraphicsExtractorMixin.java`
- Role: Replace the sprite and texture drawing of vanilla widgets, lists, tabs, the menu background and the header and footer separators with `VanillaTheme` while it is active; labels and behavior stay vanilla.
- Notes: No NeoForge event exposes widget drawing. `GuiGraphicsExtractorMixin` sits on a frequent `blit` overload and only compares the texture with the four separator textures before returning.

#### AlertScreenAccessor, BackupConfirmScreenAccessor, DeathScreenAccessor, StatsScreenAccessor
- Path: `src/main/java/com/aryston/arkea/mixin/AlertScreenAccessor.java`
- Role: Read the private message of alerts, the description of the backup prompt, the cause and score of the death screen and the previous screen of the statistics screen. Paths of the others: `src/main/java/com/aryston/arkea/mixin/BackupConfirmScreenAccessor.java`, `src/main/java/com/aryston/arkea/mixin/DeathScreenAccessor.java`, `src/main/java/com/aryston/arkea/mixin/StatsScreenAccessor.java`.

#### ClientPacketListenerMixin
- Path: `src/main/java/com/aryston/arkea/mixin/ClientPacketListenerMixin.java`
- Role: Tells `ArkStatsScreen` when the statistics arrive; vanilla only notifies `StatsScreen`.

#### DebugScreenOverlayMixin
- Path: `src/main/java/com/aryston/arkea/mixin/DebugScreenOverlayMixin.java`
- Role: Hands the left and right F3 line lists to `DebugCards` instead of the vanilla text with gray bars. Charts and the profiler stay vanilla.

#### ChatScreenMixin
- Path: `src/main/java/com/aryston/arkea/mixin/ChatScreenMixin.java`
- Role: Replaces the black fill behind the chat input with `VanillaTheme.chatBar`. Suggestions, the command preview and chat messages stay vanilla.

### `com.aryston.arkea.screen.palette`

#### CommandPaletteScreen
- Path: `src/main/java/com/aryston/arkea/screen/palette/CommandPaletteScreen.java`
- Role: Ctrl+K command palette pushed as a screen layer over any menu: search field, grouped results (actions, pages, settings, worlds, servers), arrow keys, Enter, Esc and mouse.

#### PaletteEntry
- Path: `src/main/java/com/aryston/arkea/screen/palette/PaletteEntry.java`
- Role: Group, icon, label, detail and action of one palette result.

#### PaletteIndex
- Path: `src/main/java/com/aryston/arkea/screen/palette/PaletteIndex.java`
- Role: Builds palette entries: menu actions, folders, accent colors, options pages, every setting, recent worlds (async) and servers.

#### SettingsIndex
- Path: `src/main/java/com/aryston/arkea/screen/palette/SettingsIndex.java`
- Role: Lists every setting row of the options pages by building each page off screen once per language.

### `com.aryston.arkea.screen.toasts`

#### GameToasts
- Path: `src/main/java/com/aryston/arkea/screen/toasts/GameToasts.java`
- Role: Turns vanilla system, advancement, recipe and tutorial toasts into `GameToast` content; checks the notifications switch.

### `com.aryston.arkea.debug`

#### UiCheckGameToasts
- Path: `src/main/java/com/aryston/arkea/debug/UiCheckGameToasts.java`
- Role: Check helper that raises sample system and tutorial toasts.

#### UiCheck
- Path: `src/main/java/com/aryston/arkea/debug/UiCheck.java`
- Role: Developer check (`-Define arkea.uiCheck=all` or a comma list of groups: title, options, worlds, create, servers, mods, loading, toasts, gallery, config, helion, vanilla, game). `Step.await` waits for a condition such as a loaded world. Opens screens, presses widgets by key, sends keys, saves a screenshot after every step as `screenshots/arkea_<step>.png` and closes the game. Does nothing in production.

#### UiCheckToasts, UiCheckLoading, UiCheckServers, UiCheckConfig, UiCheckGame
- Path: `src/main/java/com/aryston/arkea/debug/UiCheckToasts.java`, `src/main/java/com/aryston/arkea/debug/UiCheckLoading.java`, `src/main/java/com/aryston/arkea/debug/UiCheckServers.java`, `src/main/java/com/aryston/arkea/debug/UiCheckConfig.java`, `src/main/java/com/aryston/arkea/debug/UiCheckGame.java`
- Role: Check helpers: sample toasts, a progress screen and a connection to an unroutable address (cancelled again), temporary sample servers that are removed at the end, the config screen of Helion when it is installed, and the statistics, death, F3 and chat screens in a fresh world.

## Tests

Plain JUnit 5 tests without a running game, run by `./gradlew build` and the CI. Each test class sits in the package of the class it checks.

| File | What it checks |
|---|---|
| `src/test/java/com/aryston/arkea/LanguageFilesTest.java` | Every language file has the same keys and every config entry has a `.tooltip` translation. |
| `src/test/java/com/aryston/arkea/ui/anim/CubicBezierTest.java` | Fixed endpoints, linear identity, the CSS `ease` reference value, the standard curve rising early and the spring overshooting. |
| `src/test/java/com/aryston/arkea/ui/anim/PresenceTest.java` | Hidden start, enter, exit kept alive until it ends, smooth reversal midway, delayed enter. |
| `src/test/java/com/aryston/arkea/ui/anim/TransitionTest.java` | Reaching the target, retargeting from the current value, snapping. |
| `src/test/java/com/aryston/arkea/ui/anim/TimelineTest.java` | Delayed enter, exit from one to zero, oscillation loop. |
| `src/test/java/com/aryston/arkea/ui/layout/UiScaleTest.java` | One to one at 1366 x 768, quarter steps, the design fits every common window, coordinate round trip, one physical pixel minimum for lines. |
| `src/test/java/com/aryston/arkea/ui/render/SvgPathParserTest.java` | Absolute and relative lines, closing, implicit commands, compact numbers, arcs on their radius, cubic end points, every catalogue icon parses, invalid data is rejected. |
| `src/test/java/com/aryston/arkea/ui/render/SoftEdgeProfileTest.java` | Shadow edge coverage and the vignette fade. |
| `src/test/java/com/aryston/arkea/ui/text/SearchTextTest.java` | Case and accent insensitive matching, Turkish and German letters. |
| `src/test/java/com/aryston/arkea/ui/widget/TextFieldStateTest.java` | Typing at the caret with a length limit, deleting characters and words, select all, word jumps. |
| `src/test/java/com/aryston/arkea/ui/widget/SliderRangeTest.java` | Slider fraction, step snapping, clamping and an empty range. |
| `src/test/java/com/aryston/arkea/screen/options/control/OptionTextTest.java` | Vanilla "Caption: value" labels reduce to the value, percent labels use the Arkea key, unknown labels stay. |
| `src/test/java/com/aryston/arkea/background/BackgroundImporterTest.java` | File type detection, frame rate rounding and the 30 fps cap, scaling without upscaling, image, GIF and video conversion (frame order with B frames from `src/test/resources/background/ramp.mp4`), single frame GIFs, cancelling, manifest round trip and broken manifests. |
| `src/test/java/com/aryston/arkea/background/FrameSlotsTest.java` | 60 fps frames between two 30 fps outputs are skipped, slow frames fill several outputs, the frame limit, unknown times. |
| `src/test/java/com/aryston/arkea/background/NalUnitsTest.java` | Non-reference slices are disposable, reference and key frames are kept, parameter sets and SEI before the slice are skipped, data without a slice is kept. |
| `src/test/java/com/aryston/arkea/background/PictureScalerTest.java` | Limited and full range colors, shrinking 4K to 720p, the crop hides coded padding, area averaging. |
| `src/test/java/com/aryston/arkea/background/DropFolderTest.java` | A dropped file settles on the second poll, a growing file waits, missing files are ignored. |
| `src/test/java/com/aryston/arkea/screen/title/LastPlayedTest.java` | Today, yesterday across midnight, and the localized date for older worlds. |

## Source Files

| File | Purpose |
|---|---|
| `src/main/templates/META-INF/neoforge.mods.toml` | Mod metadata template filled from `gradle.properties`: dependencies and mixin config. |
| `src/main/resources/arkea.mixins.json` | Mixin configuration listing the accessors. |

## Asset Folders

| Folder | Contents |
|---|---|
| `src/main/resources/assets/arkea/lang/` | `en_us.json` and `tr_tr.json`: config, title screen, window and options texts, short option descriptions (`arkea.option.*`). Menu labels reuse vanilla keys so every game language shows them. |
| `src/main/resources/assets/arkea/textures/gui/` | `gallery_before.png` and `gallery_after.png`: sample images of the before and after preview in the gallery. `arkea_logo.png` and `helion_logo.png`: 48 x 48 logos (scaled from the 128 pixel design handoff logos) for the sidebar, the "UI by Arkea" footer and the Helion banner, with `.png.mcmeta` files that turn on linear filtering. `helion_preview.png`: preview of the Helion banner. |

## Build Files

| File | Purpose |
|---|---|
| `build.gradle` | ModDevGradle setup, JCodec embedded with Jar-in-Jar for video import, JUnit 5 for `src/test/java` with the Minecraft classpath of `main`, `client` run forced to Vulkan, metadata expansion, logo packing. |
| `gradle.properties` | Single place for versions and mod metadata. |
| `settings.gradle` | Plugin repositories, Java toolchain resolver, project name. |
