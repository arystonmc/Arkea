package com.aryston.arkea.screen.options;

import com.aryston.arkea.ui.render.Icon;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.screen.SettingsPanel;
import com.aryston.arkea.ui.screen.SettingsSection;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.sounds.SoundSource;

public final class ArkSoundScreen extends OptionsPageScreen {
    public ArkSoundScreen(Screen lastScreen) {
        super(OptionsPage.SOUND, lastScreen);
    }

    @Override
    protected void addSettings(SettingsPanel settings) {
        SettingsSection master = settings.section(this.section("master"), true);
        this.option(master, this.options.getSoundSourceOptionInstance(SoundSource.MASTER), Icons.SPEAKER);
        this.option(master, this.options.soundDevice(), Icons.HEAD);
        SettingsSection categories = settings.section(this.section("categories"), true);
        for (SoundSource source : SoundSource.values()) {
            if (source != SoundSource.MASTER) {
                this.option(categories, this.options.getSoundSourceOptionInstance(source), icon(source));
            }
        }
        SettingsSection output = settings.section(this.section("output"), true);
        this.option(output, this.options.showSubtitles(), Icons.SUBS);
        this.option(output, this.options.directionalAudio(), Icons.COMPASS);
        this.option(output, this.options.musicFrequency(), Icons.MUSIC);
        this.option(output, this.options.musicToast(), Icons.BELL);
    }

    private static Icon icon(SoundSource source) {
        return switch (source) {
            case MASTER -> Icons.SPEAKER;
            case MUSIC -> Icons.MUSIC;
            case RECORDS -> Icons.NOTE;
            case WEATHER -> Icons.RAIN;
            case BLOCKS -> Icons.CUBE;
            case HOSTILE -> Icons.HOSTILE;
            case NEUTRAL -> Icons.HEART;
            case PLAYERS -> Icons.USER;
            case AMBIENT -> Icons.LEAF;
            case VOICE -> Icons.CHAT;
            case UI -> Icons.SLIDERS;
        };
    }
}
