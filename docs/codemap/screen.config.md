# Arkea Code Map: `com.aryston.arkea.screen.config`

Part of the [Arkea code map](../CODEMAP.md). Every class of this package has its entry here and nowhere else (rule D1).

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
