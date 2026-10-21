# Arkea Code Map: `com.aryston.arkea.screen.settings`

Part of the [Arkea code map](../CODEMAP.md). Every class of this package has its entry here and nowhere else (rule D1).

### `com.aryston.arkea.screen.settings`

#### ArkeaSettingsScreen
- Path: `src/main/java/com/aryston/arkea/screen/settings/ArkeaSettingsScreen.java`
- Role: Builds Arkea's own immediate-apply settings screen with Appearance, Screens, HUD, Tooltips and Developers pages, links to Menu Backgrounds and HUD Preview, and the component gallery.
- Depends on: `ArkeaSettingsOptions`, `ArkeaConfigScreen`, `ArkeaConfig`.

#### ArkeaSettingsOptions
- Path: `src/main/java/com/aryston/arkea/screen/settings/ArkeaSettingsOptions.java`
- Role: Binds every Arkea setting to the immediate-apply config screen through `ArkeaConfig.set`; the chat binding also rescales the chat after a change.
- Depends on: `ArkeaConfig`, `ArkeaConfigScreen`.
