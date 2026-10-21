# Arkea Code Map: `com.aryston.arkea.ui.widget`

Part of the [Arkea code map](../CODEMAP.md). Every class of this package has its entry here and nowhere else (rule D1).

### `com.aryston.arkea.ui.widget`

#### UiHost
- Path: `src/main/java/com/aryston/arkea/ui/widget/UiHost.java`
- Role: What a widget needs from its screen: scale, text metrics, time and whether keyboard focus is shown.

#### ArkWidget
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkWidget.java`
- Role: Base of every interactive element. Implements vanilla `GuiEventListener` and `NarratableEntry`, so Tab and arrow navigation and narration work like vanilla. Tracks hover and press transitions, takes its hit box from where it was drawn (animations included), clicks on release over the widget, activates on Enter or Space when focused, draws the accent focus ring for keyboard focus, sets the pointer cursor, plays the click sound and carries an optional tooltip. Applies the press offset and the horizontal content shift of a subclass (`contentShiftX`) around both the content and the focus ring, and widens the hit box by `contentReachX` so a sliding widget stays hoverable. The hit box is the drawn box cut to the current clip, so rows scrolled out of view cannot be clicked; `localX` maps a canvas position back into the widget layout.

#### ArkButton
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkButton.java`
- Role: Standard 32 high button with label and optional leading and trailing icons; drawn by `ButtonPainter`.

#### ButtonPainter
- Path: `src/main/java/com/aryston/arkea/ui/widget/ButtonPainter.java`
- Role: Draws a button of any variant (fill, border, primary glow, hover brightness, disabled fade, label and icons, pixel icons keep their aspect) and measures its width. Shared by `ArkButton` and the loading screens, which paint vanilla buttons in the Arkea style.

#### ArkCheckbox
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkCheckbox.java`
- Role: 16 pixel checkbox with a label: accent fill and pixel check when on, accent border on hover, fills in 200 ms.

#### ArkTab
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkTab.java`
- Role: Tab of a tab row: optional icon, label, optional count badge and a 2 pixel accent underline that fades in on the selected tab.

#### ListCycleModel
- Path: `src/main/java/com/aryston/arkea/ui/widget/ListCycleModel.java`
- Role: `CycleModel` over a list of values with a getter, setter and label function (difficulties, world presets).

#### Tag
- Path: `src/main/java/com/aryston/arkea/ui/widget/Tag.java`
- Role: 12 high badge with 8 pixel spaced caps on a tone tint (HARDCORE, CHEATS, OUTDATED, CONFIG); measures and draws itself next to a name.

#### ButtonVariant
- Path: `src/main/java/com/aryston/arkea/ui/widget/ButtonVariant.java`
- Role: Fill, border, text and glow colors of primary, secondary, subtle, ghost and danger buttons.

#### ArkIconButton
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkIconButton.java`
- Role: Icon only button with hover fill and icon color blends; icons may be non-square (pixel chevrons).

#### IconButtonStyle
- Path: `src/main/java/com/aryston/arkea/ui/widget/IconButtonStyle.java`
- Role: Fill and hover fill, border, icon colors and icon size of an icon button: `BACK` and `CLOSE` of the window header, `brightening` for buttons that brighten on hover.

#### ArkMenuButton
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkMenuButton.java`
- Role: Large menu entry that slides right on hover: icon tile, label, description and chevron, or an inline icon and label.

#### MenuButtonStyle
- Path: `src/main/java/com/aryston/arkea/ui/widget/MenuButtonStyle.java`
- Role: Colors, sizes and layout of menu buttons: `primary`, `secondary`, `dangerInline`.

#### ItemContent
- Path: `src/main/java/com/aryston/arkea/ui/widget/ItemContent.java`
- Role: Icon, label and optional description of a menu button, tile or setting row.

#### MenuButtonLayout
- Path: `src/main/java/com/aryston/arkea/ui/widget/MenuButtonLayout.java`
- Role: Tile or inline content layout.

#### ArkTextLink
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkTextLink.java`
- Role: Clickable text that turns white and underlines on hover.

#### ArkSlider
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkSlider.java`
- Role: Slider: track, accent fill with glow, handle and value label as wide as the widest end label. Click or drag sets the value, arrow keys step and Shift steps ten times, the fill eases on changes that do not come from dragging. Narrates like a vanilla slider.
- Depends on: `SliderModel`.

#### SliderModel
- Path: `src/main/java/com/aryston/arkea/ui/widget/SliderModel.java`
- Role: What a slider controls: position as a fraction, drag, release, keyboard step and value labels.

#### IntSliderModel
- Path: `src/main/java/com/aryston/arkea/ui/widget/IntSliderModel.java`
- Role: `SliderModel` for an integer range with a getter, setter and label (field of view on the overview).

#### SliderRange
- Path: `src/main/java/com/aryston/arkea/ui/widget/SliderRange.java`
- Role: Minimum, maximum and step of a slider with fraction and snapping math.

#### ArkSwitch
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkSwitch.java`
- Role: On/off switch with its label: 32 x 18 track that fades to the accent in 180 ms and a square knob that springs across in 200 ms. Click, Enter or Space toggles.

#### ArkCycle
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkCycle.java`
- Role: 160 x 30 cycle selector: previous and next arrows with their own hover, the value sliding in from the side of the pressed arrow, and a progress dot per value (a bar above 16 values). Left and right keys step, Enter goes forward and Shift+Enter back; it wraps around.
- Depends on: `CycleModel`.

#### CycleModel
- Path: `src/main/java/com/aryston/arkea/ui/widget/CycleModel.java`
- Role: Value count, current index, selection and labels of a cycle selector.

