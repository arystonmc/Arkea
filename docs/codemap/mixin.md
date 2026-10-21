# Arkea Code Map: `com.aryston.arkea.mixin`

Part of the [Arkea code map](../CODEMAP.md). Every class of this package has its entry here and nowhere else (rule D1).

### `com.aryston.arkea.mixin`

#### SplashRendererAccessor
- Path: `src/main/java/com/aryston/arkea/mixin/SplashRendererAccessor.java`
- Role: Reads the private splash text of `SplashRenderer`.
- Notes: `SplashManager` only hands out a `SplashRenderer`, which draws itself at a fixed spot, angle and size. The design draws the splash in its own place and style, and no API exposes the text.

#### OptionInstanceAccessor
- Path: `src/main/java/com/aryston/arkea/mixin/OptionInstanceAccessor.java`
- Role: Reads the private tooltip supplier and default value of an `OptionInstance` for row tooltips and Reset to Defaults.

#### TooltipAccessor
- Path: `src/main/java/com/aryston/arkea/mixin/TooltipAccessor.java`
- Role: Reads the message of a vanilla `Tooltip`, which only exposes wrapped lines.

#### OptionsMixin
- Path: `src/main/java/com/aryston/arkea/mixin/OptionsMixin.java`
- Role: Runs `KeyModifierRepair` at the end of `Options.load`.

#### PanoramaMixin
- Path: `src/main/java/com/aryston/arkea/mixin/PanoramaMixin.java`
- Role: Draws the selected custom background instead of the vanilla panorama wherever the game draws it (title screen, menus over it), and lets the panorama draw when the panorama is selected.

#### OptionsSubScreenAccessor
- Path: `src/main/java/com/aryston/arkea/mixin/OptionsSubScreenAccessor.java`
- Role: Reads the previous screen of a vanilla options sub screen when `ClientEvents` replaces it.

#### SelectWorldScreenAccessor, JoinMultiplayerScreenAccessor, ModListScreenAccessor
- Path: `src/main/java/com/aryston/arkea/mixin/SelectWorldScreenAccessor.java`, `src/main/java/com/aryston/arkea/mixin/JoinMultiplayerScreenAccessor.java`, `src/main/java/com/aryston/arkea/mixin/ModListScreenAccessor.java`
- Role: Read the private previous screen of the singleplayer, multiplayer and NeoForge mods screens when `ClientEvents` replaces them.

#### SystemToastMixin, AdvancementToastMixin, RecipeToastMixin, TutorialToastMixin, RecipeToastEntryAccessor
- Path: `src/main/java/com/aryston/arkea/mixin/SystemToastMixin.java`, `src/main/java/com/aryston/arkea/mixin/AdvancementToastMixin.java`, `src/main/java/com/aryston/arkea/mixin/RecipeToastMixin.java`, `src/main/java/com/aryston/arkea/mixin/TutorialToastMixin.java`, `src/main/java/com/aryston/arkea/mixin/RecipeToastEntryAccessor.java`
- Role: Draw vanilla toasts in the Arkea style and size them; vanilla keeps timing and updates. No API exists to restyle toasts.

#### CreateWorldScreenInvoker
- Path: `src/main/java/com/aryston/arkea/mixin/CreateWorldScreenInvoker.java`
- Role: Calls the private `onCreate`, `openExperimentsScreen` and `openDataPackSelectionScreen` of `CreateWorldScreen`, so `ArkCreateWorldScreen` keeps the whole vanilla creation flow.

#### ConnectScreenAccessor, LevelLoadingScreenAccessor, ProgressScreenAccessor, DisconnectedScreenAccessor
- Path: `src/main/java/com/aryston/arkea/mixin/ConnectScreenAccessor.java`, `src/main/java/com/aryston/arkea/mixin/LevelLoadingScreenAccessor.java`, `src/main/java/com/aryston/arkea/mixin/ProgressScreenAccessor.java`, `src/main/java/com/aryston/arkea/mixin/DisconnectedScreenAccessor.java`
- Role: Read the status, load tracker and reason, header, stage and progress, and disconnection details that `LoadingViews` shows; none of these screens exposes them.

