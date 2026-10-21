# Arkea Code Map: `com.aryston.arkea.screen.servers`

Part of the [Arkea code map](../CODEMAP.md). Every class of this package has its entry here and nowhere else (rule D1).

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
