# Arkea Code Map

Index of the Arkea code map. Entries live in `docs/codemap/`, one file per Java package plus tests, resources and build files. The big picture is in `docs/ARCHITECTURE.md`.

## How to Find a Class

1. Find the feature in Quick Lookup below to get the class names.
2. Search the entry from the mod folder: `rg -n "^#### .*\bClassName\b" docs/codemap -A12`.
3. Or open one package file from the Packages table.

A class of package `P` is documented only in `codemap/P.md`, or in `codemap/P--<topic>.md` when the package is split. Update only that file in the same commit as the code (rule D1), and split it at a topic boundary once it grows past 24 KB. `scripts/verify.sh` enforces both.

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
| Config screen library for other mods, in-game overlay panel | `ArkeaConfigScreen`, `ConfigOption`, `ModConfigScreens`, `ArkConfigScreen`, `ArkOverlayScreen` |
| Radio, stepper, chips, range, color picker, context menu, side sheet, preview | `ArkRadio`, `ArkStepper`, `ArkChip`, `ArkRangeSlider`, `ColorPickerPopup`, `ArkContextMenu`, `ArkSideSheet`, `ArkCompareView` |
| Component gallery | `ArkGalleryScreen` |
| Arkea look of every other screen and of other mods | `VanillaTheme`, theme mixins, `DialogSkin` |
| Pause, death and statistics screens | `PauseSkin`, `PauseCard`, `DeathSkin`, `ArkStatsScreen` |
| In-game HUD styles (Compact, Classic+, Minimal) and their settings page | `HudPainter`, `HudEvents`, `ArkHudScreen`, `HudPreview` |
| Target card, pickup feed, inventory total, ammo, durability warnings, location chip, damage direction, death point, crosshair, sound radar | `TargetCard`, `PickupFeed`, `HotbarExtras`, `HudTracker`, `InfoChip`, `DamageIndicator`, `Crosshair`, `SoundRadar` |
| Arkea tooltips with food, durability and container previews | `ArkTooltips`, `TooltipRenderUtilMixin` |
| F3 debug cards, chat bar, command help and suggestions | `DebugCards`, `DebugScreenOverlayMixin`, `ChatScreenMixin`, `CommandSuggestionsMixin`, `SuggestionsListMixin`, `VanillaTheme` |
| Chat messages: plates, sender heads, colored names, entry animation, size and position | `ArkChat`, `ChatStyle`, `ChatSenders`, `ChatEntries`, `ChatComponentMixin` |
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

## Packages

