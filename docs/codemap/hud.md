# Arkea Code Map: `com.aryston.arkea.hud`

Part of the [Arkea code map](../CODEMAP.md). Every class of this package has its entry here and nowhere else (rule D1).

### `com.aryston.arkea.hud`

#### HudStyle
- Path: `src/main/java/com/aryston/arkea/hud/HudStyle.java`
- Role: The HUD styles: `VANILLA` (untouched), `COMPACT` (slim numbered bars in the bottom corners), `CLASSIC` (vanilla icons on plates in the corners) and `MINIMAL` (Compact, bars fade out while health, hunger and air are full), with their names and descriptions.

#### HudSettings
- Path: `src/main/java/com/aryston/arkea/hud/HudSettings.java`
- Role: The HUD config as one record (style, plate opacity, size, effect chips, boss bars, scoreboard, player list) with the slider ranges and defaults (size 75 to 125 percent). `scale` sizes the HUD for the window through `UiScale.reference`: 1.5 physical pixels per design pixel at 1920 x 1080 times the size, whatever the GUI scale, so text keeps two physical pixels per font pixel there.

#### HudSnapshot
- Path: `src/main/java/com/aryston/arkea/hud/HudSnapshot.java`
- Role: Everything the HUD shows, read from the camera player (worn armor and the food that eating the held item would give included): health, max health, absorption, heart type (poison, wither, frozen, through the NeoForge heart type event), hardcore, armor, food, saturation, hunger effect, air, vehicle health, experience, hotbar, offhand, attack indicator and the selected item name with its fade (from `HudAccessor`). `sample` builds a fixed player for previews, `sample(true)` a full one for Minimal; before any world was loaded the sample has no items and is not cached, so the items appear once components are bound.

#### HudPainter
- Path: `src/main/java/com/aryston/arkea/hud/HudPainter.java`
- Role: Draws the health and armor (bottom left), food or vehicle health and air (bottom right), the hotbar at three quarters of the HUD size (`HOTBAR_SIZE`) with offhand and attack indicator, the experience line and level and the selected item name in design pixels. Compact bars have ten segments that fill smoothly, a gold line for absorption or saturation and a pulsing red edge at low health; Classic+ blits the vanilla heart, food, armor and air sprites at double size, in rows for extra health. Also draws worn armor with durability bars above the health, a pulsing red edge on slots and armor about to break, and the hunger and saturation that the held food would restore as pulsing segments. Holds the fade of Minimal and a `HudMotion`, so each preview and the game use their own painter. Draws the stack count itself (vanilla decorations without the count) so it can roll. `plate` gives the plate color for the opacity setting; `experienceBar` and `levelText` give where the experience line and level are drawn; `LAYOUT_WIDTH` is the narrowest screen in design pixels on which the corner panels and the hotbar do not overlap.

#### HudPreview
- Path: `src/main/java/com/aryston/arkea/hud/HudPreview.java`
- Role: Draws a style over the sample scene in any box: Arkea styles through `HudPainter` on a virtual screen only as wide as the HUD layout needs (`HudPainter.LAYOUT_WIDTH` divided by the size, at most the real canvas), so the HUD fills the box instead of the scene; Vanilla with the vanilla sprites in GUI units at the same zoom and a uniform scale.

#### HudEffects
- Path: `src/main/java/com/aryston/arkea/hud/HudEffects.java`
- Role: Active effects as chips in the top right corner (beneficial in the first row, harmful in the second with a red edge): icon (or the mod's own icon through `IClientMobEffectExtensions`), level and time, ambient effects dimmed and ending effects blinking like vanilla.

#### HudBossBar
- Path: `src/main/java/com/aryston/arkea/hud/HudBossBar.java`
- Role: One boss bar as a slim line with notches and its name above, in a muted version of the boss bar color, eight design pixels below the vanilla position so the name keeps clear of the screen edge. Returns the bottom of the bar.

#### BossBarStack
- Path: `src/main/java/com/aryston/arkea/hud/BossBarStack.java`
- Role: Bottom of the lowest boss bar drawn this frame in GUI units, cleared by a layer below the boss bars and read by the target card layer above them.

#### HudSidebar
- Path: `src/main/java/com/aryston/arkea/hud/HudSidebar.java`
- Role: The sidebar scoreboard as a transparent card on the right: picks the objective like vanilla (team color slot first), sorts and limits the scores like vanilla and keeps their formatting.

#### HudTabList
- Path: `src/main/java/com/aryston/arkea/hud/HudTabList.java`
- Role: The Tab player list as a card at the top: header and footer from the server, players sorted and split into columns of 20 like vanilla, each slot with the player head (online mode), name with team formatting (spectators dimmed), score or hearts value of the list objective and four ping bars colored by latency. The local player's slot is highlighted with the accent.

#### HudMotion
- Path: `src/main/java/com/aryston/arkea/hud/HudMotion.java`
- Role: Animation state of one painter. The selection highlight slides to the new slot and the slot pops like a click; each slot watches its item, so a new item pops in with a spring, a growing stack bounces up and a shrinking one dips, and the count rolls (the old number slides out, the new one slides in tinted green or warm and turns white). Health and food bars ease to their value and keep a white ghost of lost points that drains after a short hold; the experience line eases and the level pops on level up; the item name rises in.

#### HudClock
- Path: `src/main/java/com/aryston/arkea/hud/HudClock.java`
- Role: Time source of every HUD painter and tracker (`Util.getMillis` by default); the interface check slows it down to capture animations.

#### CrosshairStyle
- Path: `src/main/java/com/aryston/arkea/hud/CrosshairStyle.java`
- Role: Crosshair shapes: Vanilla, thin cross, open cross, dot and circle, with their names.
