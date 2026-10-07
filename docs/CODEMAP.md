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
  - `set`: sets a value and saves the file at once (the Arkea settings page changes values live).
  - `SPEC`: the built spec.

### `com.aryston.arkea.integration`

#### ClientEvents
- Path: `src/main/java/com/aryston/arkea/integration/ClientEvents.java`
- Role: Swaps a newly opened vanilla `TitleScreen` for `ArkTitleScreen` (not in demo mode), `OptionsScreen` for `ArkOptionsScreen` and the video, sound, chat, accessibility, skin, controls, mouse, key binds, language and font screens for the Arkea pages, keeping the previous screen, in `ScreenEvent.Opening`, each unless the config turns it off. Only the exact vanilla classes are replaced, so subclasses from other mods stay.
- Also ticks `MenuBackground` every client tick so a background nobody draws frees its texture and decoder thread.
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
- Role: The menu backgrounds in `<game folder>/arkea/backgrounds`: scans the entry folders, imports files on one background worker thread (into a temporary folder, then an atomic move), auto imports loose media files dropped into the folder (the originals move to `imported/`, or `failed/`), selects the new entry after an import, deletes entries and counts changes in `version` so the settings page rebuilds.
- Depends on: `BackgroundImporter`, `BackgroundEntry`, `ImportJob`, `ArkeaConfig`, `MenuBackground`.

#### BackgroundEntry
- Path: `src/main/java/com/aryston/arkea/background/BackgroundEntry.java`
- Role: One imported background: name, kind (video or image), size, frame rate, frame count, bytes and date, stored as `background.json` next to `thumbnail.jpg` and either `image.jpg` or `frames/00000.jpg`...

#### BackgroundImporter
- Path: `src/main/java/com/aryston/arkea/background/BackgroundImporter.java`
- Role: Converts MP4, M4V, MOV, GIF, PNG, JPG and BMP into a background folder. Videos and animations become a JPEG frame sequence at their own frame rate (at most 30 fps, 1280 x 720 and 60 seconds); stills and single frame files become one image of at most 1920 x 1080. Writes a 384 x 216 thumbnail and the manifest, reports progress and stops when cancelled.
- Notes: Playing JPEG frames needs no video decoder at runtime; JPEG decoding through STB is about ten times faster than decoding H.264 in Java, so playback stays cheap.

#### MediaFrames
- Path: `src/main/java/com/aryston/arkea/background/MediaFrames.java`
- Role: A source of decoded frames with their time, for the importer.

#### VideoFrames
- Path: `src/main/java/com/aryston/arkea/background/VideoFrames.java`
- Role: Decodes MP4 and MOV (H.264) with JCodec, converts to RGB and returns frames in presentation order (a small reorder buffer, because frames come out of the decoder in decode order when the video has B frames).

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
- Role: The accent in use, read from `ArkeaConfig.ACCENT` (green before the config loads).

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
- Members: `push`/`pop`, `translate`, `scaleAround`, `rotateAround`, `fade`, `fill`, `gradientHorizontal`, `gradientVertical`, `border`, `topHighlight`, `shadow`, `vignette`, `image`, `icon`, `text`, `metrics`, `canvasBox` (where a local box ends up on the canvas, used for hit tests), `clip`/`endClip` (scissor a local box), `visible` (the part of a canvas box inside the current clip), `cursor`.
- Depends on: `MeshBuilder`, `UiMeshRenderState`, `GeneratedTextures`, `TextMetrics`.
- Notes: The GUI pipelines cull back faces, so every quad goes through `MeshBuilder`, which fixes the winding. A whole icon or shadow is one render state, because vanilla puts every overlapping element on its own layer.

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
- Role: Standard 32 high button with label, optional leading and trailing icons (pixel icons keep their aspect), primary glow and hover brightness.

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
- Role: Id, icon, label and external flag of a sidebar entry.

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
- Role: Frame of a setting: icon box, name, description and hover; not focusable. `controlSlot` places the control widget on its right and keeps the text clear of it, `litWhen` turns the icon from gray to the accent while the setting is on, `tooltip` supplies the hover text of the whole row, and the content fades when the control is disabled. As a `PanelRow` it places and draws the control given to `control`.

### `com.aryston.arkea.ui.overlay`

#### ArkDialog
- Path: `src/main/java/com/aryston/arkea/ui/overlay/ArkDialog.java`
- Role: Modal popup: scrim, tone icon, title, wrapped body and right aligned buttons, with the pop enter and exit animations. Can refuse to close on a scrim click for destructive actions.
- Members: `WIDTH_SMALL`, `WIDTH_MEDIUM`, `WIDTH_LARGE`, `button`, `keepOpenOnScrimClick`, `open`, `close`, `cancel`, `layout`, `render`.

#### DialogContent
- Path: `src/main/java/com/aryston/arkea/ui/overlay/DialogContent.java`
- Role: Title, body, icon and tone color of a dialog.

#### ArkTooltip
- Path: `src/main/java/com/aryston/arkea/ui/overlay/ArkTooltip.java`
- Role: Tooltip of the hovered widget or row after the design delay, wrapped to 280 pixels, kept on screen, with fade in and out.

#### Popup
- Path: `src/main/java/com/aryston/arkea/ui/overlay/Popup.java`
- Role: Something drawn above the screen content that takes clicks, wheel and keys first while open (dropdown menus).

#### DropdownMenu
- Path: `src/main/java/com/aryston/arkea/ui/overlay/DropdownMenu.java`
- Role: Menu of a dropdown: opens below the control (above when there is no room) with a 180 ms grow, rows of 28 with hover, check on the selected value, up to eight rows with wheel scrolling, arrow keys, Enter and Escape.

#### TooltipHint
- Path: `src/main/java/com/aryston/arkea/ui/overlay/TooltipHint.java`
- Role: Owner and text of a tooltip; a new owner restarts the delay.

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

### `com.aryston.arkea.screen.options`

#### ArkOptionsScreen
- Path: `src/main/java/com/aryston/arkea/screen/options/ArkOptionsScreen.java`
- Role: The options overview of `MC-Menu.dc.html`: quick settings (field of view slider, online settings), a tile for every options page, more (telemetry, credits) and the "changes save automatically" footer. Saves the options when it closes like vanilla.
- Depends on: `ArkWindowScreen`, `OptionsPage`, `SettingRow`, `ArkSlider`, `ArkTile`.
- Notes: Minecraft 26.3 moved difficulty out of the options screen, so the quick settings show the online settings instead of the difficulty of the design.

#### OptionsPage
- Path: `src/main/java/com/aryston/arkea/screen/options/OptionsPage.java`
- Role: Every options page with its sidebar group, icon, title (vanilla text without the trailing dots), label, description and the Arkea window page it opens. Helion appears only when it registers a config screen and opens its own screen. Arkea has its own page in the Content group.

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
| `src/main/resources/assets/arkea/textures/gui/` | `arkea_logo.png`: 32 x 32 logo of the "UI by Arkea" footer. `helion_logo.png` and `helion_preview.png`: logo and preview of the Helion banner, from the design handoff. |

## Build Files

| File | Purpose |
|---|---|
| `build.gradle` | ModDevGradle setup, JCodec embedded with Jar-in-Jar for video import, JUnit 5 for `src/test/java` with the Minecraft classpath of `main`, `client` run forced to Vulkan, metadata expansion, logo packing. |
| `gradle.properties` | Single place for versions and mod metadata. |
| `settings.gradle` | Plugin repositories, Java toolchain resolver, project name. |
