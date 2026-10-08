package com.aryston.arkea.tooltip;

import com.aryston.arkea.config.ArkeaConfig;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.layout.UiScale;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import com.mojang.blaze3d.platform.Window;
import com.mojang.datafixers.util.Either;
import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.jspecify.annotations.Nullable;

public final class ArkTooltips {
    private static final int INSET = 4;
    private static final float SHADOW_BLUR = 10.0F;
    private static final float SHADOW_OFFSET = 3.0F;
    private static final float ACCENT_ALPHA = 0.7F;
    private static final int FILL = ArkColors.rgba(16, 16, 18, 0.96F);
    private static final int SHADOW = ArkColors.rgba(0, 0, 0, 0.5F);
    private static final Set<String> CONTAINER_LINES = Set.of("item.container.item_count", "item.container.more_items");

    private ArkTooltips() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(ArkTooltips::registerFactories);
        NeoForge.EVENT_BUS.addListener(ArkTooltips::onGather);
        NeoForge.EVENT_BUS.addListener(ArkTooltips::onItemTooltip);
    }

    public static boolean styled(@Nullable Identifier style) {
        return style == null && ArkeaConfig.on(ArkeaConfig.TOOLTIPS);
    }

    public static void background(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
        Minecraft minecraft = Minecraft.getInstance();
        Window window = minecraft.getWindow();
        UiScale scale = UiScale.compute(window.getWidth(), window.getHeight(), window.getGuiScale());
        UiGraphics ui = new UiGraphics(graphics, scale, minecraft.font, Util.getMillis());
        graphics.pose().pushMatrix();
        graphics.pose().scale(scale.poseScale());
        Box box = new Box(scale.toDesign(x - INSET), scale.toDesign(y - INSET), scale.toDesign(width + INSET * 2), scale.toDesign(height + INSET * 2));
        ui.shadow(box, SHADOW_BLUR, SHADOW_OFFSET, SHADOW);
        ui.fill(box, FILL);
        ui.border(box, 1.0F, ArkColors.BORDER_OVERLAY);
        ui.fill(box.x() + 1.0F, box.y(), box.width() - 2.0F, 1.0F, ArkColors.withAlpha(Theme.accent().light(), ACCENT_ALPHA));
        graphics.pose().popMatrix();
    }

    private static void registerFactories(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(FoodTooltip.class, ClientFoodTooltip::new);
        event.register(DurabilityTooltip.class, ClientDurabilityTooltip::new);
        event.register(ContainerTooltip.class, ClientContainerTooltip::new);
    }

    private static void onGather(RenderTooltipEvent.GatherComponents event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) {
            return;
        }
        if (ArkeaConfig.on(ArkeaConfig.TOOLTIP_CONTAINERS)) {
            List<ItemStack> items = containerItems(stack);
            if (!items.isEmpty()) {
                event.getTooltipElements().add(Either.right(new ContainerTooltip(items)));
            }
        }
        FoodProperties food = stack.get(DataComponents.FOOD);
        if (food != null && ArkeaConfig.on(ArkeaConfig.TOOLTIP_FOOD)) {
            event.getTooltipElements().add(Either.right(new FoodTooltip(food.nutrition(), food.saturation())));
        }
        if (stack.isDamageableItem() && stack.isDamaged() && ArkeaConfig.on(ArkeaConfig.TOOLTIP_DURABILITY)) {
            event.getTooltipElements().add(Either.right(new DurabilityTooltip(stack.getMaxDamage() - stack.getDamageValue(), stack.getMaxDamage(), stack.getBarColor())));
        }
    }

    private static void onItemTooltip(ItemTooltipEvent event) {
        if (!ArkeaConfig.on(ArkeaConfig.TOOLTIP_CONTAINERS) || containerItems(event.getItemStack()).isEmpty()) {
            return;
        }
        event.getToolTip().removeIf(line -> line.getContents() instanceof TranslatableContents contents && CONTAINER_LINES.contains(contents.getKey()));
    }

    private static List<ItemStack> containerItems(ItemStack stack) {
        ItemContainerContents contents = stack.get(DataComponents.CONTAINER);
        return contents == null ? List.of() : contents.nonEmptyItemCopyStream().toList();
    }
}
