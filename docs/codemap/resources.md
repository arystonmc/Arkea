# Arkea Code Map: Source Files and Asset Folders

Part of the [Arkea code map](../CODEMAP.md). Every file under `src/` that is not a Java class, and every asset and data folder.

## Source Files

| File | Purpose |
|---|---|
| `src/main/templates/META-INF/neoforge.mods.toml` | Mod metadata template filled from `gradle.properties`: dependencies and mixin config. |
| `src/main/resources/arkea.mixins.json` | Mixin configuration listing the accessors and mixins (`MinecraftAccessor`, `WorldOpenFlowsMixin`, `ServerConnectionListenerMixin` and `ConnectionLocalServerMixin` for drawing while data packs load and the integrated server connects). |

## Asset Folders

| Folder | Contents |
|---|---|
| `src/main/resources/assets/arkea/font/` | `inter_medium.ttf` and `inter_semibold.ttf`: unmodified Inter 4.1 (SIL Open Font License, see `THIRD_PARTY_NOTICES.md`). `debug_medium_<n>.json` and `debug_semibold_<n>.json`: TrueType font definitions of the F3 cards, size 10 with oversampling 1, 1.5, 2, 2.5, 3 and 4 (`_` for the point), falling back to the default font for missing glyphs. |
| `src/main/resources/assets/arkea/lang/` | `en_us.json` and `tr_tr.json`: config, title screen, window and options texts, short option descriptions (`arkea.option.*`). Menu labels reuse vanilla keys so every game language shows them. |
| `src/main/resources/assets/arkea/textures/gui/` | `gallery_before.png` and `gallery_after.png`: sample images of the before and after preview in the gallery. `arkea_logo.png`, `helion_logo.png` and `kinetic_logo.png`: 48 x 48 sidebar logos, with `.png.mcmeta` files that turn on linear filtering. `helion_preview.png`: preview of the Helion banner. `hud_scene.png`: sample scene behind the HUD previews (from the design handoff). `sprites/crosshair/`: 15 x 15 crosshair shapes (`cross`, `gap`, `dot`, `circle`) and the `hit` marker, white on transparent, in the GUI sprite atlas. |