| Package | Code map file | Classes | Purpose |
|---|---|---|---|
| `com.aryston.arkea` | [root.md](codemap/root.md) | 1 | Entry class with the mod id and logger. |
| `com.aryston.arkea.api.clock` | [api.clock.md](codemap/api.clock.md) | 1 | Public API that lets another mod supply the world clock text Arkea shows (see docs/API.md). |
| `com.aryston.arkea.api.config` | [api.config.md](codemap/api.config.md) | 14 | Public library API for config screens of other mods (see docs/API.md). |
| `com.aryston.arkea.background` | [background.md](codemap/background.md) | 19 | Menu background library, image, GIF and video import, playback. |
| `com.aryston.arkea.config` | [config.md](codemap/config.md) | 1 | Arkea config spec. |
| `com.aryston.arkea.debug` | [debug.md](codemap/debug.md) | 10 | Automated interface check run and its scene helpers. |
| `com.aryston.arkea.hud.chat` | [hud.chat.md](codemap/hud.chat.md) | 8 | Restyled chat with input, scrolling, entries and sender heads. |
| `com.aryston.arkea.hud.extra` | [hud.extra.md](codemap/hud.extra.md) | 10 | Optional HUD extras: pickup feed, hotbar extras, durability, damage indicator, crosshair, sound radar. |
| `com.aryston.arkea.hud` | [hud.md](codemap/hud.md) | 13 | Restyled HUD: style, settings, snapshot, painter, preview, boss bars, sidebar, tab list. |
| `com.aryston.arkea.hud.target` | [hud.target.md](codemap/hud.target.md) | 3 | Target card for the entity or block under the crosshair. |
| `com.aryston.arkea.integration` | [integration.md](codemap/integration.md) | 4 | Event wiring, key modifier repair, file dialogs and HUD events. |
| `com.aryston.arkea.mixin` | [mixin.md](codemap/mixin.md) | 47 | Mixins, accessors and invokers on vanilla screens, toasts, options and loading flows. |
| `com.aryston.arkea.screen.config` | [screen.config.md](codemap/screen.config.md) | 6 | Config window and in-game overlay panel, session, rows, controls and clipboard. |
| `com.aryston.arkea.screen.debug` | [screen.debug.md](codemap/screen.debug.md) | 2 | F3 debug cards and their font. |
| `com.aryston.arkea.screen.gallery` | [screen.gallery.md](codemap/screen.gallery.md) | 2 | Widget gallery screen. |
| `com.aryston.arkea.screen.game` | [screen.game.md](codemap/screen.game.md) | 6 | Pause, death and statistics screens. |
| `com.aryston.arkea.screen.loading` | [screen.loading.md](codemap/screen.loading.md) | 8 | Loading screen skin, views, session, outro and reconnect. |
| `com.aryston.arkea.screen.mods` | [screen.mods.md](codemap/screen.mods.md) | 5 | Mods screen with rows, details and icons. |
| `com.aryston.arkea.screen.options.arkea` | [screen.options.arkea.md](codemap/screen.options.arkea.md) | 6 | Arkea options page with background media tiles. |
| `com.aryston.arkea.screen.options.control` | [screen.options.control.md](codemap/screen.options.control.md) | 5 | Option controls built from vanilla option instances. |
| `com.aryston.arkea.screen.options.hud` | [screen.options.hud.md](codemap/screen.options.hud.md) | 4 | HUD options page with style cards and preview. |
| `com.aryston.arkea.screen.options.keys` | [screen.options.keys.md](codemap/screen.options.keys.md) | 3 | Key binding page and conflict detection. |
| `com.aryston.arkea.screen.options` | [screen.options.md](codemap/screen.options.md) | 11 | Options screen, navigation and the vanilla option pages. |
| `com.aryston.arkea.screen.options.packs` | [screen.options.packs.md](codemap/screen.options.packs.md) | 5 | Resource pack page, pack rows and folder watcher. |
| `com.aryston.arkea.screen.palette` | [screen.palette.md](codemap/screen.palette.md) | 4 | Command palette and settings search index. |
| `com.aryston.arkea.screen.servers` | [screen.servers.md](codemap/screen.servers.md) | 6 | Multiplayer screen, server and LAN rows, pings and dialogs. |
| `com.aryston.arkea.screen.settings` | [screen.settings.md](codemap/screen.settings.md) | 2 | Arkea settings screen and its options. |
| `com.aryston.arkea.screen.title` | [screen.title.md](codemap/screen.title.md) | 6 | Title screen, jump back in card, splash text and recent worlds. |
| `com.aryston.arkea.screen.toasts` | [screen.toasts.md](codemap/screen.toasts.md) | 1 | Game toast routing. |
| `com.aryston.arkea.screen.vanilla` | [screen.vanilla.md](codemap/screen.vanilla.md) | 4 | Vanilla theme and skins for screens that cannot be replaced. |
| `com.aryston.arkea.screen.worlds` | [screen.worlds.md](codemap/screen.worlds.md) | 12 | World select and create world screens, world library and dialogs. |
| `com.aryston.arkea.tooltip` | [tooltip.md](codemap/tooltip.md) | 8 | Item tooltips for food, durability and containers. |
| `com.aryston.arkea.ui.anim` | [ui.anim.md](codemap/ui.anim.md) | 8 | Easing curves, transitions, presence, timelines and progress motion. |
| `com.aryston.arkea.ui.layout` | [ui.layout.md](codemap/ui.layout.md) | 2 | UI scale and boxes. |
| `com.aryston.arkea.ui.overlay` | [ui.overlay.md](codemap/ui.overlay.md) | 20 | Dialogs, toasts, tooltips, popups, menus, side sheets and color picker. |
| `com.aryston.arkea.ui.render` | [ui.render.md](codemap/ui.render.md) | 20 | UI graphics, meshes, text metrics, icons, SVG paths and small painters. |
| `com.aryston.arkea.ui.screen` | [ui.screen.md](codemap/ui.screen.md) | 7 | Base screens, scroll area, settings panels and navigation. |
| `com.aryston.arkea.ui.text` | [ui.text.md](codemap/ui.text.md) | 1 | Accent and case insensitive search text. |
| `com.aryston.arkea.ui.theme` | [ui.theme.md](codemap/ui.theme.md) | 4 | Colors, accents and themes. |
| `com.aryston.arkea.ui.widget` | [ui.widget.md](codemap/ui.widget.md) | 51 | Widget engine: buttons, toggles, sliders, dropdowns, text fields and more. |

## Other Files

| Topic | Code map file | Contents |
|---|---|---|
| Tests | [tests.md](codemap/tests.md) | 20 JUnit test files under `src/test/`. |
| Source files and asset folders | [resources.md](codemap/resources.md) | Non-Java files under `src/` and asset folders. |
| Build files | [build.md](codemap/build.md) | Gradle and CI files. |
