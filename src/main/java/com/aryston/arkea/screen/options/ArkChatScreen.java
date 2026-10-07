package com.aryston.arkea.screen.options;

import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.screen.SettingsPanel;
import com.aryston.arkea.ui.screen.SettingsSection;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;

public final class ArkChatScreen extends OptionsPageScreen {
    public ArkChatScreen(Screen lastScreen) {
        super(OptionsPage.CHAT, lastScreen);
    }

    @Override
    protected void addSettings(SettingsPanel settings) {
        Options options = this.options;
        SettingsSection chat = settings.section(this.section("chat"), false);
        this.option(chat, options.chatVisibility(), Icons.CHAT);
        this.option(chat, options.chatColors(), Icons.SPARKLE);
        this.option(chat, options.chatLinks(), Icons.LINK);
        this.option(chat, options.chatLinksPrompt(), Icons.LINK);
        this.option(chat, options.autoSuggestions(), Icons.CMD);
        this.option(chat, options.hideMatchedNames(), Icons.USER);
        this.option(chat, options.onlyShowSecureChat(), Icons.LOCK);
        this.option(chat, options.saveChatDrafts(), Icons.CHEST);
        this.option(chat, options.reducedDebugInfo(), Icons.CPU);
        this.option(chat, options.narrator(), Icons.NARRATOR);
        SettingsSection appearance = settings.section(this.section("appearance"), false);
        this.option(appearance, options.chatOpacity(), Icons.TEXTBG);
        this.option(appearance, options.textBackgroundOpacity(), Icons.TEXTBG);
        this.option(appearance, options.chatScale(), Icons.FONT);
        this.option(appearance, options.chatLineSpacing(), Icons.RULES);
        this.option(appearance, options.chatDelay(), Icons.CLOCK);
        this.option(appearance, options.chatWidth(), Icons.RESIZE);
        this.option(appearance, options.chatHeightFocused(), Icons.RESIZE);
        this.option(appearance, options.chatHeightUnfocused(), Icons.RESIZE);
    }
}
