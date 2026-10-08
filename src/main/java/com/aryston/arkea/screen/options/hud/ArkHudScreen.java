package com.aryston.arkea.screen.options.hud;

import com.aryston.arkea.config.ArkeaConfig;
import com.aryston.arkea.hud.HudSettings;
import com.aryston.arkea.hud.HudStyle;
import com.aryston.arkea.screen.options.OptionsPage;
import com.aryston.arkea.screen.options.OptionsPageScreen;
import com.aryston.arkea.ui.render.Icon;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.screen.SettingsPanel;
import com.aryston.arkea.ui.screen.SettingsSection;
import com.aryston.arkea.ui.widget.ArkSlider;
import com.aryston.arkea.ui.widget.IntSliderModel;
import com.aryston.arkea.ui.widget.ItemContent;
import com.aryston.arkea.ui.widget.SettingRow;
import com.aryston.arkea.ui.widget.SliderRange;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;

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
        SettingsSection look = settings.section(this.section("hudLook"), true);
        this.slider(look, Icons.BLEND, "hudOpacity", ArkeaConfig.HUD_OPACITY,
            new SliderRange(HudSettings.OPACITY_MIN, HudSettings.OPACITY_MAX, HudSettings.OPACITY_STEP), HudSettings.OPACITY_DEFAULT);
        this.slider(look, Icons.RESIZE, "hudSize", ArkeaConfig.HUD_SIZE,
            new SliderRange(HudSettings.SIZE_MIN, HudSettings.SIZE_MAX, HudSettings.SIZE_STEP), HudSettings.SIZE_DEFAULT);
        SettingsSection parts = settings.section(this.section("hudParts"), true);
        this.part(parts, Icons.FLASK, "hudEffects", ArkeaConfig.HUD_EFFECTS);
        this.part(parts, Icons.HOSTILE, "hudBossBars", ArkeaConfig.HUD_BOSS_BARS);
        this.part(parts, Icons.RULES, "hudScoreboard", ArkeaConfig.HUD_SCOREBOARD);
        this.part(parts, Icons.USER, "hudTabList", ArkeaConfig.HUD_TAB_LIST);
        this.addReset(() -> ArkeaConfig.set(ArkeaConfig.HUD_STYLE, HudStyle.VANILLA));
    }

    private void slider(SettingsSection section, Icon icon, String key, ModConfigSpec.IntValue value, SliderRange range, int defaultValue) {
        Component name = Component.translatable("arkea.configuration." + key);
        IntSliderModel model = new IntSliderModel(range, value::get, newValue -> ArkeaConfig.set(value, newValue),
            current -> Component.translatable("arkea.hud.percent", current));
        ArkSlider slider = this.add(new ArkSlider(this, name, model));
        section.add(new SettingRow(new ItemContent(icon, name, Component.translatable("arkea.settings." + key + ".description"))), slider, ArkSlider.WIDTH,
            ArkSlider.HEIGHT);
        this.addReset(() -> ArkeaConfig.set(value, defaultValue));
    }

    private void part(SettingsSection section, Icon icon, String key, ModConfigSpec.BooleanValue value) {
        this.toggle(section, new ItemContent(icon, Component.translatable("arkea.configuration." + key),
            Component.translatable("arkea.settings." + key + ".description")), value::get, newValue -> ArkeaConfig.set(value, newValue), true);
    }
}
