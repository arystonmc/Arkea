# Arkea Architecture: Layers

Part of the [Arkea architecture](../ARCHITECTURE.md).

## Layers

| Layer | Package | Knows about |
|---|---|---|
| Theme | `ui.theme` | Design tokens only |
| Motion | `ui.anim` | Nothing but time in milliseconds |
| Layout | `ui.layout` | Window size and GUI scale |
| Render | `ui.render` | `GuiGraphicsExtractor`, GUI render states |
| Widgets and overlays | `ui.widget`, `ui.overlay` | Render, motion, vanilla input and narration interfaces |
| Screen base | `ui.screen` | Everything above, vanilla `Screen` |
| Library API | `api.config` | The design system; builds config windows for other mods |
| Screens | `screen.*` | The design system and vanilla data and screens |
| Menu backgrounds | `background` | Files, JCodec, STB, textures; no widgets |
| Wiring | `integration`, `config`, `mixin` | NeoForge events and config |

`ui` never imports from `screen` or `config`; the accent reaches it through `Theme.setAccentSource`. A new screen only needs `ArkScreen`, widgets and `UiGraphics`. `api` is the stable surface for other mods; `ui` is public too (see `docs/API.md`).