#### ArkDropdown
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkDropdown.java`
- Role: 160 x 30 dropdown: value and chevron, accent border while open; opens a `DropdownMenu` on the screen's popup layer.
- Depends on: `CycleModel`, `DropdownMenu`.

#### ArkTextField
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkTextField.java`
- Role: 30 high text field with search icon, placeholder, blinking caret, select all, clear button, clipboard, word jumps; Escape clears it. Tells the game that text input starts and where the caret is, which 26.3 needs before it delivers typed characters and places the IME window.
- Depends on: `TextFieldState`.

#### TextFieldState
- Path: `src/main/java/com/aryston/arkea/ui/widget/TextFieldState.java`
- Role: Text, caret and select-all of a text field, kept by the screen so typing survives a rebuild.

#### ArkSegmented
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkSegmented.java`
- Role: Segmented control of the design (selected segment in the accent with glow); click, left and right keys choose a segment.

#### ArkKeycap
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkKeycap.java`
- Role: 140 x 30 key binding button: the bound key, a pulsing "Press a key..." state while listening, red conflict and faint unbound states; narrates like the vanilla key binding buttons.

#### ArkChoiceTile
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkChoiceTile.java`
- Role: 48 high selectable tile with name, second line and a check when selected; optional double click action.

#### ArkBanner
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkBanner.java`
- Role: Clickable accent banner with a preview image, logo, title, subtitle and an action button with an external arrow (the "Helion is installed" banner of the video page).

#### ArkSwatch
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkSwatch.java`
- Role: Row of color swatches with a white outline and a check on the chosen one; left and right arrows change the choice.

#### ArkNavItem
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkNavItem.java`
- Role: Sidebar entry: icon and label, accent fill with marker and glow when selected, hover fill, external link arrow.

#### NavEntry
- Path: `src/main/java/com/aryston/arkea/ui/widget/NavEntry.java`
- Role: Id, icon, optional logo texture (drawn instead of the icon), label and external flag of a sidebar entry.

#### ArkTile
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkTile.java`
- Role: 72 high link tile with accent icon box, name, description and chevron.

#### PanelRow
- Path: `src/main/java/com/aryston/arkea/ui/widget/PanelRow.java`
- Role: A row of the settings grid: height, placement, its widgets, drawing, hover tooltip and hiding when scrolled out of view.

#### WidgetRow
- Path: `src/main/java/com/aryston/arkea/ui/widget/WidgetRow.java`
- Role: A grid row that is one widget filling the cell (language tiles).

#### ToolbarRow
- Path: `src/main/java/com/aryston/arkea/ui/widget/ToolbarRow.java`
- Role: A frameless row of widgets aligned left or right (key bind filters and find button).

#### NoticeRow
- Path: `src/main/java/com/aryston/arkea/ui/widget/NoticeRow.java`
- Role: Tinted notice with a tone icon, text and an optional action button (key conflicts, empty search results).

#### SettingRow
- Path: `src/main/java/com/aryston/arkea/ui/widget/SettingRow.java`
- Role: Frame of a setting: icon box, name, description and hover; not focusable. `tags` and `cost` draw badges and cost pips after the name, `resettable` adds an undo button that shows while the value differs from its default, `accessory` adds other icon buttons (preview). `controlSlot` places the control widget on its right and keeps the text clear of it, `litWhen` turns the icon from gray to the accent while the setting is on, `tooltip` supplies the hover text of the whole row, and the content fades when the control is disabled. As a `PanelRow` it places and draws the control given to `control`.

#### ArkRadio
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkRadio.java`
- Role: Radio button with the square dot of the design that pops in (280 ms spring); selecting it runs the group callback.

#### ArkStepper
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkStepper.java`
- Role: Number stepper (140 x 30): minus, value, plus; arrow keys step (Shift for ten), holding a button repeats after 400 ms every 60 ms. Uses a `SliderModel`.

#### ArkChip
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkChip.java`
- Role: Chip of a multi-select (28 high): accent tint and pixel check when selected; `add` makes the dashed "+ Add" chip.

#### ChipsRow
- Path: `src/main/java/com/aryston/arkea/ui/widget/ChipsRow.java`
- Role: Setting row with a name, description and wrapping chips below; its height follows the width (`PanelRow.height(width)`).

#### ArkRangeSlider
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkRangeSlider.java`
- Role: Slider with two handles for a range; the handle closest to the click moves, Enter switches the handle for the arrow keys.

#### RangeModel, IntRangeModel
- Path: `src/main/java/com/aryston/arkea/ui/widget/RangeModel.java`
- Role: Values of a range slider; `IntRangeModel` keeps two integers in a `SliderRange` and the low value below the high one. Path of the second file: `src/main/java/com/aryston/arkea/ui/widget/IntRangeModel.java`.

#### DoubleSliderModel
- Path: `src/main/java/com/aryston/arkea/ui/widget/DoubleSliderModel.java`
- Role: `SliderModel` over a decimal value with min, max and step; ticks for five or fewer steps.

#### ArkColorButton
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkColorButton.java`
- Role: Row control of a color (140 x 30, swatch and hex) that opens the `ColorPickerPopup`.

#### ArkDisclosure
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkDisclosure.java`
- Role: Header of a collapsible section (40 high): rotating chevron, label and a count or tag on the right.

#### ArkPagination
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkPagination.java`
- Role: Previous and next arrows around "page / pages"; arrow keys work while focused.

#### ArkPresetTile
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkPresetTile.java`
- Role: Preset tile (84 high): icon box, title and description, lifts 2 pixels on hover, accent frame, glow and check badge when selected.

#### ArkCompareView
- Path: `src/main/java/com/aryston/arkea/ui/widget/ArkCompareView.java`
- Role: Before and after images with a draggable split handle and Before, Split and After modes (the split animates 450 ms); used by preview dialogs.
