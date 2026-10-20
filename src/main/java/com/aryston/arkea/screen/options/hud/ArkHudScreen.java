package com.aryston.arkea.screen.options.hud;

import com.aryston.arkea.config.ArkeaConfig;
import com.aryston.arkea.hud.HudSettings;
import com.aryston.arkea.hud.HudStyle;
import com.aryston.arkea.screen.options.OptionsPage;
import com.aryston.arkea.screen.options.OptionsPageScreen;
import com.aryston.arkea.ui.screen.SettingsPanel;
import com.aryston.arkea.ui.screen.SettingsSection;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class ArkHudScreen extends OptionsPageScreen {
    private static final int STYLE_COLUMNS = 2;

    public ArkHudScreen(Screen lastScreen) {
        super(OptionsPage.HUD, lastScreen);
    }

    @Override
    protected void addSettings(SettingsPanel settings) {
        settings.section(this.section("hudPreview"), true).columns(1).add(new HudPreviewRow());
        SettingsSection styles = settings.section(this.section("hudStyle"), true).columns(STYLE_COLUMNS)
            .hint(Component.translatable("arkea.hud.style.hint"));
        for (HudStyle style : HudStyle.values()) {
            HudStyleCard card = this.add(new HudStyleCard(this, style, HudSettings::current, value -> ArkeaConfig.set(ArkeaConfig.HUD_STYLE, value)));
            styles.add(new HudStyleRow(card));
        }
        this.addReset(() -> ArkeaConfig.set(ArkeaConfig.HUD_STYLE, HudStyle.VANILLA));
    }
}
