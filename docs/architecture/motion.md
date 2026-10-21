# Arkea Architecture: Motion

Part of the [Arkea architecture](../ARCHITECTURE.md).

## Motion

- Time comes from `Util.getMillis()`, never ticks. The HUD reads it through `HudEvents.clock`, which the interface check can slow down.
- HUD animations react to changes between frames: `HudMotion` compares each snapshot with the last one (selected slot, item and count per slot, health, food, level) instead of listening to game events, so they work for every source of change (picking up, placing, dropping, commands, other mods).
- `Presence` and `Timeline` implement "every enter has a shorter ease-in exit". Durations live in `Motion`, curves in `Easing`.
- Screen switches go through `ArkScreen.navigate` or `leave`: the screen plays its exit, then the switch runs as a main thread task before the next frame. Input is ignored while leaving. The switch must never run inside rendering: opening a world there ran the whole data pack load and server start inside one frame, where `Minecraft.renderFrame` skips every frame it is asked to draw, so the menu froze until the server was ready.
- `added()` restarts the enter animation each time a screen is shown again; a resize only lays out again.
