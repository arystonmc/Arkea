# Arkea Architecture: Testing Tools

Part of the [Arkea architecture](../ARCHITECTURE.md).

## Testing Tools

- `docs/API.md` is the developer guide; the Component Gallery (`ArkGalleryScreen`) shows every component and is part of the interface check (`gallery`).

- Unit tests cover motion, scaling, the path parser, shadow profiles, dates, language files and background import (image, GIF and video conversion).
- `-Define arkea.uiCheck=all` (or groups such as `worlds,servers,loading`) opens every Arkea screen and popup, saves `screenshots/arkea_<step>.png` and closes the game; run it in a 1366 x 768 window to compare with the design at one to one.
- A file dropped into `run/arkea/backgrounds` is imported within about two seconds while the Arkea page is open, which tests imports without a file picker (the SDL picker needs a desktop portal on Linux, so it reports "unavailable" headless).
- In a cloud session the client runs headless: `Xvfb :99`, Mesa `mesa-vulkan-drivers` (llvmpipe Vulkan), `DISPLAY=:99 ./gradlew runClient`. `xdotool` moves the mouse, clicks and types, `import -window root` takes screenshots, so hover, focus, dialogs and screen switches can be checked frame by frame.
