# Arkea Code Map: `com.aryston.arkea.ui.layout`

Part of the [Arkea code map](../CODEMAP.md). Every class of this package has its entry here and nowhere else (rule D1).

### `com.aryston.arkea.ui.layout`

#### UiScale
- Path: `src/main/java/com/aryston/arkea/ui/layout/UiScale.java`
- Role: Maps design pixels (1366 x 768 reference) to GUI units: scale in quarter steps so the design always fits, canvas size, mouse conversion, snapping of line thickness to whole physical pixels. `reference` builds a scale from the window alone (a given scale at 1920 x 1080, proportional to the smaller side ratio) for the HUD and the F3 cards.

#### Box
- Path: `src/main/java/com/aryston/arkea/ui/layout/Box.java`
- Role: Rectangle in design pixels with edges, center, hit test, emptiness, offset, inset and intersection.
