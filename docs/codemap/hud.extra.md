# Arkea Code Map: `com.aryston.arkea.hud.extra`

Part of the [Arkea code map](../CODEMAP.md). Every class of this package has its entry here and nowhere else (rule D1).

### `com.aryston.arkea.hud.extra`

#### PickupFeed
- Path: `src/main/java/com/aryston/arkea/hud/extra/PickupFeed.java`
- Role: Picked up items and experience as rows on the right ("+5 Iron Ingot" with the icon), filled from the take item packet (`ClientPacketListenerMixin`). Rows of the same item merge for three seconds with a bump of the count, slide in, and fade out after four seconds.

#### HotbarExtras
- Path: `src/main/java/com/aryston/arkea/hud/extra/HotbarExtras.java`
- Role: Inventory total of the held stack as a badge above the selected slot (bumps when it changes), placed above the experience bar and, where they would overlap, above the level number, and the ammo counter next to the hotbar while a bow or crossbow is held (infinite in creative or with Infinity, red at zero). Works over the Arkea and the vanilla hotbar.

#### HotbarLayout
- Path: `src/main/java/com/aryston/arkea/hud/extra/HotbarLayout.java`
- Role: Where the hotbar, the selected slot, the experience bar and the level number are drawn on the canvas, for the Arkea or the vanilla hotbar, so `HotbarExtras` can keep clear of them.

#### HudTracker
- Path: `src/main/java/com/aryston/arkea/hud/extra/HudTracker.java`
- Role: Client tick watcher: one toast when a hotbar item or worn armor drops under ten percent durability (again after a repair), one sleep toast per night in the Overworld, new hits for `DamageIndicator` (skipping fall, fire, lava, drowning, freezing and starving, which have no direction) and the time of death for the death point.

#### Durability
- Path: `src/main/java/com/aryston/arkea/hud/extra/Durability.java`
- Role: Remaining uses and the ten percent "about to break" rule.

#### DamageIndicator
- Path: `src/main/java/com/aryston/arkea/hud/extra/DamageIndicator.java`
- Role: Red arcs around the crosshair toward the attacker, from the hurt direction the server sends; the arc keeps pointing at the attacker while the player turns and fades in about a second.

#### InfoChip
- Path: `src/main/java/com/aryston/arkea/hud/extra/InfoChip.java`
- Role: Top left chips: coordinates (hidden with reduced debug info), direction, biome and time of day; and for ten minutes after a death in the same dimension, the distance to the death point with an arrow toward it. Nothing is drawn while F3 is open.

#### Crosshair, HitMarker
- Path: `src/main/java/com/aryston/arkea/hud/extra/Crosshair.java`, `src/main/java/com/aryston/arkea/hud/extra/HitMarker.java`
- Role: The sprite of the chosen crosshair shape (drawn by vanilla with its contrast pipeline through `HudCrosshairMixin`), and a short fading mark around the crosshair when the player attacks an entity (`AttackEntityEvent`).

#### SoundRadar
- Path: `src/main/java/com/aryston/arkea/hud/extra/SoundRadar.java`
- Role: Sound listener that places the subtitles of positioned sounds around the crosshair in the direction of the sound, merging repeats and fading after two seconds.
