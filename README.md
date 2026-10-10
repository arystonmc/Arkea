<p align="center">
  <img src="branding/logo.png" alt="Arkea Logo" width="160">
</p>

<h1 align="center">Arkea</h1>

<p align="center">
  <strong>A complete interface overhaul and modern UI engine for Minecraft.</strong>
</p>

<p align="center">
  <a href="https://modrinth.com/mod/arkea"><img src="https://img.shields.io/badge/Download_on-Modrinth-00AF5C?style=for-the-badge&logo=modrinth&logoColor=white" alt="Download on Modrinth"></a>
  <a href="https://www.curseforge.com/minecraft/mc-mods/arkea"><img src="https://img.shields.io/badge/Download_on-CurseForge-F16436?style=for-the-badge&logo=curseforge&logoColor=white" alt="Download on CurseForge"></a>
  <a href="https://lureid.com/mods/arkea"><img src="https://img.shields.io/badge/Website-Aryston-3C8527?style=for-the-badge" alt="Aryston website"></a>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Minecraft-26.3-5B8C5A?style=flat-square" alt="Minecraft 26.3">
  <img src="https://img.shields.io/badge/Loader-NeoForge-E87B35?style=flat-square" alt="NeoForge">
  <img src="https://img.shields.io/badge/License-Aryston%20Custom-3C8527?style=flat-square" alt="License">
</p>

---

## Overview

**Arkea** reimagines Minecraft's user interface from the ground up.

Every menu is modernized with fluid entrance animations, smooth sliders, and unified theme colors. In-game, Arkea provides customizable HUD styles with animated health and hunger meters, item feedback feeds, and comprehensive inspection tools. Furthermore, Arkea functions as an open UI library: any mod using NeoForge configuration automatically receives a consistent Arkea settings window.

## Key Highlights

- 🌟 **Redesigned Menus:** Title screen with "Jump back in" world cards, comprehensive singleplayer world management with backups, multiplayer server browser, and responsive loading screens.
- ⚙️ **Unified Options Window:** Fluid sidebar navigation, global search across all options, conflict-free keybinding management, and live preset switching.
- 🎬 **Custom Video & GIF Backgrounds:** Replace the vanilla rotating panorama with your own MP4, MOV, GIF, or image backgrounds with zero gameplay performance cost.
- 🎯 **Four Customizable HUD Styles:**
  - **Vanilla:** Untouched classic HUD.
  - **Compact:** Sleek corner status bars with numeric indicators.
  - **Classic+:** Vanilla icons placed on translucent modern plates.
  - **Minimal:** Subtle status bars that fade away when full.
- ⚡ **Integrated In-Game QoL:**
  - Target info card (block/mob inspection, harvestability, crop growth).
  - Food restoration & saturation preview.
  - Item pickup feed and low durability alerts.
  - Directional damage arcs and death point compass.
  - Interactive item tooltips (durability bars & shulker grid previews).
- ⌨️ **Command Palette (Ctrl + K):** Jump to any setting, screen, or recent world instantly.
- 🧩 **Auto-Theming for Every Mod:** Every mod using NeoForge configuration automatically gains an Arkea settings window.
- 💬 **Arkea Chat & Tooltips:** Player heads next to chat messages, food and durability details in tooltips, and container contents as a grid.
- ⏸️ **Pause, Death & Statistics Screens:** A pause menu with a world card, a cinematic death screen and a sortable statistics window.

For the complete list of player features, see the [Arkea Feature Guide](docs/FEATURES.md).

## Requirements

| Requirement | Supported Version |
|---|---|
| **Minecraft** | 26.3 |
| **Loader** | NeoForge 26.3.0.51-beta or newer |
| **Java** | 25 |
| **Side** | Client only (safe to join any multiplayer server) |

## For Mod Developers

Arkea provides a reactive UI component library (buttons, switches, dropdowns, steppers, color pickers, before/after previews, and dialogs) for mod authors.

To use Arkea in your mod:

```groovy
repositories {
    maven { url = uri("https://raw.githubusercontent.com/arystonmc/Arkea/maven") }
}

dependencies {
    implementation "com.aryston.arkea:arkea-neoforge-26.3:0.1"
}
```

Developer documentation and component guides are available in [docs/API.md](docs/API.md).

## Community & Contributing

- **Website:** [lureid.com/mods/arkea](https://lureid.com/mods/arkea) with the [wiki](https://lureid.com/wiki/arkea), [compatibility list](https://lureid.com/mods/arkea/compatibility) and [support](https://lureid.com/support?mod=arkea).
- Found a bug or have a suggestion? Read [SUPPORT.md](SUPPORT.md) or open an issue on our [Issue Tracker](https://github.com/arystonmc/Arkea/issues).
- Want to compile or contribute code? Read [CONTRIBUTING.md](CONTRIBUTING.md).

## License

This project is licensed under the [Aryston Source-Available License](LICENSE.md).  
Third-party notices and component licenses are detailed in [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md).

---

*Not an official Minecraft product. Not approved by or associated with Mojang or Microsoft.*
