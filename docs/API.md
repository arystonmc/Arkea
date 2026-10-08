# Arkea API

How other mods use Arkea to build settings screens and their own interfaces. Arkea is a client-only NeoForge library for Minecraft 26.3.

## Add Arkea to Your Build

Arkea is published to a Maven repository on GitHub. Add it to `build.gradle`:

```groovy
repositories {
    maven {
        name = "Arkea"
        url = uri("https://raw.githubusercontent.com/lureidcom/Arkea/maven")
        content {
            includeGroup "com.aryston.arkea"
        }
    }
}

dependencies {
    implementation "com.aryston.arkea:arkea-neoforge-26.3:0.1"
}
```

Then declare Arkea in `neoforge.mods.toml`, so the game refuses to start without it:

```toml
[[dependencies.yourmod]]
    modId="arkea"
    type="required"
    versionRange="[0.1,)"
    ordering="AFTER"
    side="CLIENT"
```

Players install the Arkea jar from the [releases](https://github.com/lureidcom/Arkea/releases) next to your mod. Do not embed it with Jar-in-Jar; Arkea also replaces the vanilla menus and must exist only once.

## Option 1: Nothing to Do

With Arkea installed, every mod that uses `ModConfigSpec` and the NeoForge `ConfigurationScreen` gets an Arkea settings window automatically: one sidebar group per config type, one page per top level section, a section per nested group, the right control for every value type, search, reset buttons and Apply. Names come from the translation key of each value (`translation(...)` on the builder, or `<modid>.configuration.<path>`), descriptions from `<key>.tooltip` or the config comment. Players can turn this off on the Arkea page of the options.

To use the generated window as your config screen explicitly:

```java
container.registerExtensionPoint(IConfigScreenFactory.class, ModConfigScreens.factory());
```

## Option 2: Design the Window

`ArkeaConfigScreen` builds a window like the Helion settings: sidebar pages in groups, presets, sections, a meter in the sidebar and Apply.

```java
public static Screen create(Screen parent) {
    ConfigOption<Boolean> bloom = ConfigOption.toggle("mymod.config.bloom", Binding.of(MyConfig.BLOOM))
        .icon(Icons.SPARKLE)
        .description(Component.translatable("mymod.config.bloom.description"))
        .cost(1);
    ConfigOption<Double> intensity = ConfigOption.slider("mymod.config.intensity", Binding.of(MyConfig.INTENSITY), 0.0, 1.0, 0.05)
        .percent()
        .requires(bloom);
    ConfigOption<Quality> quality = ConfigOption.enumChoice("mymod.config.quality", Binding.of(MyConfig.QUALITY), Quality.class)
        .style(ChoiceStyle.SEGMENTED);

    return ArkeaConfigScreen.builder(Component.literal("My Mod"))
        .brand(MY_LOGO, Component.literal("MY MOD"), Component.literal("Graphics"))
        .group(Component.literal("CONFIGURATION"))
        .page("effects", Component.literal("Effects"), Icons.SPARKLE, page -> page
            .preset(new ConfigPreset(Component.literal("Fast"), Component.literal("Everything off"), Icons.BOLT).set(bloom, false))
            .section(Component.literal("BLOOM"), section -> section.add(bloom).add(intensity))
            .section(Component.literal("QUALITY"), section -> section.add(quality)))
        .build(parent);
}
```

Register it in your mod constructor:

```java
container.registerExtensionPoint(IConfigScreenFactory.class, (mod, parent) -> MyConfigScreen.create(parent));
```

### Options

| Factory | Control |
|---|---|
| `ConfigOption.toggle` | Switch |
| `ConfigOption.choice`, `enumChoice` | Cycle selector (up to six values), dropdown (more) or `style(ChoiceStyle.SEGMENTED)` |
| `ConfigOption.slider` (int or double) | Slider with value label; ticks for five or fewer steps; `percent()` and `format(...)` change the label |
| `ConfigOption.stepper` | Minus and plus stepper |
| `ConfigOption.text` | Text field; `validate(...)` decides which input is stored |
| `ConfigOption.color` | Color button with a picker (RGB as an int) |
| `ConfigOption.multi` | Chips for picking several values of a list |
| `ConfigOption.action` | Button that runs an action |

Every option can have `name`, `description`, `icon`, `tooltip`, `tag` (badges such as `Tag.beta(...)`), `cost` (1 to 3 pips), `requiresRestart`, `requires` (disabled with a "Requires" tooltip until all given switches are on) and `preview` (before and after images, opened from an eye button and the context menu).

### Bindings

`Binding.of(ModConfigSpec.ConfigValue)` reads and writes a NeoForge config value, saves the config file on Apply and marks values that need a restart. `Binding.of(getter, setter, default)` binds anything else.

### Window Behavior

- `applyMode(ApplyMode.ON_APPLY)` (default): changes wait for Apply, the sidebar marks pages with unapplied changes, Discard drops them and leaving asks first. `ApplyMode.IMMEDIATE` saves every change at once.
- `onApply(...)` runs after values are written.
- `meter(new ConfigMeter(...))` shows a meter at the bottom of the sidebar, computed from the values on screen (`ConfigValues.value(option)` includes unapplied changes).
- `ModConfigScreens.pages(modId, type, spec)` returns generated pages that can be mixed with your own.

## Option 3: Build Your Own Screens

The design system in `com.aryston.arkea.ui` is public. A screen extends `ArkScreen` (full canvas) or `ArkWindowScreen` (the settings window with sidebar, header, scrolling content and footer), lays out in design pixels of the 1366 x 768 reference and draws through `UiGraphics`.

| Package | Contents |
|---|---|
| `ui.theme` | `ArkColors` (every design token), `Accent`, `Theme.accent()` |
| `ui.anim` | `Transition`, `Presence`, `Timeline`, `Easing`, `Motion` durations |
| `ui.layout` | `Box`, `UiScale` |
| `ui.render` | `UiGraphics` (rectangles, borders, gradients, shadows, glows, images, icons, text), `Icons`, `TextStyle`, `Meter`, `ItemSlot`, `PixelSpinner`, `EmptyState` |
| `ui.widget` | Buttons, switch, checkbox, radio, cycle, dropdown, segmented, tabs, slider, range slider, stepper, text field, color button, chips, swatches, keycaps, tiles, preset tiles, nav items, banners, pagination, compare view, setting rows |
| `ui.overlay` | `ArkDialog` (with `DialogForm`, `DialogList`, `DialogProgress`), `ArkSideSheet`, `ArkContextMenu`, `ArkToasts`, `ArkTooltip`, `ColorPickerPopup` |
| `ui.screen` | `ArkScreen`, `ArkWindowScreen`, `SettingsPanel`, `SettingsSection`, `ScrollArea` |

The Component Gallery (Options → Arkea → Developers → Component Gallery) shows every component live; its source is `screen/gallery/ArkGalleryScreen.java`.

Rules every screen follows:

- Build widgets in `buildUi` (or `buildContent` in a window) and add them with `add(...)`; keep state that must survive a resize in fields.
- Draw every widget exactly once per frame with `widget.render(graphics, mouseX, mouseY)`.
- Move between screens with `navigate` or `switchTo` so exit animations play.
- Open dialogs and side sheets with `openDialog`, menus and pickers with `openPopup`; return items from `contextMenu(x, y)` for right-click menus.

## Stability

`com.aryston.arkea.api` is the stable API. `com.aryston.arkea.ui` is public and documented here but may still change between minor versions while Arkea is below 1.0. Everything else (`screen`, `integration`, `mixin`, `config`, `background`, `debug`) is internal.
