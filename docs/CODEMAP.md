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
  - `SPEC`: the built spec.

### `com.aryston.arkea.integration`

#### ClientEvents
- Path: `src/main/java/com/aryston/arkea/integration/ClientEvents.java`
- Role: Swaps a newly opened vanilla `TitleScreen` for `ArkTitleScreen` in `ScreenEvent.Opening`, unless the config turns it off or the game runs in demo mode.
- Depends on: `ArkeaConfig`, `ArkTitleScreen`.

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
- Role: Rectangle in design pixels with edges, center, hit test, offset and inset.

### `com.aryston.arkea.ui.render`

#### UiGraphics
- Path: `src/main/java/com/aryston/arkea/ui/render/UiGraphics.java`
- Role: The only drawing API of Arkea screens. Wraps `GuiGraphicsExtractor` with a pose and alpha stack; draws pixel snapped rectangles, horizontal and vertical gradients, borders, top highlights, soft shadows and glows, the vignette, textures, icons and text in design pixels.
- Members: `push`/`pop`, `translate`, `scaleAround`, `rotateAround`, `fade`, `fill`, `gradientHorizontal`, `gradientVertical`, `border`, `topHighlight`, `shadow`, `vignette`, `image`, `icon`, `text`, `metrics`, `canvasBox` (where a local box ends up on the canvas, used for hit tests), `cursor`.
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
- Role: Measures text in design pixels: font scale (whole physical pixels per font pixel so the vanilla font stays crisp), cap height, width with letter spacing, ellipsis, word wrap.

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
- Role: Base of every interactive element. Implements vanilla `GuiEventListener` and `NarratableEntry`, so Tab and arrow navigation and narration work like vanilla. Tracks hover and press transitions, takes its hit box from where it was drawn (animations included), clicks on release over the widget, activates on Enter or Space when focused, draws the accent focus ring for keyboard focus, sets the pointer cursor, plays the click sound and carries an optional tooltip.

#### ArkButton
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkButton.java`
- Role: Standard 32 high button with label and optional leading icon, primary glow, hover brightness and press offset.

#### ButtonVariant
- Path: `src/main/java/com/aryston/arkea/ui/widget/ButtonVariant.java`
- Role: Fill, border, text and glow colors of primary, secondary, subtle, ghost and danger buttons.

#### ArkIconButton
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkIconButton.java`
- Role: Square icon only button.

#### IconButtonStyle
- Path: `src/main/java/com/aryston/arkea/ui/widget/IconButtonStyle.java`
- Role: Fill, border, icon color and icon size of an icon button.

#### ArkMenuButton
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkMenuButton.java`
- Role: Large menu entry that slides right on hover: icon tile, label, description and chevron, or an inline icon and label.

#### MenuButtonStyle
- Path: `src/main/java/com/aryston/arkea/ui/widget/MenuButtonStyle.java`
- Role: Colors, sizes and layout of menu buttons: `primary`, `secondary`, `dangerInline`.

#### MenuButtonContent
- Path: `src/main/java/com/aryston/arkea/ui/widget/MenuButtonContent.java`
- Role: Icon, label and optional description of a menu button.

#### MenuButtonLayout
- Path: `src/main/java/com/aryston/arkea/ui/widget/MenuButtonLayout.java`
- Role: Tile or inline content layout.

#### ArkTextLink
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkTextLink.java`
- Role: Clickable text that turns white and underlines on hover.

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
- Role: Tooltip of the hovered widget after the design delay, wrapped to 280 pixels, kept on screen, with fade in and out.

### `com.aryston.arkea.ui.screen`

#### ArkScreen
- Path: `src/main/java/com/aryston/arkea/ui/screen/ArkScreen.java`
- Role: Base of every Arkea screen. Computes `UiScale` on every init, scales the pose to design pixels, converts the mouse, renders the tooltip, runs one dialog on its own stratum with the menu blur, routes input and focus to the dialog while it is open, closes it with Escape, plays the exit animation before `navigate` or `leave` switches screens, and narrates dialogs.
- Depends on: `UiScale`, `UiGraphics`, `ArkWidget`, `ArkDialog`, `ArkTooltip`.

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

### `com.aryston.arkea.mixin`

#### SplashRendererAccessor
- Path: `src/main/java/com/aryston/arkea/mixin/SplashRendererAccessor.java`
- Role: Reads the private splash text of `SplashRenderer`.
- Notes: `SplashManager` only hands out a `SplashRenderer`, which draws itself at a fixed spot, angle and size. The design draws the splash in its own place and style, and no API exposes the text.

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
| `src/test/java/com/aryston/arkea/screen/title/LastPlayedTest.java` | Today, yesterday across midnight, and the localized date for older worlds. |

## Source Files

| File | Purpose |
|---|---|
| `src/main/templates/META-INF/neoforge.mods.toml` | Mod metadata template filled from `gradle.properties`: dependencies and mixin config. |
| `src/main/resources/arkea.mixins.json` | Mixin configuration listing `SplashRendererAccessor`. |

## Asset Folders

| Folder | Contents |
|---|---|
| `src/main/resources/assets/arkea/lang/` | `en_us.json` and `tr_tr.json`: config and title screen texts. Menu labels reuse vanilla keys so every game language shows them. |
| `src/main/resources/assets/arkea/textures/gui/` | `arkea_logo.png`: 32 x 32 logo of the "UI by Arkea" footer. |

## Build Files

| File | Purpose |
|---|---|
| `build.gradle` | ModDevGradle setup, JUnit 5 for `src/test/java` with the Minecraft classpath of `main`, `client` run forced to Vulkan, metadata expansion, logo packing. |
| `gradle.properties` | Single place for versions and mod metadata. |
| `settings.gradle` | Plugin repositories, Java toolchain resolver, project name. |
