# Arkea Architecture: Input, Focus and Narration

Part of the [Arkea architecture](../ARCHITECTURE.md).

## Input, Focus and Narration

- `ArkWidget` implements vanilla `GuiEventListener` and `NarratableEntry` and is registered with `Screen.addWidget`. Vanilla handles Tab, Shift+Tab, arrows and narration; widgets activate on Enter and Space when focused.
- A click activates on release over the widget, like a browser. The container forwards the release to the focused widget.
- The focus ring shows only when the last input came from the keyboard.
- While a dialog is open, `children()` returns only the dialog buttons, Escape cancels the dialog and narration reads the dialog title, body and focused button.
