# Arkea Code Map: `com.aryston.arkea.ui.render`

Part of the [Arkea code map](../CODEMAP.md). Every class of this package has its entry here and nowhere else (rule D1).

### `com.aryston.arkea.ui.render`

#### UiGraphics
- Path: `src/main/java/com/aryston/arkea/ui/render/UiGraphics.java`
- Role: The only drawing API of Arkea screens. Wraps `GuiGraphicsExtractor` with a pose and alpha stack; draws pixel snapped rectangles, horizontal and vertical gradients, borders, top highlights, soft shadows and glows, the vignette, textures, icons and text in design pixels.
- Members: `push`/`pop`, `translate`, `scaleAround`, `rotateAround`, `fade`, `fill`, `gradientHorizontal`, `gradientVertical`, `border`, `topHighlight`, `shadow`, `vignette`, `image`, `icon`, `text`, `richText` (a component with its own colors, clipped with an ellipsis), `trueTypeLine` (a line in a TrueType font with its baseline moved onto a whole physical pixel), `imageCover` (a texture cropped to fill a box), `dashedBorder`, `metrics`, `canvasBox` (where a local box ends up on the canvas, used for hit tests), `clip`/`endClip` (scissor a local box), `visible` (the part of a canvas box inside the current clip), `cursor`.
- Depends on: `MeshBuilder`, `UiMeshRenderState`, `GeneratedTextures`, `TextMetrics`.
- Notes: The GUI pipelines cull back faces, so every quad goes through `MeshBuilder`, which fixes the winding. A whole icon or shadow is one render state, because vanilla puts every overlapping element on its own layer. Text is moved to the physical pixel grid before drawing, because the vanilla font is sampled nearest and a half pixel offset drops a row of every glyph. TrueType glyphs are placed from the baseline at seven font pixels, so at half pixel scales (1.5, 2.5) `trueTypeLine` also shifts the baseline by half a pixel.

#### UiMeshRenderState
- Path: `src/main/java/com/aryston/arkea/ui/render/UiMeshRenderState.java`
- Role: Custom `GuiElementRenderState` with any number of quads in GUI coordinates, colored or textured, with scissor and bounds.

#### MeshBuilder
- Path: `src/main/java/com/aryston/arkea/ui/render/MeshBuilder.java`
- Role: Collects quads for one render state and stores them in the winding vanilla uses.

#### QuadGeometry
- Path: `src/main/java/com/aryston/arkea/ui/render/QuadGeometry.java`
- Role: Scratch quad: four corners with position, texture coordinates and color, signed area.

#### GeneratedTextures
- Path: `src/main/java/com/aryston/arkea/ui/render/GeneratedTextures.java`
- Role: Builds the soft edge texture (nine slice shadows and glows) and the radial vignette at runtime as linear filtered `DynamicTexture`s, so no blurred images ship.

#### SoftEdgeProfile
- Path: `src/main/java/com/aryston/arkea/ui/render/SoftEdgeProfile.java`
- Role: Gaussian edge coverage (CSS box-shadow blur) and the linear radial fade of the vignette.

#### TextMetrics
- Path: `src/main/java/com/aryston/arkea/ui/render/TextMetrics.java`
- Role: Measures text in design pixels: font scale (the design size, snapped to whole physical pixels per font pixel when it is within 0.15 of one so the vanilla font stays crisp), cap height, width with letter spacing, ellipsis, word wrap.

#### TextStyle
- Path: `src/main/java/com/aryston/arkea/ui/render/TextStyle.java`
- Role: Design font size, letter spacing and an optional hard text shadow. `BASELINE` is where Minecraft places the baseline of a glyph, in font pixels.

#### Icon
- Path: `src/main/java/com/aryston/arkea/ui/render/Icon.java`
- Role: Parsed icon: polylines, view box size and stroke or fill style.

#### IconStyle
- Path: `src/main/java/com/aryston/arkea/ui/render/IconStyle.java`
- Role: Stroke (1.5 wide lines with square caps) or fill (convex polygons, pixel glyphs).

#### Icons
- Path: `src/main/java/com/aryston/arkea/ui/render/Icons.java`
- Role: Catalogue of every line icon of the design (`IC` map of `MC-Menu.dc.html`) plus close, search, play and the pixel chevrons and check.

#### SvgPathParser
- Path: `src/main/java/com/aryston/arkea/ui/render/SvgPathParser.java`
- Role: Turns SVG path data (move, line, horizontal, vertical, cubic, quadratic, arc, close, absolute and relative) into polylines.
- Depends on: `PathTokenizer`.

#### PathTokenizer
- Path: `src/main/java/com/aryston/arkea/ui/render/PathTokenizer.java`
- Role: Reads commands, numbers in compact SVG notation and arc flags.

#### Polyline
- Path: `src/main/java/com/aryston/arkea/ui/render/Polyline.java`
- Role: Point list with a closed flag.

#### PixelSpinner
- Path: `src/main/java/com/aryston/arkea/ui/render/PixelSpinner.java`
- Role: The pixel spinner of the design: eight squares on a 24 pixel ring whose brightness runs around once every 0.8 s.

#### EmptyState
- Path: `src/main/java/com/aryston/arkea/ui/render/EmptyState.java`
- Role: The dashed 280 high panel of empty lists (no servers, LAN scanning) with an optional spinner, a title and a hint.

#### LetterTile
- Path: `src/main/java/com/aryston/arkea/ui/render/LetterTile.java`
- Role: Colored square with the first letter of a name and a darker bottom edge, used when a server or mod has no icon; the color comes from the name, so it stays the same.

#### Meter
- Path: `src/main/java/com/aryston/arkea/ui/render/Meter.java`
- Role: Small painters of the design: ten segment meter (GPU impact), cost pips (1 to 3 squares), progress bar, step dots and the plain, labeled ("OR") and dashed dividers.

#### ItemSlot
- Path: `src/main/java/com/aryston/arkea/ui/render/ItemSlot.java`
- Role: Pixel item slot of the design (40 x 40, inset bevel, white frame when selected) with a real `ItemStack` drawn through the vanilla item renderer.

#### SampleItems
- Path: `src/main/java/com/aryston/arkea/ui/render/SampleItems.java`
- Role: Item stacks for previews that are safe on the title screen: item components are only bound when a world's data packs load, so before that it gives an empty stack instead of crashing in the `ItemStack` constructor.