#### MinecraftAccessor
- Path: `src/main/java/com/aryston/arkea/mixin/MinecraftAccessor.java`
- Role: Reads the private SDL event handler so `LoadingFrames` can pump window events while it draws.

#### WorldOpenFlowsMixin
- Path: `src/main/java/com/aryston/arkea/mixin/WorldOpenFlowsMixin.java`
- Role: Hands the blocking wait for data packs in `WorldOpenFlows.loadWorldDataBlocking` to `LoadingFrames`.
- Notes: Vanilla waits with `managedBlock`, which runs tasks but draws nothing; there is no event to draw from.

#### ServerConnectionListenerMixin
- Path: `src/main/java/com/aryston/arkea/mixin/ServerConnectionListenerMixin.java`
- Role: Hands the blocking wait in `ServerConnectionListener.startMemoryChannel`, which opens the integrated server's in-memory channel, to `LoadingFrames`, so the loading screen keeps drawing.
- Notes: Vanilla calls `syncUninterruptibly` on the render thread; there is no event to draw from.

#### ConnectionLocalServerMixin
- Path: `src/main/java/com/aryston/arkea/mixin/ConnectionLocalServerMixin.java`
- Role: Hands the blocking wait in `Connection.connectToLocalServer`, where the client connects to the integrated server, to `LoadingFrames`.
- Notes: Vanilla calls `syncUninterruptibly` on the render thread; there is no event to draw from.

#### ConfigurationScreenAccessor
- Path: `src/main/java/com/aryston/arkea/mixin/ConfigurationScreenAccessor.java`
- Role: Reads the mod of a NeoForge `ConfigurationScreen` so `ClientEvents` can open the Arkea screen of the same mod. No event or getter exposes it.

#### AbstractButtonMixin, AbstractSliderButtonMixin, EditBoxMixin, CheckboxMixin, AbstractTextAreaWidgetMixin, AbstractSelectionListMixin, AbstractScrollAreaMixin, MenuTabButtonMixin, ScreenMixin, GuiGraphicsExtractorMixin
- Path: `src/main/java/com/aryston/arkea/mixin/AbstractButtonMixin.java`, `src/main/java/com/aryston/arkea/mixin/AbstractSliderButtonMixin.java`, `src/main/java/com/aryston/arkea/mixin/EditBoxMixin.java`, `src/main/java/com/aryston/arkea/mixin/CheckboxMixin.java`, `src/main/java/com/aryston/arkea/mixin/AbstractTextAreaWidgetMixin.java`, `src/main/java/com/aryston/arkea/mixin/AbstractSelectionListMixin.java`, `src/main/java/com/aryston/arkea/mixin/AbstractScrollAreaMixin.java`, `src/main/java/com/aryston/arkea/mixin/MenuTabButtonMixin.java`, `src/main/java/com/aryston/arkea/mixin/ScreenMixin.java`, `src/main/java/com/aryston/arkea/mixin/GuiGraphicsExtractorMixin.java`
- Role: Replace the sprite and texture drawing of vanilla widgets, lists, tabs, the menu background and the header and footer separators with `VanillaTheme` while it is active; labels and behavior stay vanilla.
- Notes: No NeoForge event exposes widget drawing. `GuiGraphicsExtractorMixin` sits on a frequent `blit` overload and only compares the texture with the four separator textures before returning.

#### AlertScreenAccessor, BackupConfirmScreenAccessor, DeathScreenAccessor, StatsScreenAccessor
- Path: `src/main/java/com/aryston/arkea/mixin/AlertScreenAccessor.java`
- Role: Read the private message of alerts, the description of the backup prompt, the cause and score of the death screen and the previous screen of the statistics screen. Paths of the others: `src/main/java/com/aryston/arkea/mixin/BackupConfirmScreenAccessor.java`, `src/main/java/com/aryston/arkea/mixin/DeathScreenAccessor.java`, `src/main/java/com/aryston/arkea/mixin/StatsScreenAccessor.java`.

#### HudAccessor
- Path: `src/main/java/com/aryston/arkea/mixin/HudAccessor.java`
- Role: Reads the selected item name timer and item, the current contextual bar and the player list overlay of `Hud`, which have no getters.

