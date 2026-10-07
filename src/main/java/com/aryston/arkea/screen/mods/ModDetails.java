package com.aryston.arkea.screen.mods;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.widget.ArkButton;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;

final class ModDetails {
    private static final String NEOFORGE = "neoforge";
    private static final float PADDING = 28.0F;
    private static final float GAP = 20.0F;
    private static final float ICON = 72.0F;
    private static final float HEADER_GAP = 18.0F;
    private static final float NAME_GAP = 10.0F;
    private static final float CHIP_HEIGHT = 18.0F;
    private static final float CHIP_PADDING = 6.0F;
    private static final float CHIP_GAP = 6.0F;
    private static final float INFO_ROW = 28.0F;
    private static final float VALUE_SHARE = 0.7F;
    private static final float DESCRIPTION_LINE = 19.0F;
    private static final float BUTTON_HEIGHT = 34.0F;
    private static final float BUTTON_GAP = 8.0F;
    private static final float DASH = 4.0F;
    private static final float NO_CONFIG_PADDING = 16.0F;
    private static final int FILL = ArkColors.rgba(255, 255, 255, 0.03F);
    private static final int CHIP_FILL = ArkColors.rgba(255, 255, 255, 0.06F);
    private static final int INFO_LINE = ArkColors.rgba(255, 255, 255, 0.05F);
    private static final int DASH_COLOR = ArkColors.rgba(255, 255, 255, 0.12F);
    private static final TextStyle NAME = TextStyle.of(22.0F);
    private static final TextStyle CHIP = TextStyle.of(10.0F);
    private static final TextStyle INFO = TextStyle.of(11.0F);
    private static final TextStyle DESCRIPTION = TextStyle.of(12.0F);

    private final ModEntry entry;
    private final ModIcons icons;
    private final List<ArkButton> buttons;
    private final boolean hasConfig;
    private Box frame = Box.EMPTY;
    private List<String> description = List.of();

    ModDetails(ModEntry entry, ModIcons icons, List<ArkButton> buttons, boolean hasConfig) {
        this.entry = entry;
        this.icons = icons;
        this.buttons = buttons;
        this.hasConfig = hasConfig;
    }

    private List<Info> info() {
        List<Info> info = new ArrayList<>();
        Component authors = this.entry.authors();
        if (!authors.getString().isEmpty()) {
            info.add(new Info(Component.translatable("arkea.mods.authors"), authors));
        }
        info.add(new Info(Component.translatable("arkea.mods.id"), Component.literal(this.entry.id())));
        Component license = this.entry.info().license();
        if (!license.getString().isEmpty()) {
            info.add(new Info(Component.translatable("arkea.mods.license"), license));
        }
        Component credits = this.entry.info().credits();
        if (!credits.getString().isEmpty()) {
            info.add(new Info(Component.translatable("arkea.mods.credits"), credits));
        }
        String latest = this.entry.latestVersion();
        if (this.entry.hasUpdate() && latest != null) {
            info.add(new Info(Component.translatable("arkea.mods.latest"), Component.literal(latest)));
        }
        return info;
    }

    float layout(Box area, TextMetrics metrics) {
        float inner = area.width() - PADDING * 2.0F;
        String text = this.entry.info().description().getString();
        this.description = text.isBlank() ? List.of() : metrics.wrap(text, DESCRIPTION, inner);
        float height = PADDING + ICON + GAP + this.info().size() * INFO_ROW;
        if (!this.description.isEmpty()) {
            height += GAP + this.description.size() * DESCRIPTION_LINE;
        }
        height += GAP + BUTTON_HEIGHT + PADDING;
        this.frame = new Box(area.x(), area.y(), area.width(), height);
        float x = area.x() + PADDING;
        float y = this.frame.bottom() - PADDING - BUTTON_HEIGHT;
        for (ArkButton button : this.buttons) {
            float width = button.preferredWidth();
            button.setBounds(new Box(x, y, width, BUTTON_HEIGHT));
            x += width + BUTTON_GAP;
        }
        return this.frame.bottom();
    }

