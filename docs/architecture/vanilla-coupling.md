# Arkea Architecture: Vanilla Coupling

Part of the [Arkea architecture](../ARCHITECTURE.md).

## Vanilla Coupling

| Vanilla or NeoForge | Use |
|---|---|
| `ScreenEvent.Opening` | Replace `TitleScreen` with `ArkTitleScreen` |
| `GuiGraphicsExtractor.submitGuiElementRenderState`, `peekScissorStack`, `pose`, `text`, `nextStratum`, `blurBeforeThisStratum`, `requestCursor` | All drawing |
| `Panorama.extractRenderState` + `PanoramaMixin` | Title background, replaced by a custom background when one is selected |
| `DynamicTexture`, `NativeImage`, STB `stbi_load_from_memory`, `RenderSystem.getSamplerCache`, `GuiGraphicsExtractor.blit(GpuTextureView, GpuSampler, ...)`, `ClientTickEvent.Post` | Custom background playback and release |
| SDL `SDL_ShowOpenFileDialog`, `Window.handle`, `Screen.onFilesDrop`, `Blaze3D.openPath` | Importing backgrounds, opening the backgrounds folder |
| `SplashManager.getSplash` + `SplashRendererAccessor` | Splash text |
| `LevelStorageSource.findLevelCandidates`, `loadLevelSummaries`, `FaviconTexture.forWorld`, `WorldOpenFlows.openWorld` | Jump back in card |
| `Minecraft.allowsMultiplayer`, `isNameBanned`, `multiplayerBan` | Disabled multiplayer and Realms buttons |
| `ModList.get().size()`, `ModListScreen.create` | Mods button |
| `OptionsScreen.getLastScreen`, `Options.fov`, `Options.save`, the vanilla options sub-screens, `IConfigScreenFactory` | Options screen |
| `OptionInstance` (`values`, `toString`, `caption`, value set types) + `OptionInstanceAccessor`, `TooltipAccessor` | Option controls, labels, tooltips, defaults |
| `OptionsSubScreen` + `OptionsSubScreenAccessor`, `ScreenEvent.Opening` | Replacing the vanilla options pages |
| `Minecraft.onTextInputFocusChange`, `textInputManager().setTextInputArea` | Typed characters and IME position for `ArkTextField` |
| `KeyMapping` (`same`, `setKeyModifierAndCode`, `resetMapping`), NeoForge `KeyModifier`, `IKeyMappingExtension` | Key binds page |
| `Options.load` + `OptionsMixin` | Restoring modifier key bindings that NeoForge 26.3 beta drops |
| `LanguageManager`, `Minecraft.reloadResourcePacks` | Language page |
| `PackSelectionModel`, `PackDetector`, `Util.copyBetweenDirs`, `Options.updateResourcePacks` | Resource packs page |
| `GpuWarnlistManager`, `UnsupportedGraphicsWarningScreen`, `Window.changeFullscreenVideoMode`, `Minecraft.updateMaxMipLevel`, `delayTextureReload` | Video page side effects copied from `VideoSettingsScreen` |
| `SelectWorldScreen` + accessor, `LevelSummary`, `LevelStorageAccess` (`makeWorldBackup`, `deleteLevel`, `renameLevel`), `WorldOpenFlows` (`openWorld`, `recreateWorldData`), `OptimizeWorldScreen.create`, `CreateWorldScreen.openFresh`, `createFromExisting` | Singleplayer screen |
| `CreateWorldScreen` + `CreateWorldScreenInvoker`, `WorldCreationUiState`, `WorldCreationGameRulesScreen`, `PresetEditor` | Create world screen |
| `JoinMultiplayerScreen` + accessor, `ServerList`, `ServerData`, `ServerStatusPinger`, `LanServerDetection`, `ConnectScreen.startConnecting`, `ServerAddress` | Multiplayer screen |
| `ScreenEvent.Render.Pre`, `ConnectScreen`, `LevelLoadingScreen`, `LevelLoadTracker`, `ProgressScreen`, `GenericMessageScreen`, `DisconnectedScreen` + accessors | Loading skin |
| `WorldOpenFlows.loadWorldDataBlocking` + `WorldOpenFlowsMixin`, `Minecraft.renderFrame`, `managedBlock`, `RenderSystem.pumpEvents` + `MinecraftAccessor` | Drawing while data packs load |
| `RegisterGuiLayersEvent.registerAboveAll`, `RenderGuiEvent.Pre`, `RenderGuiLayerEvent.Pre`, `Screen.extractBackground` of the closed loading screen, `Minecraft.schedule` | Loading outro, screen switches outside rendering |
| `RenderGuiLayerEvent.Pre`, `VanillaGuiLayers`, `CustomizeGuiOverlayEvent.BossEventProgress`, `Hud` (`isHidden`, `HeartType`, `getMobEffectSprite`) + `HudAccessor`, `ExperienceBar`, `ClientHooks.firePlayerHeartTypeEvent`, `IClientMobEffectExtensions`, `MobEffectUtil.formatDuration`, scoreboard objectives and `StyledFormat.SIDEBAR_DEFAULT`, `PlayerTabOverlay` (`setVisible`, `getNameForDisplay`) + `PlayerTabOverlayAccessor`, `PlayerFaceExtractor`, `AvatarRenderer.isPlayerUpsideDown`, `RegisterGuiLayersEvent`, `ClientboundTakeItemEntityPacket`, `SoundEventListener`, `AttackEntityEvent`, `Player.getHurtDir`, `getLastDeathLocation`, `getProjectile`, `MultiPlayerGameMode` breaking fields, mineable block tags, `SystemToast` | Arkea HUD and HUD features |
| `ChatComponent.extractRenderState` (the bottom offset 40, `ChatGraphicsAccess`), `forEachLine`, `addMessageToDisplayQueue` and `GuiMessage.splitLines`, `getScale`, private `getWidth`, `rescaleChat` + `ChatComponentMixin`, `ClientChatReceivedEvent`, `ChatType.Bound`, `chat.type.text`, `PlayerInfo`, `PlayerFaceExtractor`, `DefaultPlayerSkin` | Arkea chat, sender heads and names |
| `CommandSuggestions.extractUsage`, `CommandSuggestions$SuggestionsList` (`rect`, `offset`, `current`) + `CommandSuggestionsMixin`, `SuggestionsListMixin` | Command help and suggestions |
| `ChatScreen.init` (`updateCommandInfo`), `input`, `mouseScrolled` and `keyPressed` (`ChatComponent.scrollChat`), `ChatComponent.forEachLine` (`getLinesPerPage`, `chatScrollbarPos`), `resetChatScroll`, `Screen.height` in `CommandSuggestions.showSuggestions` and `extractUsage` + `ChatScreenMixin`, `ChatComponentAccessor` | Chat input under the chat, smooth chat scrolling |
| TrueType font providers (`ttf`, `oversample`), `FontDescription.Resource`, `StringDecomposer.iterateFormatted` | Inter in the F3 cards |
| `CustomizeGuiOverlayEvent.BossEventProgress` (`getY`), `RegisterGuiLayersEvent.registerBelow` | Target card below the boss bars |
| GUI atlas sprite contents (`SpriteContents.isTransparent`) | Saturation outline in food tooltips |
| `TooltipRenderUtil.extractTooltipBackground` + `TooltipRenderUtilMixin`, `RenderTooltipEvent.GatherComponents`, `ItemTooltipEvent`, `RegisterClientTooltipComponentFactoriesEvent`, `DataComponents.FOOD` and `CONTAINER` | Arkea tooltips |
| NeoForge `ModListScreen` + accessor, `ModDisplayInfo`, `DefaultModDisplayInfo`, `ImageResource`, `IConfigScreenFactory`, `VersionChecker`, `ConfirmLinkScreen` | Mods screen |
