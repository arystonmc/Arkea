# Arkea Code Map: `com.aryston.arkea.ui.theme`

Part of the [Arkea code map](../CODEMAP.md). Every class of this package has its entry here and nowhere else (rule D1).

### `com.aryston.arkea.ui.theme`

#### ArkColors
- Path: `src/main/java/com/aryston/arkea/ui/theme/ArkColors.java`
- Role: Every color token of the design (`01-design-tokens.json`) as an ARGB int, plus color math.
- Members: `rgb`, `rgba`, `withAlpha`, `alpha`, `multiplyAlpha` (used for every fade), `brighten` (CSS `brightness()` on hover), `lerp`.

#### Accent
- Path: `src/main/java/com/aryston/arkea/ui/theme/Accent.java`
- Role: The four accent themes (green, emerald, gold, stone) with base, light, border, glow and tint colors.

#### Theme
- Path: `src/main/java/com/aryston/arkea/ui/theme/Theme.java`
- Role: The accent in use. The design system asks a supplier set with `setAccentSource` (Arkea sets `ArkeaConfig::accent`), so `ui` never depends on the Arkea config.

#### Hsv
- Path: `src/main/java/com/aryston/arkea/ui/theme/Hsv.java`
- Role: Hue, saturation and value of a color with conversion from and to RGB, for the color picker.
