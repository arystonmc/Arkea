# Arkea Architecture: Window Screens

Part of the [Arkea architecture](../ARCHITECTURE.md).

## Window Screens

- `ArkWindowScreen` draws the shell of every settings screen: 1238 x 704 window centered on the canvas, sidebar with brand and grouped navigation, header with back, breadcrumb, title and close, content area, footer with a note and buttons.
- Its background is the vanilla panorama on the title screen or the world in game, blurred once, with a dim layer that fades with the window.
- The window enters with opacity, a 16 pixel rise and a 0.98 scale (450 ms) and exits in reverse (220 ms); content rows stagger in with `renderRow`.
- A subclass builds its content in `buildContent` inside the content box, returns where it ends and draws it in `renderContent`. Content taller than the area scrolls: it is clipped with a scissor, translated by the eased offset of `ScrollArea`, and widgets take their hit box from the clipped box. Keyboard focus scrolls the focused row into view.
- `switchTo` moves between window screens without closing the window: the old content fades and rises 8 pixels in 160 ms, the next screen starts with the window already open and only its rows and title animate in.
- Settings pages lay out with `SettingsPanel` (the two column grid of the design) and get their controls from `OptionControls`, which reads the value set of each vanilla `OptionInstance`. Vanilla values, listeners and side effects stay in charge; the controls only read and set options. Every vanilla options page has an Arkea version. Content lists Arkea, Helion and Kinetic after Resource Packs: Arkea opens its own settings screen, while loaded Helion and Kinetic installations open their `IConfigScreenFactory` screens. Menu Backgrounds and HUD Preview are hidden navigation pages opened from Arkea settings.
- Long pages only draw the rows inside the visible area. Pages with a search field (key binds, language, packs) rebuild their content on every change and keep the focus on the widget with the same key.
- Popups (dropdown menus) render after the content and take input first; a click outside closes them.
