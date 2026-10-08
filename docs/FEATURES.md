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
- Switches for the Arkea title screen, options window and menu screens
- Since: 0.1

### Singleplayer
Your worlds at a glance, with everything you need to look after them.

- Every world as a card with its picture, game mode, difficulty, version and when you last played, plus tags for hardcore, cheats and older or newer versions
- A details panel shows play time and folder size of the selected world
- Search by name, and sort by last played, name or game mode (remembered)
- Double-click or press Enter to play, Delete to remove a world, F5 to reload
- Delete asks first and can make a backup before deleting
- Edit renames the world, makes a backup, opens its folder or the backups folder, optimizes it or resets its icon; long tasks show their progress in a notification
- Since: 0.1

### Create New World
All world settings on three clear tabs.

- Game: name, game mode, difficulty and commands
- World: world type, seed, structures, bonus chest and the superflat editor
- More: game rules, data packs and experiments
- Since: 0.1

### Multiplayer
Servers and LAN games in one window.

- Server cards with icon, colored message of the day, player count and ping bars; offline and outdated servers stand out
- Move servers up and down (or Shift and the arrow keys), copy an address, edit or remove a server right on its card
- Add Server and Direct Connect check the address before you join
- The LAN tab scans your network and lists open worlds
- Search servers by name or address, refresh with F5
- Hover a server to see who is online, and reconnect with one click after losing the connection
- Since: 0.1

### Loading Screens
Connecting and loading show what is happening.

- Server connections list each step (connecting, logging in, joining, loading terrain) with a progress bar
- World loading, saving, portal travel and other waits share the same look, with a tip at the bottom
- A lost connection shows the reason clearly with one button back
- Since: 0.1

### Mods
A clean list of every installed mod.

- Icons, versions and tags for mods with settings or an available update
- Details with authors, mod id, license, credits and description
- Open a mod's settings, its homepage or its issue tracker, or the mods folder
- Since: 0.1

### Notifications
Short messages at the bottom of every Arkea screen.

- Success, info, warning and error notifications; errors stay until you click them
- Long tasks such as backups show a moving bar and finish with the result
- Some notifications offer an action, such as showing the new backup
- Advancement, recipe, tutorial and system notifications in the top right corner use the same look (turn it off on the Arkea page)
- Since: 0.1

### Command Palette
Jump anywhere with the keyboard.

- Press Ctrl+K in any menu and type: every setting, options page, recent world, server, folder and accent color is one Enter away
- Choosing a setting opens its page and scrolls to it
- Since: 0.1
