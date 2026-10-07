package com.aryston.arkea.screen.options;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.screen.SettingsPanel;
import com.aryston.arkea.ui.screen.SettingsSection;
import com.aryston.arkea.ui.text.SearchText;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.widget.ArkButton;
import com.aryston.arkea.ui.widget.ArkChoiceTile;
import com.aryston.arkea.ui.widget.ButtonVariant;
import com.aryston.arkea.ui.widget.NoticeRow;
import com.aryston.arkea.ui.widget.WidgetRow;
import java.util.Map;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public final class ArkLanguageScreen extends OptionsPageScreen {
    private static final String DEFAULT_LANGUAGE = "en_us";
    private static final int COLUMNS = 3;

    private final LanguageManager languages;
    private String selected;
    private @Nullable ArkChoiceTile selectedTile;
    private boolean revealed;

    public ArkLanguageScreen(Screen lastScreen) {
        super(OptionsPage.LANGUAGE, lastScreen);
        this.languages = this.minecraft.getLanguageManager();
        this.selected = this.languages.getSelected();
    }

    @Override
    protected @Nullable Component searchHint() {
        return Component.translatable("arkea.language.search");
    }

    @Override
    protected boolean focusSearchFirst() {
        return true;
    }

    @Override
    protected void addSettings(SettingsPanel settings) {
        String query = SearchText.normalize(this.searchQuery());
        SettingsSection list = settings.section(null, true).columns(COLUMNS);
        this.selectedTile = null;
        for (Map.Entry<String, LanguageInfo> entry : this.languages.getLanguages().entrySet()) {
            String code = entry.getKey();
            LanguageInfo info = entry.getValue();
            if (!SearchText.matches(query, info.name(), info.region(), code)) {
                continue;
            }
            ArkChoiceTile tile = this.add(new ArkChoiceTile(this, Component.literal(info.name()), Component.literal(info.region()),
                () -> this.selected.equals(code), () -> this.selected = code).onDoubleClick(this::onClose));
            tile.key("language:" + code);
            list.add(new WidgetRow(tile, ArkChoiceTile.HEIGHT));
            if (code.equals(this.selected)) {
                this.selectedTile = tile;
            }
        }
        if (list.isEmpty()) {
            ArkButton clear = this.add(new ArkButton(this, Component.translatable("arkea.keybinds.clear"), ButtonVariant.SUBTLE, () -> {
                this.clearSearch();
                this.rebuild();
            }));
            clear.key("clear-search");
            list.columns(1).add(new NoticeRow(Icons.SEARCH, ArkColors.INFO, ArkColors.INFO_TEXT, Component.translatable("arkea.language.empty"), clear));
        }
        SettingsSection font = settings.section(this.section("font"), true);
        this.option(font, this.options.forceUnicodeFont(), Icons.FONT);
        this.option(font, this.options.japaneseGlyphVariants(), Icons.FONT);
    }

    @Override
    protected void afterLayout() {
        if (!this.revealed && this.selectedTile != null) {
            this.revealed = true;
            Box tile = this.selectedTile.bounds();
            this.scrollToReveal(tile);
        }
    }

    @Override
    protected Component footerNote() {
        if (!this.selected.equals(this.languages.getSelected())) {
            return Component.translatable("arkea.language.pending");
        }
        return Component.translatable("arkea.language.accuracy");
    }

    @Override
    protected void resetDefaults() {
        super.resetDefaults();
        this.selected = DEFAULT_LANGUAGE;
        this.rebuild();
    }

    @Override
    public void removed() {
        if (!this.selected.equals(this.languages.getSelected())) {
            this.languages.setSelected(this.selected);
            this.options.languageCode = this.selected;
            this.minecraft.reloadResourcePacks();
        }
        super.removed();
    }
}
