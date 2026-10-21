# Arkea Code Map: `com.aryston.arkea.api.config`

Part of the [Arkea code map](../CODEMAP.md). Every class of this package has its entry here and nowhere else (rule D1).

### `com.aryston.arkea.api.config`

Public library API. Other mods build Arkea screens with these classes; see `docs/API.md`.

#### ArkeaConfigScreen
- Path: `src/main/java/com/aryston/arkea/api/config/ArkeaConfigScreen.java`
- Role: Entry point of the config screen API: a builder with brand, sidebar groups, pages, apply mode, apply listeners and a sidebar meter; `build` returns the screen, `factory` an `IConfigScreenFactory`.
- Members: `builder`, `page`, `Builder.brand`, `group`, `page`, `applyMode`, `onApply`, `meter`, `build`, `factory`, `Definition`, `Group`.

#### ConfigPage
- Path: `src/main/java/com/aryston/arkea/api/config/ConfigPage.java`
- Role: A sidebar page: sections, presets and an optional notice.

#### ConfigSection
- Path: `src/main/java/com/aryston/arkea/api/config/ConfigSection.java`
- Role: A titled group of options, half or full width, optionally collapsible.

#### ConfigOption
- Path: `src/main/java/com/aryston/arkea/api/config/ConfigOption.java`
- Role: One setting: id, `Binding`, kind and how it looks (name, description, icon, tooltip, tags, cost, restart, requirement, before and after preview, choice style, value labels).
- Members: `toggle`, `choice`, `enumChoice`, `slider` (int and double), `stepper`, `text`, `color`, `multi`, `action`.

#### OptionKind
- Path: `src/main/java/com/aryston/arkea/api/config/OptionKind.java`
- Role: Sealed kinds of option values: toggle, choice, int range, double range, text, color, multi-select and action.

#### ChoiceStyle
- Path: `src/main/java/com/aryston/arkea/api/config/ChoiceStyle.java`
- Role: How a choice is shown: automatic (cycle up to six values, dropdown above), cycle, dropdown or segmented.

#### Binding
- Path: `src/main/java/com/aryston/arkea/api/config/Binding.java`
- Role: Where an option reads and writes its value; `of(ConfigValue)` binds a `ModConfigSpec` value (saved and restart aware), `of(getter, setter, default)` anything else.

#### SpecBinding
- Path: `src/main/java/com/aryston/arkea/api/config/SpecBinding.java`
- Role: `Binding` of a `ModConfigSpec.ConfigValue`: raw value, default, `save` and the restart type of the spec.

#### ConfigPreset
- Path: `src/main/java/com/aryston/arkea/api/config/ConfigPreset.java`
- Role: A named set of option values shown as a preset tile; active while every value matches.

#### ConfigMeter
- Path: `src/main/java/com/aryston/arkea/api/config/ConfigMeter.java`
- Role: Sidebar meter (label, value, fraction and note) computed from the values shown on screen, such as an estimated GPU impact.

#### ConfigValues
- Path: `src/main/java/com/aryston/arkea/api/config/ConfigValues.java`
- Role: Read access to the values on screen, including changes that are not applied yet.

#### ApplyMode
- Path: `src/main/java/com/aryston/arkea/api/config/ApplyMode.java`
- Role: `ON_APPLY` keeps changes until Apply (default), `IMMEDIATE` writes and saves every change at once.

#### SpecOptions
- Path: `src/main/java/com/aryston/arkea/api/config/SpecOptions.java`
- Role: Turns a `ModConfigSpec.ConfigValue` into an option: switch, choice (`TranslatableEnum` names), slider or stepper by range, decimal slider, text for strings, longs, unbounded decimals and lists; names and descriptions from the translation key or comment.

#### ModConfigScreens
- Path: `src/main/java/com/aryston/arkea/api/config/ModConfigScreens.java`
- Role: Builds an Arkea screen for every `ModConfigSpec` of a mod: one sidebar group per config type, a page per top level section, sections per nested group. Unloaded configs and server configs that cannot be edited show a notice.
- Members: `forMod`, `factory`, `pages`, `hasEditableConfig`.
