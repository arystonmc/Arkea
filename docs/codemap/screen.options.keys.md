# Arkea Code Map: `com.aryston.arkea.screen.options.keys`

Part of the [Arkea code map](../CODEMAP.md). Every class of this package has its entry here and nowhere else (rule D1).

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
