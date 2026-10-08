# Arkea Changelog

All notable changes to Arkea. Each version section is ready to paste as an update note.

Format: sections `Added`, `Changed`, `Fixed`, `Removed`. One short plain English line per change.

## Unreleased

### Added
- Initial mod setup for Minecraft 26.3 on NeoForge
- Add an animated title screen with a jump back in card for the last played world
- Add an animated options window with a sidebar, quick settings and a tile for every page
- Add video, sound, chat, accessibility, skin and controls pages with switches, sliders and cycle selectors
- Add Reset to Defaults with a confirmation on every options page
- Add key binds, language and resource packs pages with search
- Add key bind filters, find by key and false conflict filtering for debug and spectator keys
- Add dropdown menus for the fullscreen resolution and the sound device
- Add custom menu backgrounds: import a video, GIF or picture to replace the rotating panorama
- Add an Arkea page to the options with the background gallery, accent colors and screen switches
- Add a singleplayer screen with world details, search, sorting, backups and safer deleting
- Add a create world screen with Game, World and More tabs
- Add a multiplayer screen with server cards, ping bars, LAN games and server dialogs
- Add connecting and loading screens with steps, progress and tips
- Add a mods screen with details, settings and links for every mod
- Add notifications for backups, deleting, renaming and copying
- Add a Ctrl+K command palette for settings, pages, worlds, servers and actions
- Show advancement, recipe, tutorial and system notifications in the Arkea style
- Add a Reconnect button after losing a server connection
- Show the player list when hovering a server
- Add radio buttons, steppers, chips, range sliders, ticked sliders, a color picker, pagination and preset tiles
- Add context menus, side sheets, before and after previews, what's new and progress dialogs
- Add collapsible sections, item slots, meters, cost pips, step dots and dividers
- Add a config screen library other mods can use to build Arkea settings windows
- Show the settings of every mod in the Arkea style
- Add a Component Gallery for mod developers
- Add a reset button and badges to setting rows
- Add Arkea pause, death and statistics screens
- Draw every other screen and other mods' screens with Arkea buttons, sliders, fields, lists and backgrounds
- Show confirmations, link warnings, alerts and backup prompts as Arkea dialogs
- Show the F3 debug information as Arkea cards and draw the chat bar in the Arkea style
- Add Arkea HUD styles (Compact, Classic+ and Minimal) with a settings page and live previews
- Show the Tab player list as an Arkea card with heads, scores and ping bars
- Animate the Arkea HUD: sliding selection, rolling stack counts, easing bars with a damage trace and a level up pop
- Add a target card, pickup feed, inventory total, ammo counter, low durability warnings and armor durability
- Add a food preview in the hunger bar, damage direction arcs, a death point, a location chip and a sleep reminder
- Add crosshair shapes, a hit marker and a sound radar
- Show tooltips as Arkea cards with food values, durability and container previews
- Draw chat messages on Arkea plates, a little smaller, narrower and above the HUD
- Show the sender's head and a colored name in front of every chat message, and slide new messages in
- Draw command help and command suggestions as Arkea panels

### Changed
- Adopt the Aryston Source-Available License

### Fixed
- Keep the target card in the top left so boss bars no longer push it down
- Keep key bindings with Ctrl, Shift or Alt after a restart
- Import videos dropped into the backgrounds folder while the game is running, once they finish copying
- Import videos about ten times faster, using every processor core
- Show the Arkea and Helion logos in the options sidebar
- Keep a cancelled background import from starting again by itself
- Stop the game from crashing when the HUD settings or the gallery are opened before any world was loaded
- Size the Arkea HUD for the window so it fits a 1920 x 1080 screen at any GUI scale
- Make the F3 cards smaller and hide the location chip and target card behind them
- Show the HUD larger in the settings previews and keep the Vanilla preview from being squashed
- Keep crisp chat text and the chat above the taller Classic+ HUD
