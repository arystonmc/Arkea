# Arkea Code Map: `com.aryston.arkea.screen.title`

Part of the [Arkea code map](../CODEMAP.md). Every class of this package has its entry here and nowhere else (rule D1).

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
