package com.aryston.arkea.screen.settings;

import com.aryston.arkea.api.config.ApplyMode;
import com.aryston.arkea.api.config.ArkeaConfigScreen;
import com.aryston.arkea.api.config.ConfigOption;
import com.aryston.arkea.screen.gallery.ArkGalleryScreen;
import com.aryston.arkea.screen.options.OptionsPage;
import com.aryston.arkea.screen.options.arkea.ArkArkeaScreen;
import com.aryston.arkea.screen.options.hud.ArkHudScreen;
import com.aryston.arkea.ui.render.Icon;
import com.aryston.arkea.ui.render.Icons;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class ArkeaSettingsScreen {
    private static final String KEY = "arkea.settings.screen.";

    private ArkeaSettingsScreen() {
    }

    public static Screen create(Screen parent) {
        ArkeaSettingsOptions options = new ArkeaSettingsOptions();
        return ArkeaConfigScreen.builder(text("title"))
            .brand(OptionsPage.ARKEA_LOGO, text("brand"), text("brand.subtitle"))
            .applyMode(ApplyMode.IMMEDIATE)
            .group(text("group.interface"))
            .page("appearance", text("page.appearance"), Icons.SPARKLE, page -> page
                .fullSection(text("section.backgrounds"), section -> section
                    .add(open("backgrounds", Icons.PANO, ArkArkeaScreen::new))
                    .add(options.backgroundPan))
                .fullSection(text("section.theme"), section -> section.add(options.accent).add(options.vanillaTheme)))
            .page("screens", text("page.screens"), Icons.LAYERS, page -> page
                .fullSection(text("section.screens"), section -> section.add(options.titleScreen).add(options.optionsScreen)
                    .add(options.menuScreens).add(options.inGameScreens).add(options.modConfigScreens))
                .fullSection(text("section.feedback"), section -> section.add(options.joinSound).add(options.gameToasts).add(options.debugOverlay)))
            .page("hud", text("page.hud"), Icons.MONITOR, page -> page
                .fullSection(text("section.hudStyle"), section -> section.add(options.hudStyle).add(open("hudPreview", Icons.EYE, ArkHudScreen::new))
                    .add(options.hudOpacity).add(options.hudSize))
                .section(text("section.hudParts"), section -> section.add(options.hudEffects).add(options.hudBossBars).add(options.hudScoreboard)
                    .add(options.hudTabList).add(options.chat))
                .section(text("section.hudInfo"), section -> section.add(options.targetCard).add(options.pickupFeed).add(options.stackTotal)
                    .add(options.ammoCounter).add(options.infoChip).add(options.deathPoint))
                .section(text("section.hudWarnings"), section -> section.add(options.durabilityWarning).add(options.armorIcons)
                    .add(options.foodPreview).add(options.damageDirection).add(options.sleepReminder))
                .section(text("section.crosshair"), section -> section.add(options.crosshair).add(options.hitMarker)))
            .page("tooltips", text("page.tooltips"), Icons.CHAT, page -> page
                .fullSection(text("section.tooltips"), section -> section.add(options.tooltips).add(options.tooltipFood)
                    .add(options.tooltipDurability).add(options.tooltipContainers))
                .fullSection(text("section.access"), section -> section.add(options.soundRadar)))
            .group(text("group.system"))
            .page("developers", text("page.developers"), Icons.CMD, page -> page
                .fullSection(text("section.developers"), section -> section.add(open("gallery", Icons.LAYERS, ArkGalleryScreen::new))))
            .build(parent);
    }

    private static ConfigOption<Boolean> open(String id, Icon icon, Function<Screen, Screen> screen) {
        return ConfigOption.action(KEY + "open." + id, text("open." + id + ".button"), () -> {
                Minecraft minecraft = Minecraft.getInstance();
                minecraft.gui.setScreen(screen.apply(minecraft.gui.screen()));
            })
            .name(text("open." + id))
            .description(text("open." + id + ".description"))
            .icon(icon);
    }

    private static Component text(String key) {
        return Component.translatable(KEY + key);
    }
}
