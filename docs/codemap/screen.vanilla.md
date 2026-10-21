# Arkea Code Map: `com.aryston.arkea.screen.vanilla`

Part of the [Arkea code map](../CODEMAP.md). Every class of this package has its entry here and nowhere else (rule D1).

### `com.aryston.arkea.screen.vanilla`

#### VanillaTheme
- Path: `src/main/java/com/aryston/arkea/screen/vanilla/VanillaTheme.java`
- Role: Arkea look for the widgets of every screen Arkea does not replace (vanilla and other mods): buttons (Done, Yes, Proceed and Continue as primary), slider tracks and handles, text fields and text areas, checkboxes, list backgrounds, header and footer separators, scrollbars, menu tabs, the menu background, the chat input bar, command help lines and the command suggestion panel (selected row with the accent), with hover animations and the accent color. Vanilla keeps the labels, layout and behavior. Inactive in container screens and when the Arkea Style Everywhere switch is off.

#### VanillaSkin
- Path: `src/main/java/com/aryston/arkea/screen/vanilla/VanillaSkin.java`
- Role: Base of screens that keep their vanilla instance but are drawn by Arkea: cancels nothing itself, draws the background, opens a design pixel frame and offers `Frame` helpers that move vanilla widgets to design boxes (so they stay the input targets) and paint them.

#### VanillaSkins
- Path: `src/main/java/com/aryston/arkea/screen/vanilla/VanillaSkins.java`
- Role: The skins `ClientEvents` asks in `ScreenEvent.Render.Pre`: dialogs, pause and death.

#### DialogSkin
- Path: `src/main/java/com/aryston/arkea/screen/vanilla/DialogSkin.java`
- Role: Draws confirm, link, alert, popup, warning (multiplayer safety) and backup screens as an Arkea dialog: tone icon, wrapped title, the texts of the screen, its checkboxes and its buttons right aligned. Only screens whose children are buttons and text widgets are drawn this way.
