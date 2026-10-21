# Arkea Code Map: `com.aryston.arkea.ui.overlay`

Part of the [Arkea code map](../CODEMAP.md). Every class of this package has its entry here and nowhere else (rule D1).

### `com.aryston.arkea.ui.overlay`

#### ArkDialog
- Path: `src/main/java/com/aryston/arkea/ui/overlay/ArkDialog.java`
- Role: Modal popup: scrim, tone icon, title, wrapped body, optional custom content and right aligned buttons, with the pop enter and exit animations. Can refuse to close on a scrim click for destructive actions. A `hero` image fills the top 110 pixels and fades into the dialog (delete world), a `thumbnail` replaces the tone icon (edit world).
- Members: `WIDTH_SMALL`, `WIDTH_MEDIUM`, `WIDTH_LARGE`, `button`, `keepOpenOnScrimClick`, `hero`, `thumbnail`, `content`, `open`, `close`, `cancel`, `layout`, `render`, `widgets` (content widgets and buttons, for focus and input).

#### DialogBody
- Path: `src/main/java/com/aryston/arkea/ui/overlay/DialogBody.java`
- Role: Custom content of a dialog: its height for a width, layout, drawing and widgets.

#### DialogForm
- Path: `src/main/java/com/aryston/arkea/ui/overlay/DialogForm.java`
- Role: `DialogBody` built from rows: caps field labels, full width fields, equal columns of buttons, a label with a control on the right, and a message line that appears when its supplier returns text (validation errors).

#### DialogImage
- Path: `src/main/java/com/aryston/arkea/ui/overlay/DialogImage.java`
- Role: Something that draws itself into a box, for dialog hero images and thumbnails.

#### GameToast, GameToastIcon
- Path: `src/main/java/com/aryston/arkea/ui/overlay/GameToast.java`, `src/main/java/com/aryston/arkea/ui/overlay/GameToastIcon.java`
- Role: In-game toast of the design (300 x 56 or larger, icon tile 40, yellow title, optional progress) drawn inside a vanilla toast slot; icons for items and tones.

#### ArkToasts
- Path: `src/main/java/com/aryston/arkea/ui/overlay/ArkToasts.java`
- Role: The menu toasts of the design: a stack at the bottom center of every Arkea screen, 40 high with a 38 wide tone column, at most three, newest at the bottom, enter with a 16 pixel rise and scale 0.95 (320 ms), leave with 12 pixels (200 ms), stay 2.4 s; errors, progress and busy toasts stay until finished. Optional action chip (Undo, Show), percentage with a 2 pixel bar, or a moving bar while busy. A click dismisses a toast and runs its action. Toasts survive screen switches.
- Members: `show`, `progress`, `busy`, `click`, `contains`, `render`.

#### MenuToast
- Path: `src/main/java/com/aryston/arkea/ui/overlay/MenuToast.java`
- Role: One toast: tone, message, optional action, progress or busy state, its presence and stack lift animations; `finish` turns a running toast into a result.

#### ToastTone
- Path: `src/main/java/com/aryston/arkea/ui/overlay/ToastTone.java`
- Role: Success (accent light), info, warning and error, each with its icon and color.

#### DialogContent
- Path: `src/main/java/com/aryston/arkea/ui/overlay/DialogContent.java`
- Role: Title, body, icon and tone color of a dialog.

#### ArkTooltip
- Path: `src/main/java/com/aryston/arkea/ui/overlay/ArkTooltip.java`
- Role: Tooltip of the hovered widget or row after the design delay, wrapped to 280 pixels, kept on screen, with fade in and out.

#### Popup
- Path: `src/main/java/com/aryston/arkea/ui/overlay/Popup.java`
- Role: Something drawn above the screen content that takes clicks, drags, wheel, keys and typed characters first while open (dropdown menus, context menus, the color picker).

#### DropdownMenu
- Path: `src/main/java/com/aryston/arkea/ui/overlay/DropdownMenu.java`
- Role: Menu of a dropdown: opens below the control (above when there is no room) with a 180 ms grow, rows of 28 with hover, check on the selected value, up to eight rows with wheel scrolling, arrow keys, Enter and Escape.

#### TooltipHint
- Path: `src/main/java/com/aryston/arkea/ui/overlay/TooltipHint.java`
- Role: Owner and text of a tooltip; a new owner restarts the delay.

#### Overlay
- Path: `src/main/java/com/aryston/arkea/ui/overlay/Overlay.java`
- Role: Modal layer of an `ArkScreen` (dialogs and side sheets): layout, open, close, cancel, widgets and drawing.

#### ArkSideSheet
- Path: `src/main/java/com/aryston/arkea/ui/overlay/ArkSideSheet.java`
- Role: Side sheet of the design: 420 wide panel on the right with title, subtitle, close button, optional body and footer buttons; slides 24 pixels with a fade over a scrim.

#### ArkContextMenu, ContextMenuItem
- Path: `src/main/java/com/aryston/arkea/ui/overlay/ArkContextMenu.java`
- Role: Context menu at the cursor (260 wide, rows 32, separators, right aligned shortcuts, disabled and danger items) with a 140 ms drop-in and 120 ms fade; arrow keys and Enter work. Path of the item record: `src/main/java/com/aryston/arkea/ui/overlay/ContextMenuItem.java`.

#### ColorPickerPopup
- Path: `src/main/java/com/aryston/arkea/ui/overlay/ColorPickerPopup.java`
- Role: Color picker of the design: saturation and value field, hue bar, swatch with editable hex and six Minecraft friendly presets; drag and type input come through `Popup`.

#### DialogList
- Path: `src/main/java/com/aryston/arkea/ui/overlay/DialogList.java`
- Role: Dialog body with rows of an icon, title, detail and tag: what's new, change reviews and profile lists.

#### DialogProgress
- Path: `src/main/java/com/aryston/arkea/ui/overlay/DialogProgress.java`
- Role: Dialog body with a step label, percentage and an eased progress bar.
