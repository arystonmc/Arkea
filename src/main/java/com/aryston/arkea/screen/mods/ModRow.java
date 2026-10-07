package com.aryston.arkea.screen.mods;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.LetterTile;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import com.aryston.arkea.ui.widget.ArkWidget;
import com.aryston.arkea.ui.widget.Tag;
import com.aryston.arkea.ui.widget.UiHost;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

final class ModRow extends ArkWidget {
    static final float HEIGHT = 56.0F;
    private static final float PADDING_LEFT = 10.0F;
    private static final float PADDING_RIGHT = 14.0F;
    private static final float GAP = 12.0F;
    private static final float ICON = 36.0F;
    private static final float LINE_GAP = 6.0F;
    private static final float SELECTED_FILL = 0.12F;
    private static final int BORDER = ArkColors.rgba(255, 255, 255, 0.07F);
    private static final TextStyle NAME = TextStyle.of(13.0F);
    private static final TextStyle VERSION = TextStyle.of(10.0F);

    private final ModEntry entry;
    private final ModIcons icons;
    private final BooleanSupplier selected;
    private final Runnable select;
    private final List<Tag> tags = new ArrayList<>();

    ModRow(UiHost host, ModEntry entry, ModIcons icons, BooleanSupplier selected, Runnable select) {
        super(host);
        this.entry = entry;
        this.icons = icons;
        this.selected = selected;
        this.select = select;
        if (entry.hasUpdate()) {
            this.tags.add(Tag.tone(Component.translatable("arkea.mods.tag.update"), ArkColors.WARNING));
        }
        if (entry.configFactory().isPresent()) {
            this.tags.add(Tag.tone(Component.translatable("arkea.mods.tag.config"), Theme.accent().light()));
        }
    }

    @Override
    protected void renderWidget(UiGraphics graphics) {
        Box box = this.bounds();
        boolean isSelected = this.selected.getAsBoolean();
        int accent = Theme.accent().base();
        graphics.fill(box, isSelected ? ArkColors.withAlpha(accent, SELECTED_FILL) : ArkColors.lerp(this.hoverProgress(), ArkColors.ROW, ArkColors.ROW_HOVER));
        graphics.border(box, 1.0F, isSelected ? accent : BORDER);
        Box icon = new Box(box.x() + PADDING_LEFT, box.centerY() - ICON * 0.5F, ICON, ICON);
        drawIcon(graphics, this.icons.get(this.entry), icon, this.entry);
        TextMetrics metrics = graphics.metrics();
        float right = box.right() - PADDING_RIGHT;
        for (int index = this.tags.size() - 1; index >= 0; index--) {
            Tag tag = this.tags.get(index);
            right -= tag.width(metrics);
            tag.draw(graphics, right, box.centerY());
            right -= Tag.GAP;
        }
        float textX = icon.right() + GAP;
        float block = metrics.capHeight(NAME) + LINE_GAP + metrics.capHeight(VERSION);
        float y = box.centerY() - block * 0.5F;
        graphics.text(metrics.ellipsize(this.entry.info().displayName().getString(), NAME, right - textX), textX, y, NAME, ArkColors.TEXT_PRIMARY);
        graphics.text(metrics.ellipsize(this.entry.info().version(), VERSION, right - textX), textX, y + metrics.capHeight(NAME) + LINE_GAP, VERSION,
            ArkColors.TEXT_FAINT);
    }

    static void drawIcon(UiGraphics graphics, @Nullable DynamicTexture texture, Box box, ModEntry entry) {
        if (texture != null) {
            graphics.image(texture, box, 0.0F, 0.0F, 1.0F, 1.0F, ArkColors.TEXT_PRIMARY);
            return;
        }
        LetterTile.draw(graphics, box, entry.info().displayName().getString(), Minecraft.getInstance().getLanguageManager().getJavaLocale());
    }

    @Override
    protected void onPress() {
        this.select.run();
    }

    @Override
    protected Component narrationMessage() {
        return Component.translatable("narrator.select", this.entry.info().displayName());
    }
}
