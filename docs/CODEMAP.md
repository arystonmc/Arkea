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
| Options pages (video, sound, chat, accessibility, skin, controls) | `OptionsPageScreen`, `ArkVideoScreen`, `ArkSoundScreen`, `ArkChatScreen`, `ArkAccessibilityScreen`, `ArkSkinScreen`, `ArkControlsScreen` |
| Which control a vanilla option gets, value labels, descriptions | `OptionControls`, `OptionText` |
| Title screen layout and actions | `ArkTitleScreen` |
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
  - `SPEC`: the built spec.

### `com.aryston.arkea.integration`

#### ClientEvents
- Path: `src/main/java/com/aryston/arkea/integration/ClientEvents.java`
- Role: Swaps a newly opened vanilla `TitleScreen` for `ArkTitleScreen` (not in demo mode), `OptionsScreen` for `ArkOptionsScreen` and the video, sound, chat, accessibility, skin, controls and mouse screens for the Arkea pages, keeping the previous screen, in `ScreenEvent.Opening`, each unless the config turns it off. Only the exact vanilla classes are replaced, so subclasses from other mods stay.
- Depends on: `ArkeaConfig`, `ArkTitleScreen`, `ArkOptionsScreen`, the options pages, `OptionsSubScreenAccessor`.

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
- Role: The accent in use. Green until a theme option exists.

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
- Role: Rectangle in design pixels with edges, center, hit test, offset, inset and intersection.

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

#### ArkBanner
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkBanner.java`
- Role: Clickable accent banner with a preview image, logo, title, subtitle and an action button with an external arrow (the "Helion is installed" banner of the video page).

#### ArkNavItem
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkNavItem.java`
- Role: Sidebar entry: icon and label, accent fill with marker and glow when selected, hover fill, external link arrow.

#### NavEntry
- Path: `src/main/java/com/aryston/arkea/ui/widget/NavEntry.java`
- Role: Id, icon, label and external flag of a sidebar entry.

#### ArkTile
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkTile.java`
- Role: 72 high link tile with accent icon box, name, description and chevron.

#### SettingRow
- Path: `src/main/java/com/aryston/arkea/ui/widget/SettingRow.java`
- Role: Frame of a setting: icon box, name, description and hover; not focusable. `controlSlot` places the control widget on its right and keeps the text clear of it, `litWhen` turns the icon from gray to the accent while the setting is on, `tooltip` supplies the hover text of the whole row, and the content fades when the control is disabled.

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

#### TooltipHint
- Path: `src/main/java/com/aryston/arkea/ui/overlay/TooltipHint.java`
- Role: Owner and text of a tooltip; a new owner restarts the delay.

### `com.aryston.arkea.ui.screen`

#### ArkScreen
- Path: `src/main/java/com/aryston/arkea/ui/screen/ArkScreen.java`
- Role: Base of every Arkea screen. Computes `UiScale` on every init, scales the pose to design pixels, converts the mouse, renders the tooltip, runs one dialog on its own stratum with the menu blur, routes input and focus to the dialog while it is open, closes it with Escape, plays the exit animation before `navigate` or `leave` switches screens, and narrates dialogs.
- Depends on: `UiScale`, `UiGraphics`, `ArkWidget`, `ArkDialog`, `ArkTooltip`.
- Notes: a subclass that blurs its background reports it with `blursBackground`, so the dialog does not request the single blur of the frame again. `hoveredHint` decides which tooltip shows; the frame that switches screens still draws this screen, so no empty frame appears between two screens.

#### ArkWindowScreen
- Path: `src/main/java/com/aryston/arkea/ui/screen/ArkWindowScreen.java`
- Role: Base of settings windows: the 1238 x 704 window centered on the canvas with sidebar (brand, grouped navigation), header (back, breadcrumb, title, close), content area and footer (note, buttons). Draws the dim layer over the blurred panorama or world, plays the window enter and exit, the title slide and staggered rows (`renderRow`). Back and Escape return to the previous screen, close returns to the game or the title screen. The content is clipped to the area between header and footer and scrolls with the wheel or the scrollbar; keyboard focus scrolls the focused row into view (`revealBox`). `switchTo` moves to another window screen by fading only the content (160 ms) while the window stays.
- Depends on: `ArkScreen`, `ScrollArea`, `ArkNavItem`, `ArkIconButton`, `ArkButton`, `NavGroup`, `SidebarBrand`.

#### ScrollArea
- Path: `src/main/java/com/aryston/arkea/ui/screen/ScrollArea.java`
- Role: Scroll offset of a viewport with eased wheel scrolling, page jumps on the track, a draggable 4 pixel thumb and `reveal` for focus.

#### SettingsPanel
- Path: `src/main/java/com/aryston/arkea/ui/screen/SettingsPanel.java`
- Role: The settings grid of the design: half sections side by side, full sections across with two rows per line, section labels, staggered rows, disabled rows at 40 %, row tooltips and the row of a control for scrolling.

#### SettingsSection
- Path: `src/main/java/com/aryston/arkea/ui/screen/SettingsSection.java`
- Role: Title, width and rows (setting row, control, control size) of one section.

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
- Role: Every options page with its sidebar group, icon, title (vanilla text without the trailing dots), label, description and the screen it opens: an Arkea window page or, for key binds, language and resource packs, the vanilla screen. Helion appears only when it registers a config screen.

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

#### ArkSkinScreen
- Path: `src/main/java/com/aryston/arkea/screen/options/ArkSkinScreen.java`
- Role: A switch per player model part and the main hand.

#### ArkControlsScreen
- Path: `src/main/java/com/aryston/arkea/screen/options/ArkControlsScreen.java`
- Role: Mouse and movement options of the vanilla controls and mouse screens in one page, plus the key binds link.

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
| `src/test/java/com/aryston/arkea/ui/widget/SliderRangeTest.java` | Slider fraction, step snapping, clamping and an empty range. |
| `src/test/java/com/aryston/arkea/screen/options/control/OptionTextTest.java` | Vanilla "Caption: value" labels reduce to the value, percent labels use the Arkea key, unknown labels stay. |
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
| `build.gradle` | ModDevGradle setup, JUnit 5 for `src/test/java` with the Minecraft classpath of `main`, `client` run forced to Vulkan, metadata expansion, logo packing. |
| `gradle.properties` | Single place for versions and mod metadata. |
| `settings.gradle` | Plugin repositories, Java toolchain resolver, project name. |
