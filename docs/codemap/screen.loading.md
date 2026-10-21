# Arkea Code Map: `com.aryston.arkea.screen.loading`

Part of the [Arkea code map](../CODEMAP.md). Every class of this package has its entry here and nowhere else (rule D1).

### `com.aryston.arkea.screen.loading`

#### LoadingSkin
- Path: `src/main/java/com/aryston/arkea/screen/loading/LoadingSkin.java`
- Role: Draws the connecting and loading design over vanilla loading screens without replacing them: background, spinner, check tile or error tile, kicker, title (fades in when it changes), detail, step label with percentage and an 8 pixel progress bar with a moving sheen, step list (ticks pop in when a step finishes), buttons and a tip card. Every animation runs on the session clock, and the bar shows the session's smoothed progress. The vanilla buttons keep handling input; every frame they are moved to where the design draws them and painted with `ButtonPainter`. Also draws the same content for `LoadingOutro`.
- Notes: The vanilla screens cannot be replaced: game code checks `instanceof LevelLoadingScreen` (HUD, portals, music, packet handling) and `ConnectScreen` runs the connection itself.

#### LoadingViews
- Path: `src/main/java/com/aryston/arkea/screen/loading/LoadingViews.java`
- Role: Builds the view of a supported vanilla screen: `ConnectScreen` (steps from the connection status), `LevelLoadingScreen` (world, server terrain or portal travel), `ProgressScreen`, `GenericMessageScreen` and `DisconnectedScreen` (error with the reason). A single player join is one view with four steps (world data, starting the world, spawn area, terrain) from the first "Reading world data" message to the end, so the screen never changes its layout; each step owns a span of the bar (0 to 12 %, 12 to 20 %, 20 to 100 % from the load tracker), and connecting to a server gives each connection step a quarter.

#### LoadingView
- Path: `src/main/java/com/aryston/arkea/screen/loading/LoadingView.java`
- Role: Mood (busy, ready, error), texts, progress span and fraction (unknown fractions creep), steps of one loading frame and whether it belongs to a world join; `joinsWorld` is true for world and server joins (they have steps; portal travel and other waits do not); `ready` turns it into the finished view of the outro.

#### LoadingSession
- Path: `src/main/java/com/aryston/arkea/screen/loading/LoadingSession.java`
- Role: One loading sequence across vanilla screens: it continues while loading screens follow each other (a new one starts after another screen or 1.5 s without one), so the enter animation and the tip appear once. Keeps a clock that advances at most 34 ms per frame, so a slow frame pauses the animations instead of making them jump, follows the progress target smoothly without ever moving back (a new sequence starts the bar again), and remembers when the title and each step changed. `finish` turns the last view into the ready view, `tick` drives the clock during the outro.
- Depends on: `LoadingView`, `ProgressMotion`.

#### LoadingOutro
- Path: `src/main/java/com/aryston/arkea/screen/loading/LoadingOutro.java`
- Role: GUI layer above everything that plays the move into the game when a loading screen closes into a world: 280 ms ready state (bar fills, every step ticks, the spinner turns into the green check tile), 240 ms dip in which the content fades and grows while the picture goes to black, then 640 ms in which the black fades out over the world. Until the black covers the screen it draws the closed loading screen's own background (with its blur) and hides every other HUD layer; with another screen open it skips straight to the black. Stops when a new loading screen opens. When the outro of a world or server join starts it plays `PLAYER_LEVELUP` as a UI sound at half volume, unless `JOIN_SOUND` is off.
- Depends on: `LoadingSkin`, `LoadingSession`, `LoadingView`, `ArkeaConfig`.

#### LoadingFrames
- Path: `src/main/java/com/aryston/arkea/screen/loading/LoadingFrames.java`
- Role: While vanilla blocks the render thread for data packs (`WorldOpenFlows.loadWorldDataBlocking`) and a loading screen is shown, keeps pumping window events and drawing frames, and runs the main thread tasks the loading needs between them; vanilla showed one frozen frame for 1.2 to 1.5 s there.

#### Reconnect
- Path: `src/main/java/com/aryston/arkea/screen/loading/Reconnect.java`
- Role: Reconnect button added to the disconnected screen after a server connection.

#### JoinTarget
- Path: `src/main/java/com/aryston/arkea/screen/loading/JoinTarget.java`
- Role: Name of the world or server the player last chose, shown as the loading title.
