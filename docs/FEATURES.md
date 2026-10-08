# Arkea Features

What the latest version of Arkea can do. Written for players and ready to reuse on websites and mod pages. This file always describes the present state; history belongs in `CHANGELOG.md`.

## Summary

Arkea is a client-side interface library for Minecraft mods. It gives settings screens one consistent, animated look with switches, sliders, dropdowns, presets, search and previews, so players configure every mod the same way, and other mods can build their own screens with it.

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
- Switches for the Arkea title screen, options window, menu screens, in-game screens and mod settings
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

### Settings for Every Mod
Every mod with settings opens them in the same Arkea window.

- Mods that use the standard NeoForge config get an Arkea window automatically: pages in the sidebar, the right control for every setting, search, a reset button per setting and Apply
- Changes wait for Apply; leaving with unapplied changes asks first, and pages with unapplied changes are marked
- Right-click a setting to reset it, copy its value or paste one
- Mods can design their own window with presets, previews, badges and a cost meter, like Helion
- Turn it off on the Arkea page of the options to get the classic NeoForge screens back
- Since: 0.1

### Interface Library for Mod Developers
Arkea is a library: other mods build their screens with the same components.

- Buttons, switches, checkboxes, radio buttons, cycle selectors, dropdowns, segmented controls, sliders with ticks, range sliders, steppers, text fields, color pickers, chips, tabs, pagination, preset tiles and setting rows
- Dialogs, side sheets, context menus, before and after previews, tooltips, notifications, banners, meters and item slots
- A Component Gallery on the Arkea page shows every component live
- Developer guide: `docs/API.md`
- Since: 0.1

### Pause, Death and Statistics
The screens you see while playing match the rest of Arkea.

- Pause menu with a big Back to Game button, tiles for every action (buttons added by other mods included), a red Save and Quit, and a card with the world picture, day, time, weather, game mode, difficulty, players and dimension
- A Helion Graphics button in the pause menu when Helion is installed
- Death screen with a red vignette, the cause and score fading in, and Respawn and Title Screen buttons
- Statistics window with General, Items and Mobs tabs, sortable item columns with item icons and kill bars for every mob
- Turn it off on the Arkea page of the options (In-Game Screens)
- Since: 0.1

### Arkea HUD
Players pick how health, hunger and the hotbar look while playing.

- Four styles on a new HUD page of the options, each with its own live preview card, and a large live preview of the chosen style
- Vanilla: the original HUD, untouched (default)
- Compact: health in the bottom left and hunger in the bottom right as slim, semi-transparent bars with numbers; armor and air as thin lines above them; the center keeps only a slim hotbar, the experience line and the item name
- Classic+: the vanilla hearts, food, armor and air icons on transparent plates in the same corners
- Minimal: like Compact, but the bars fade out while health, hunger and air are full
- Smooth motion: the selection slides and pops like a click when you switch items, new items pop in, stack counts roll up or down with a bounce of the icon, bars ease to their value and leave a fading trace of lost health or food, the level pops on level up and the item name rises in
- Low health pulses the health bar; poison, wither, frozen, absorption, saturation, the hunger effect, vehicle health and extra health rows are all shown
- Effect chips with time and level, slim boss bars, a transparent scoreboard card and a Tab player list card with heads, scores and ping bars, each with its own switch
- Plate transparency (0 to 80 percent) and HUD size (75 to 125 percent) sliders; the HUD is sized for the window, so it fits a 1920 x 1080 screen at any GUI scale and grows with larger screens
- HUD parts added by other mods stay where they are; spectator mode, the locator bar and the jump bar stay vanilla
- Since: 0.1

### HUD Features
Useful information on screen, in every HUD style (Vanilla included), each with its own switch on the HUD page.

- Target card in the spirit of Jade, in the top left under the location chip so boss bars never move it: what you look at with its icon, name and mod; the right tool and whether you can harvest it; crop growth; redstone power; breaking progress; hearts or a health bar, armor, baby, villager profession and level, horse speed and jump height
- Pickup feed: "+5 Iron Ingot" rows with icons on the right, merging repeats, plus experience
- Inventory total of the held stack above the selected slot, and an ammo counter next to the hotbar with a bow or crossbow
- Low durability warning: one notice and a pulsing slot when a tool or armor piece drops under ten percent
- Worn armor with durability bars above the health bar (Arkea styles)
- Food preview in the spirit of AppleSkin: while holding food, the hunger and saturation it would restore pulse in the hunger bar (Arkea styles)
- Damage direction arcs around the crosshair that keep pointing at the attacker
- Death point: the death coordinates on the death screen, then the distance and direction back for ten minutes
- Location chip with coordinates, direction, biome and time (off by default; coordinates hidden on servers with reduced debug info)
- Sleep reminder once per night (off by default)
- Crosshair shapes (thin cross, open cross, dot, circle) that stay readable on every background, and a hit marker (off by default)
- Sound radar: subtitles placed around the crosshair in the direction of the sound (off by default)
- Since: 0.1

### Arkea Tooltips
Item tooltips show more and look like Arkea.

- Tooltips are Arkea cards with an accent line; tooltips with their own item style keep it
- Food shows its hunger shanks and golden saturation gems
- Damaged tools and armor show a durability bar with remaining and maximum uses
- Shulker boxes and other containers show their items as a grid instead of a text list
- Since: 0.1

### Arkea Chat
A chat in the spirit of Chat Heads, where you see at a glance who wrote what.

- Every player message shows the 2D head of its sender and the name in its own color (your own name in the accent color, team colors are kept), set apart from the message by a "»"
- Server chat formatted by plugins gets the head too when the sender's name is in front of the message
- New messages slide in from the left and fade in while the older ones move up smoothly
- Dark Arkea plates with a line in the sender's color and a thin divider between messages; crisp text at about 85 percent of the vanilla size, at most 42 percent of the screen wide, above the HUD
- Links, hover and clicks work as in vanilla; the chat input, command help and command suggestions are Arkea panels with Arkea Style Everywhere on
- Turn it off on the HUD page of the options (Arkea Chat)
- Since: 0.1

### Debug Screen
F3 matches Arkea.

- F3 information grouped in compact cards with an accent edge, sized for the window so it stays small and readable on a 1920 x 1080 screen
- The location chip and the target card step aside while F3 is open
- Charts, the profiler and the chunk map stay vanilla
- Turn the debug cards off on the Arkea page of the options (Arkea Debug Screen)
- Since: 0.1

### Arkea Style Everywhere
Screens that Arkea does not redesign still look like Arkea.

- Buttons, sliders, text fields, checkboxes, lists, tabs, scrollbars and backgrounds of every other screen, from vanilla or from other mods, use the Arkea style and accent color
- Confirmations, link warnings, alerts, the online play warning and backup prompts open as Arkea dialogs
- Inventories and containers keep the vanilla look
- Turn it off on the Arkea page of the options
- Since: 0.1

### Command Palette
Jump anywhere with the keyboard.

- Press Ctrl+K in any menu and type: every setting, options page, recent world, server, folder and accent color is one Enter away
- Choosing a setting opens its page and scrolls to it
- Since: 0.1