    void render(UiGraphics graphics, float mouseX, float mouseY) {
        graphics.fill(this.frame, FILL);
        graphics.border(this.frame, 1.0F, ArkColors.BORDER_DEFAULT);
        TextMetrics metrics = graphics.metrics();
        float x = this.frame.x() + PADDING;
        float inner = this.frame.width() - PADDING * 2.0F;
        Box icon = new Box(x, this.frame.y() + PADDING, ICON, ICON);
        ModRow.drawIcon(graphics, this.icons.get(this.entry), icon, this.entry);
        float textX = icon.right() + HEADER_GAP;
        float block = metrics.capHeight(NAME) + NAME_GAP + CHIP_HEIGHT;
        float nameY = icon.centerY() - block * 0.5F;
        graphics.text(metrics.ellipsize(this.entry.info().displayName().getString(), NAME, this.frame.right() - PADDING - textX), textX, nameY, NAME,
            ArkColors.TEXT_PRIMARY);
        float chipY = nameY + metrics.capHeight(NAME) + NAME_GAP;
        float chipX = this.chip(graphics, Component.translatable("arkea.mods.version", this.entry.info().version()), textX, chipY, true);
        this.chip(graphics, Component.translatable(this.loaderKey()), chipX + CHIP_GAP, chipY, false);
        float y = icon.bottom() + GAP;
        float line = graphics.scale().snapThickness(1.0F);
        for (Info info : this.info()) {
            float textY = y + (INFO_ROW - metrics.capHeight(INFO)) * 0.5F;
            graphics.text(info.key().getString(), x, textY, INFO, ArkColors.TEXT_DESCRIPTION);
            float valueWidth = Math.min(inner * VALUE_SHARE, metrics.width(info.value().getString(), INFO));
            graphics.richText(info.value(), x + inner - valueWidth, textY, valueWidth + 1.0F, INFO, ArkColors.TEXT_PRIMARY);
            graphics.fill(x, y + INFO_ROW - line, inner, line, INFO_LINE);
            y += INFO_ROW;
        }
        if (!this.description.isEmpty()) {
            y += GAP;
            for (String text : this.description) {
                graphics.text(text, x, y + (DESCRIPTION_LINE - metrics.capHeight(DESCRIPTION)) * 0.5F, DESCRIPTION, ArkColors.TEXT_SOFT);
                y += DESCRIPTION_LINE;
            }
        }
        if (!this.hasConfig) {
            this.renderNoConfig(graphics, x);
        }
        for (ArkButton button : this.buttons) {
            button.render(graphics, mouseX, mouseY);
        }
    }

    private void renderNoConfig(UiGraphics graphics, float x) {
        TextMetrics metrics = graphics.metrics();
        String text = Component.translatable("arkea.mods.no_config").getString();
        float left = this.buttons.isEmpty() ? x : this.buttons.getLast().bounds().right() + BUTTON_GAP;
        Box box = new Box(left, this.frame.bottom() - PADDING - BUTTON_HEIGHT, metrics.width(text, DESCRIPTION) + NO_CONFIG_PADDING * 2.0F, BUTTON_HEIGHT);
        graphics.dashedBorder(box, DASH, DASH_COLOR);
        graphics.text(text, box.x() + NO_CONFIG_PADDING, box.centerY() - metrics.capHeight(DESCRIPTION) * 0.5F, DESCRIPTION, ArkColors.TEXT_LABEL);
    }

    private float chip(UiGraphics graphics, Component text, float x, float y, boolean filled) {
        TextMetrics metrics = graphics.metrics();
        String value = text.getString();
        Box box = new Box(x, y, metrics.width(value, CHIP) + CHIP_PADDING * 2.0F, CHIP_HEIGHT);
        if (filled) {
            graphics.fill(box, CHIP_FILL);
        }
        graphics.border(box, 1.0F, ArkColors.BORDER_STRONG);
        graphics.text(value, box.x() + CHIP_PADDING, box.centerY() - metrics.capHeight(CHIP) * 0.5F, CHIP, filled ? ArkColors.TEXT_PRIMARY : ArkColors.TEXT_MUTED);
        return box.right();
    }

    private String loaderKey() {
        if (this.entry.isGame()) {
            return "arkea.mods.loader.game";
        }
        return NEOFORGE.equals(this.entry.id()) ? "arkea.mods.loader.loader" : "arkea.mods.loader.mod";
    }

    private record Info(Component key, Component value) {
    }
}