#### TooltipRenderUtilMixin
- Path: `src/main/java/com/aryston/arkea/mixin/TooltipRenderUtilMixin.java`
- Role: Hands the default tooltip background to `ArkTooltips`; styled tooltips keep their sprites.

#### HudCrosshairMixin
- Path: `src/main/java/com/aryston/arkea/mixin/HudCrosshairMixin.java`
- Role: Swaps only the crosshair sprite in `Hud.extractCrosshair`, so the spectator, third person, 3D crosshair and attack indicator rules stay vanilla.

#### MultiPlayerGameModeAccessor
- Path: `src/main/java/com/aryston/arkea/mixin/MultiPlayerGameModeAccessor.java`
- Role: Reads the breaking progress and position.

#### PlayerTabOverlayAccessor
- Path: `src/main/java/com/aryston/arkea/mixin/PlayerTabOverlayAccessor.java`
- Role: Reads the header and footer of the player list that the server sent.

#### ClientPacketListenerMixin
- Path: `src/main/java/com/aryston/arkea/mixin/ClientPacketListenerMixin.java`
- Role: Tells `ArkStatsScreen` when the statistics arrive (vanilla only notifies `StatsScreen`) and hands items and experience the local player picks up to `PickupFeed`, right after the packet moves to the main thread.

#### DebugScreenOverlayMixin
- Path: `src/main/java/com/aryston/arkea/mixin/DebugScreenOverlayMixin.java`
- Role: Hands the left and right F3 line lists to `DebugCards` instead of the vanilla text with gray bars. Charts and the profiler stay vanilla.

#### ChatScreenMixin
- Path: `src/main/java/com/aryston/arkea/mixin/ChatScreenMixin.java`
- Role: With the Arkea chat on, places the input on `ChatInput` before the first command suggestion update of `init`, paints the input bar through `ChatInput` instead of the black fill and sends wheel and page key scrolls through `ArkChat.scroll`; otherwise replaces the black fill with `VanillaTheme.chatBar` while Arkea Style Everywhere is on. No event covers the input position or the scroll amount.

#### ChatComponentAccessor
- Path: `src/main/java/com/aryston/arkea/mixin/ChatComponentAccessor.java`
- Role: Reads the private scroll position of the chat, so `ArkChat` knows how far a scroll really moved after vanilla clamps it.

#### ChatComponentMixin
- Path: `src/main/java/com/aryston/arkea/mixin/ChatComponentMixin.java`
- Role: Hooks `ChatComponent.extractRenderState`, the one path that both draws the chat and finds the message under the mouse: wraps its `ChatGraphicsAccess` with `ArkChat.wrap`, lifts the bottom offset through `ArkChat.bottom` and caps `getScale` and the private `getWidth` through `ArkChat`, so clicks and hover follow the drawn chat. Also hands the drawing graphics of the public `extractRenderState` to `ArkChat` (for heads), reports the line `forEachLine` is visiting (`ArkChat.enter` and `leave`), widens the lines it visits while the chat scrolls (`getLinesPerPage` and the private `chatScrollbarPos` there), resets the scroll animation in `resetChatScroll` and wraps lines through `ArkChat.split` in `addMessageToDisplayQueue`. No API exposes the line or the wrap width.

#### CommandSuggestionsMixin, SuggestionsListMixin
- Path: `src/main/java/com/aryston/arkea/mixin/CommandSuggestionsMixin.java`, `src/main/java/com/aryston/arkea/mixin/SuggestionsListMixin.java`
- Role: With Arkea Style Everywhere on, the command help lines become `VanillaTheme.chatBar` panels, and the suggestion list draws `VanillaTheme.suggestions` behind its rows (private `rect`, `offset`, `current` shadowed) instead of its black fills, keeps only the dotted more markers in the accent, recolors the entries and moves their text three GUI units away from the accent line (the list is widened to match). With the Arkea chat on, the suggestions of the chat screen are drawn on a new stratum above the chat (sender heads would cover them otherwise), and the suggestion list and help lines are anchored to the input bar instead of the screen bottom (`Screen.height` read in `showSuggestions` and `extractUsage`). No event covers the suggestion drawing; input and narration stay vanilla.
