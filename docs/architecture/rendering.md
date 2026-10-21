# Arkea Architecture: Rendering

Part of the [Arkea architecture](../ARCHITECTURE.md).

## Rendering

- Everything is drawn through `UiGraphics`. It records `UiMeshRenderState`s (custom `GuiElementRenderState`s) with vertices already transformed into GUI coordinates.
- The GUI pipelines cull back faces. `MeshBuilder` stores every quad in the winding vanilla uses (negative signed area in GUI coordinates), whatever transform produced it.
- Vanilla puts every element that overlaps an earlier one on a higher layer, and each layer is a separate draw. Icons and shadows are therefore one render state each, holding all their quads.
- Soft shadows and glows are nine slices of one generated soft edge texture (Gaussian profile, CSS blur radius = two sigma). The vignette is a generated radial texture stretched over the canvas. Both are linear filtered.
- Icons are the SVG paths of the design, parsed once into polylines and drawn as 1.5 pixel quads with square caps or as filled convex polygons, so they stay sharp at every scale without textures.
- Alpha is a stack in `UiGraphics`: every color is multiplied by it. This is how fades work, because 26.x has no global shader color.
