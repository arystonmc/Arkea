# Arkea Features

What the latest version of Arkea can do. Written for players and ready to reuse on websites and mod pages. This file always describes the present state; history belongs in `CHANGELOG.md`.

## Summary

Arkea is a client-side interface library for Minecraft mods. It gives settings screens one consistent, animated look with switches, sliders, dropdowns, presets, search and previews, so players configure every Aryston mod the same way.

## At a Glance

| | |
|---|---|
| Version | 0.1 |
| Minecraft | 26.3 |
| Loader | NeoForge |
| Side | Client only, safe to join any server |
| Status | In development |

## Features

### Animated Title Screen
A calm, modern main menu that keeps everything the vanilla menu can do.

- Large menu entries for Singleplayer, Multiplayer, Realms, Mods and Options with short descriptions; they slide in when the menu opens and lean toward the cursor on hover
- A "Jump back in" card shows your most recently played world with its picture, game mode and when you last played it; one click starts it
- The splash text pulses next to the title; click it for a new one
- Quit asks once before closing the game, and Language, Accessibility and Friends sit one click away
- Works with mouse, keyboard (Tab, arrows, Enter, Escape) and the narrator
- Turn it off on the Arkea page of the options (or in Mods, Arkea, Config) to get the vanilla main menu back
- Since: 0.1

### Options Window
All settings in one calm window instead of a wall of buttons.

- A sidebar groups every settings page into Game, Player and Content; Helion settings appear there when Helion is installed
- Field of View has a smooth slider right on the overview; drag it, click it or use the arrow keys (Shift for bigger steps)
- Every settings page has its own tile with a short description, and Telemetry and Credits stay one click away
- Video, Sound, Chat, Accessibility, Skin and Controls are redesigned too: every option has a short description, a switch, slider or selector, and the full explanation on hover
- Switch pages from the sidebar without the window closing; long pages scroll with the wheel, the scrollbar or the keyboard
- Reset to Defaults puts a page back after asking once
- Key Binds: search by action or key, filters for changed, conflicting and unbound keys, "Find by key" to see everything a key does, Ctrl, Shift and Alt combinations and mouse buttons, reset one key or all; the debug and spectator keys of the game no longer show up as false conflicts
- Key combinations with Ctrl, Shift or Alt keep working after a restart (works around a NeoForge beta bug that forgets them)
- Language: every language in a searchable grid, accents ignored ("turk" finds Türkçe), plus the font options
- Resource Packs: add, order and remove packs with their icons, drop pack files to install them, and the game only reloads when something changed
- Dropdown menus for the fullscreen resolution and the sound device
- When Helion is installed, the video page points to its shader settings
- The window glides in over the blurred menu background or your world and glides out when you leave
- Works with mouse, keyboard and the narrator; changes save automatically
- Turn it off on the Arkea page of the options (or in Mods, Arkea, Config) to get the vanilla options menu back
- Since: 0.1

### Custom Menu Backgrounds
Replace the rotating panorama with your own video, GIF or picture.

- Import MP4, MOV, GIF, PNG or JPG from the Arkea page of the options, drop a file on the game window, or put it in the backgrounds folder (new files are picked up within a second while the Arkea page is open)
- Every background plays on the title screen and in every menu that normally shows the panorama
- Videos are converted once into a light frame sequence (up to 60 seconds, 720p, 30 fps) on every processor core, about ten seconds for a typical 1080p clip, so playback barely costs any performance; pictures can drift slowly so the menu never looks frozen
- A gallery shows every background with a preview, its length and size; pick one with a click, delete it, or go back to the vanilla panorama any time
- Since: 0.1

### Arkea Settings
Arkea has its own page in the options window, no need to go through the mod list.

- Menu background gallery and import
- Accent color for every Arkea screen: green, emerald, gold or stone
- Switches for the Arkea title screen and options window
- Since: 0.1
