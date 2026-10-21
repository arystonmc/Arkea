# Arkea Code Map: `com.aryston.arkea.screen.options.control`

Part of the [Arkea code map](../CODEMAP.md). Every class of this package has its entry here and nowhere else (rule D1).

### `com.aryston.arkea.screen.options.control`

#### OptionControls
- Path: `src/main/java/com/aryston/arkea/screen/options/control/OptionControls.java`
- Role: Picks the control of a vanilla `OptionInstance`: a switch for on/off booleans, a cycle selector for enums, cyclable ranges and other booleans (Hold/Toggle), a slider for sliderable values; reads the default for resets.

#### OptionControl
- Path: `src/main/java/com/aryston/arkea/screen/options/control/OptionControl.java`
- Role: Widget, size, icon state and reset action of one option.

#### OptionSliderModel
- Path: `src/main/java/com/aryston/arkea/screen/options/control/OptionSliderModel.java`
- Role: `SliderModel` over a sliderable option; options that apply late (render distance) apply on release, or 600 ms after the last key step, like vanilla.

#### OptionCycleModel
- Path: `src/main/java/com/aryston/arkea/screen/options/control/OptionCycleModel.java`
- Role: `CycleModel` over the value list of an option.

#### OptionText
- Path: `src/main/java/com/aryston/arkea/screen/options/control/OptionText.java`
- Role: Turns vanilla "Caption: value" labels into the value alone, reads the vanilla tooltip of the current value, and finds the short Arkea description (`arkea.option.*`) and name overrides (`.name`).
