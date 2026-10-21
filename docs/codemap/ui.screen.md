# Arkea Code Map: `com.aryston.arkea.ui.screen`

Part of the [Arkea code map](../CODEMAP.md). Every class of this package has its entry here and nowhere else (rule D1).

### `com.aryston.arkea.ui.screen`

#### ArkScreen
- Path: `src/main/java/com/aryston/arkea/ui/screen/ArkScreen.java`
- Role: Base of every Arkea screen. Computes `UiScale` on every init, scales the pose to design pixels, converts the mouse, renders the tooltip, runs one dialog on its own stratum with the menu blur, routes input and focus to the dialog while it is open, closes it with Escape, plays the exit animation before `navigate` or `leave` switches screens (the switch is scheduled as a main thread task after the frame, so world loading started from a menu never runs inside a frame, where every frame it tries to draw is skipped), and narrates dialogs.
- Depends on: `UiScale`, `UiGraphics`, `ArkWidget`, `ArkDialog`, `ArkTooltip`.
- Notes: a subclass that blurs its background reports it with `blursBackground`, so the dialog does not request the single blur of the frame again. `openPopup` shows a popup above the content; `rebuild` lays the screen out again and gives focus back to the widget with the same `key`. `hoveredHint` decides which tooltip shows; the frame that switches screens still draws this screen, so no empty frame appears between two screens.

#### ArkWindowScreen
- Path: `src/main/java/com/aryston/arkea/ui/screen/ArkWindowScreen.java`
- Role: Base of settings windows: the 1238 x 704 window centered on the canvas with sidebar (brand, grouped navigation), header (back, breadcrumb, title, close), content area and footer (note, buttons). Draws the dim layer over the blurred panorama or world, plays the window enter and exit, the title slide and staggered rows (`renderRow`). Back and Escape return to the previous screen, close returns to the game or the title screen. The content is clipped to the area between header and footer and scrolls with the wheel or the scrollbar; keyboard focus scrolls the focused row into view (`revealBox`). `switchTo` moves to another window screen by fading only the content (160 ms) while the window stays. A screen with `searchHint` gets a search field in the header (`/` focuses it) whose text survives rebuilds; `afterLayout` and `scrollToReveal` let a page scroll to a row once it is laid out.
- Depends on: `ArkScreen`, `ScrollArea`, `ArkNavItem`, `ArkIconButton`, `ArkButton`, `NavGroup`, `SidebarBrand`.

#### ScrollArea
- Path: `src/main/java/com/aryston/arkea/ui/screen/ScrollArea.java`
- Role: Scroll offset of a viewport with eased wheel scrolling, page jumps on the track, a draggable 4 pixel thumb and `reveal` for focus.

#### SettingsPanel
- Path: `src/main/java/com/aryston/arkea/ui/screen/SettingsPanel.java`
- Role: The settings grid of the design: half sections side by side, full sections across with two (or any number of) rows per line, section labels with optional right hints, rows of any `PanelRow` type, staggered rows, row tooltips, the row of a control for scrolling. Rows outside the visible area are skipped, so long lists stay fast.

#### SettingsSection
- Path: `src/main/java/com/aryston/arkea/ui/screen/SettingsSection.java`
- Role: Optional title and hint, width, columns and rows of one section.

#### NavGroup
- Path: `src/main/java/com/aryston/arkea/ui/screen/NavGroup.java`
- Role: Label and entries of a sidebar group.

#### SidebarBrand
- Path: `src/main/java/com/aryston/arkea/ui/screen/SidebarBrand.java`
- Role: Icon, title and subtitle at the top of the sidebar.
