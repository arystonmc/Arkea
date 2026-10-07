package com.aryston.arkea.screen.options;

import com.aryston.arkea.ui.render.Icon;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.screen.SettingsPanel;
import com.aryston.arkea.ui.screen.SettingsSection;
import com.aryston.arkea.ui.widget.ItemContent;
import java.util.Locale;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.PlayerModelPart;

public final class ArkSkinScreen extends OptionsPageScreen {
    public ArkSkinScreen(Screen lastScreen) {
        super(OptionsPage.SKIN, lastScreen);
    }

    @Override
    protected void addSettings(SettingsPanel settings) {
        SettingsSection parts = settings.section(this.section("model"), true);
        for (PlayerModelPart part : PlayerModelPart.values()) {
            Component description = Component.translatable("arkea.option.modelPart." + part.name().toLowerCase(Locale.ROOT));
            this.toggle(parts, new ItemContent(icon(part), part.getName(), description), () -> this.options.isModelPartEnabled(part),
                value -> this.options.setModelPart(part, value), true);
        }
        SettingsSection hand = settings.section(this.section("hand"), true);
        this.option(hand, this.options.mainHand(), Icons.HAND);
    }

    private static Icon icon(PlayerModelPart part) {
        return switch (part) {
            case CAPE -> Icons.CAPE;
            case JACKET -> Icons.SHIRT;
            case LEFT_SLEEVE, RIGHT_SLEEVE -> Icons.SLEEVE;
            case LEFT_PANTS_LEG, RIGHT_PANTS_LEG -> Icons.LEG;
            case HAT -> Icons.HAT;
        };
    }
}
