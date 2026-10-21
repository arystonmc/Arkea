# Arkea Code Map: `com.aryston.arkea.screen.debug`

Part of the [Arkea code map](../CODEMAP.md). Every class of this package has its entry here and nowhere else (rule D1).

### `com.aryston.arkea.screen.debug`

#### DebugCards
- Path: `src/main/java/com/aryston/arkea/screen/debug/DebugCards.java`
- Role: Draws the F3 debug lines as compact cards: every group of lines between empty lines becomes a card with an accent bar on the screen edge, the first line as its title. Sized for the window through `UiScale.reference` (1.5 at 1920 x 1080, 8 pixel text), undoing the pose scale of the debug screen scale option. Text is Inter through `DebugFont` (titles in SemiBold, lines in Medium); line spacing follows the text size. Active while the Arkea Debug Screen switch is on.

#### DebugFont
- Path: `src/main/java/com/aryston/arkea/screen/debug/DebugFont.java`
- Role: Picks the Inter font definition whose oversampling (1, 1.5, 2, 2.5, 3 or 4) is closest to the physical pixels per font pixel and sizes the text to exactly that, so the nearest filtered TrueType glyphs map one texel to one pixel and stay sharp. Builds the styled lines, keeping vanilla formatting codes.
