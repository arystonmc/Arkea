# Arkea Architecture: Config Windows

Part of the [Arkea architecture](../ARCHITECTURE.md).

## Config Windows

- `ArkeaConfigScreen.Builder` collects groups, pages, sections and options into a `Definition`. `ArkConfigScreen.open` creates a `ConfigSession` and the screen of the first page that has options. Every page is its own `ArkConfigScreen` sharing the session, so switching pages plays the content animation of `switchTo` and keeps unapplied values, expanded sections and text field states. Arkea uses this API for its own Appearance, Screens, HUD, Tooltips and Developers pages with immediate apply; its HUD and menu-background previews remain focused pages reached by links.
- Controls never write to a config directly: they read `session.value(option)` (unapplied value first, then the binding) and call `session.stage`. `apply` writes all staged values, saves each binding once (a `ModConfigSpec` save fires the NeoForge reloading event) and runs the apply listeners. `ApplyMode.IMMEDIATE` writes and saves at once.
- Requirements are evaluated every frame (`setActive` on the control), so turning a switch off disables the rows that need it without a rebuild. Presets, reset, paste and collapsing rebuild the page.
- `ClientEvents` replaces the exact NeoForge `ConfigurationScreen` class with `ModConfigScreens.forMod` (reads the mod through `ConfigurationScreenAccessor`). `ModConfigScreens` walks `ModConfigSpec.getValues()` and `getSpec()`: top level values form a General page, every top level section a page, nested sections become sections. Unloaded configs and synced configs on a remote or LAN server show a notice instead of controls, like NeoForge.
- The context menu and popups (dropdowns, the color picker) live in `ArkScreen`: right-click asks `contextMenu(x, y)`, and an open popup receives clicks, drags, releases, wheel, keys and typed characters first. Dialogs and side sheets share the `Overlay` interface.
