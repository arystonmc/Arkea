# Arkea Code Map: `com.aryston.arkea.screen.options`

Part of the [Arkea code map](../CODEMAP.md). Every class of this package has its entry here and nowhere else (rule D1).

### `com.aryston.arkea.screen.options`

#### ArkOptionsScreen
- Path: `src/main/java/com/aryston/arkea/screen/options/ArkOptionsScreen.java`
- Role: The options overview of `MC-Menu.dc.html`: quick settings (field of view slider, online settings), a tile for every available non-external options page, more (telemetry, credits) and the "changes save automatically" footer. Hidden Backgrounds and HUD pages and external Arkea, Helion and Aervium settings screens do not appear as overview tiles. Saves the options when it closes like vanilla.
- Depends on: `ArkWindowScreen`, `OptionsPage`, `SettingRow`, `ArkSlider`, `ArkTile`.
- Notes: Minecraft 26.3 moved difficulty out of the options screen, so the quick settings show the online settings instead of the difficulty of the design.

#### OptionsPage
- Path: `src/main/java/com/aryston/arkea/screen/options/OptionsPage.java`
- Role: Every options page with its sidebar group, icon, title (vanilla text without the trailing dots), label, description and the Arkea window page it opens. The Content group lists Arkea, Helion and Aervium after Resource Packs; Helion and Aervium appear only when their mods register config-screen factories and open those screens. The hidden Backgrounds and HUD pages are excluded from overview tiles and are opened from the Arkea settings screen. Arkea, Helion and Aervium show their logos (`ARKEA_LOGO`, `HELION_LOGO`, `AERVIUM_LOGO`) in the sidebar.

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
