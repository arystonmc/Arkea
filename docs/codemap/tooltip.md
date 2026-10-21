# Arkea Code Map: `com.aryston.arkea.tooltip`

Part of the [Arkea code map](../CODEMAP.md). Every class of this package has its entry here and nowhere else (rule D1).

### `com.aryston.arkea.tooltip`

#### ArkTooltips
- Path: `src/main/java/com/aryston/arkea/tooltip/ArkTooltips.java`
- Role: Draws the default tooltip background as an Arkea card (shadow, dark fill, border, accent line; custom tooltip styles of items stay), adds the food, durability and container components in `RenderTooltipEvent.GatherComponents`, removes the vanilla container text lines when the grid replaces them, and registers the client components.

#### FoodTooltip, DurabilityTooltip, ContainerTooltip
- Path: `src/main/java/com/aryston/arkea/tooltip/FoodTooltip.java`, `src/main/java/com/aryston/arkea/tooltip/DurabilityTooltip.java`, `src/main/java/com/aryston/arkea/tooltip/ContainerTooltip.java`
- Role: Tooltip component data: nutrition and saturation, remaining and maximum uses with the bar color, and the items of a container.

#### ClientFoodTooltip, ClientDurabilityTooltip, ClientContainerTooltip
- Path: `src/main/java/com/aryston/arkea/tooltip/ClientFoodTooltip.java`, `src/main/java/com/aryston/arkea/tooltip/ClientDurabilityTooltip.java`, `src/main/java/com/aryston/arkea/tooltip/ClientContainerTooltip.java`
- Role: Draw them in the tooltip: hunger shanks and saturation as empty shanks with a golden outline (`FoodOutline`, a half outline fades its right half; a count past ten icons), a durability bar with "remaining / max", and a nine column slot grid with counts.

#### FoodOutline
- Path: `src/main/java/com/aryston/arkea/tooltip/FoodOutline.java`
- Role: Draws the outline of the `hud/food_full` sprite in one color, AppleSkin style: the edge pixels are read from the sprite contents of the GUI atlas (so resource packs are followed) once per atlas reload and drawn as one GUI fill each, scaled to the icon size.
