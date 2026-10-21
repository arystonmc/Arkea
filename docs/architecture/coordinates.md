# Arkea Architecture: Coordinates

Part of the [Arkea architecture](../ARCHITECTURE.md).

## Coordinates

- Screens are laid out in design pixels of the 1366 x 768 reference. `UiScale` picks a scale in quarter steps so the whole design fits the window, and the canvas grows beyond 1366 x 768 on wider or taller windows. Layouts anchor to the canvas edges like the CSS of the design.
- `ArkScreen` scales the pose by `scale / guiScale`, so one unit is one design pixel. Mouse positions arrive in GUI units and are divided by the same factor.
- Rectangles are snapped to whole physical pixels while the pose has no rotation. Line thickness never drops below one physical pixel.
- Text uses the vanilla font at the design size. When a font pixel comes within 0.15 of a whole number of physical pixels the scale snaps to it, so most text stays crisp; other sizes keep their exact design size so labels fit and the hierarchy of the design survives on every resolution.
