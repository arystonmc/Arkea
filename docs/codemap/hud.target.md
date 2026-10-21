# Arkea Code Map: `com.aryston.arkea.hud.target`

Part of the [Arkea code map](../CODEMAP.md). Every class of this package has its entry here and nowhere else (rule D1).

### `com.aryston.arkea.hud.target`

#### TargetCard
- Path: `src/main/java/com/aryston/arkea/hud/target/TargetCard.java`
- Role: Card at the top center about what the crosshair points at, in the spirit of Jade, placed right under the boss bars when there are any (`BossBarStack`): item icon, name, hearts or a health bar with numbers and armor for living things, info lines (a green check or red cross after the tool line) and the mod name in blue italics; a breaking progress line while mining. Slides down and fades in and out; hidden while a screen or F3 is open.

#### TargetReader
- Path: `src/main/java/com/aryston/arkea/hud/target/TargetReader.java`
- Role: Builds the card content. Blocks: pick block icon, the effective tool from the mineable tags with a check when the held item is that tool (destroy speed above bare hands) or, for blocks that need a tool, can harvest it, growth percent from the `age` property, redstone power or powered state, breaking progress from `MultiPlayerGameModeAccessor`. Entities: spawn egg or item icon, health, armor, baby, villager profession and level, horse speed and jump height, item stack count. Mod names come from `ModList`.

#### TargetInfo
- Path: `src/main/java/com/aryston/arkea/hud/target/TargetInfo.java`
- Role: The card content: key, icon, name, mod, lines with color, icon and an optional check or cross (`Mark`), health, armor and breaking progress.
