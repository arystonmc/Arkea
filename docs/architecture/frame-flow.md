# Arkea Architecture: Frame Flow

Part of the [Arkea architecture](../ARCHITECTURE.md).

## Frame Flow

1. `Screen.extractRenderStateWithTooltipAndSubtitles` calls `extractBackground` (the title screen draws the vanilla panorama there), opens a new stratum and calls `ArkScreen.extractRenderState`.
2. `ArkScreen` starts a pending navigation when the exit animation has ended, otherwise builds a `UiGraphics` for the frame, pushes the design scale and calls `renderUi` of the screen with the mouse in canvas coordinates (outside the canvas while a dialog is open or the screen is leaving).
3. Widgets render themselves, store where their bounds landed on the canvas (`UiGraphics.canvasBox`) and update hover from that box, so hover and clicks follow enter animations.
4. The tooltip of the hovered widget renders on top.
5. An open dialog renders on the next stratum. The menu background blur is requested for that stratum while the dialog is shown. Window screens blur their background instead (`blursBackground`), and the dialog then uses that blur.
