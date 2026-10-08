package com.aryston.arkea.screen.palette;

import com.aryston.arkea.ui.anim.Motion;
import com.aryston.arkea.ui.anim.Presence;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.layout.UiScale;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.screen.ArkScreen;
import com.aryston.arkea.ui.text.SearchText;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import com.aryston.arkea.ui.widget.ArkTextField;
import com.aryston.arkea.ui.widget.TextFieldState;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;

public final class CommandPaletteScreen extends ArkScreen {
    private static final float WIDTH = 560.0F;
    private static final float TOP_SHARE = 0.16F;
    private static final float PADDING = 12.0F;
    private static final float FIELD_HEIGHT = 36.0F;
    private static final float ROW_HEIGHT = 40.0F;
    private static final float GROUP_HEIGHT = 24.0F;
    private static final float LIST_MAX = 380.0F;
    private static final float FOOTER_HEIGHT = 30.0F;
    private static final float ICON_BOX = 28.0F;
    private static final float ICON = 14.0F;
    private static final float GAP = 12.0F;
    private static final float LINE_GAP = 5.0F;
    private static final float SHADOW_BLUR = 60.0F;
    private static final float SHADOW_OFFSET = 24.0F;
    private static final float ENTER_OFFSET = 10.0F;
    private static final float ENTER_SCALE = 0.97F;
    private static final float SELECTED_FILL = 0.16F;
    private static final float MARKER = 2.0F;
    private static final float GROUP_INSET = 7.0F;
    private static final int MAX_RESULTS = 80;
    private static final int SEARCH_LENGTH = 64;
    private static final int SCRIM = ArkColors.rgba(8, 8, 9, 0.55F);
    private static final int ICON_FILL = ArkColors.rgba(255, 255, 255, 0.05F);
    private static final TextStyle LABEL = TextStyle.of(13.0F);
    private static final TextStyle DETAIL = TextStyle.of(10.0F);
    private static final TextStyle GROUP = TextStyle.of(10.0F).spacing(1.0F);
    private static final TextStyle HINT = TextStyle.of(10.0F);

    private final List<PaletteEntry> entries = new ArrayList<>();
    private final TextFieldState query = new TextFieldState(SEARCH_LENGTH);
    private final Presence presence = Presence.of(Motion.POPUP_IN, Motion.POPUP_OUT);
    private List<PaletteEntry> results = List.of();
    private int selected;
    private float scroll;
    private float contentHeight;
    private float lastMouseX = Float.NaN;
    private float lastMouseY = Float.NaN;
    private Box frame = Box.EMPTY;
    private Box list = Box.EMPTY;
    private final List<Row> rows = new ArrayList<>();

    public CommandPaletteScreen() {
        super(Component.translatable("arkea.palette.title"));
        this.entries.addAll(PaletteIndex.entries(this.minecraft));
        PaletteIndex.worlds(this.minecraft).thenAcceptAsync(worlds -> {
            this.entries.addAll(worlds);
            this.filter();
        }, this.minecraft);
        this.presence.show(Util.getMillis());
        this.filter();
    }

    @Override
    protected void buildUi() {
        UiScale scale = this.uiScale();
        float x = (scale.canvasWidth() - WIDTH) * 0.5F;
        float y = scale.canvasHeight() * TOP_SHARE;
        this.frame = new Box(x, y, WIDTH, PADDING * 2.0F + FIELD_HEIGHT + LIST_MAX + FOOTER_HEIGHT);
        ArkTextField field = this.add(new ArkTextField(this, Component.translatable("arkea.palette.hint"), this.query, value -> this.filter()));
        field.key("query");
        field.setBounds(new Box(x + PADDING, y + PADDING, WIDTH - PADDING * 2.0F, FIELD_HEIGHT));
        this.list = new Box(x, field.bounds().bottom() + PADDING, WIDTH, LIST_MAX);
        this.setInitialFocus(field);
    }

    private void filter() {
        String normalized = SearchText.normalize(this.query.text());
        this.results = this.entries.stream()
            .filter(entry -> SearchText.matches(normalized, entry.label().getString(), entry.detail().getString()))
            .sorted(Comparator.comparingInt((PaletteEntry entry) -> entry.group().ordinal()).thenComparingInt(entry -> rank(normalized, entry)))
            .limit(MAX_RESULTS)
            .toList();
        this.selected = Math.min(this.selected, Math.max(0, this.results.size() - 1));
        this.scroll = 0.0F;
    }

    private static int rank(String query, PaletteEntry entry) {
        String label = SearchText.normalize(entry.label().getString());
        return label.startsWith(query) ? 0 : label.contains(query) ? 1 : 2;
    }

    @Override
    protected void renderUi(UiGraphics graphics, float mouseX, float mouseY) {
        float progress = this.presence.progress(graphics.now());
        UiScale scale = graphics.scale();
        graphics.push();
        graphics.fade(progress);
        graphics.fill(0.0F, 0.0F, scale.canvasWidth(), scale.canvasHeight(), SCRIM);
        graphics.translate(0.0F, ENTER_OFFSET * (1.0F - progress));
        graphics.scaleAround(ENTER_SCALE + (1.0F - ENTER_SCALE) * progress, this.frame.centerX(), this.frame.centerY());
        graphics.shadow(this.frame, SHADOW_BLUR, SHADOW_OFFSET, ArkColors.SHADOW_DIALOG);
        graphics.fill(this.frame, ArkColors.DIALOG);
        graphics.border(this.frame, 1.0F, ArkColors.BORDER_OVERLAY);
        graphics.topHighlight(this.frame, ArkColors.INNER_HIGHLIGHT);
        this.findWidget("query").ifPresent(field -> field.render(graphics, mouseX, mouseY));
        this.renderResults(graphics, mouseX, mouseY);
        this.renderFooter(graphics);
        graphics.pop();
    }

    private void renderResults(UiGraphics graphics, float mouseX, float mouseY) {
        this.rows.clear();
        boolean moved = mouseX != this.lastMouseX || mouseY != this.lastMouseY;
        this.lastMouseX = mouseX;
        this.lastMouseY = mouseY;
        TextMetrics metrics = graphics.metrics();
        float y = this.list.y() - this.scroll;
        PaletteEntry.Group group = null;
        graphics.clip(this.list);
        for (int index = 0; index < this.results.size(); index++) {
            PaletteEntry entry = this.results.get(index);
            if (entry.group() != group) {
                group = entry.group();
                String label = group.label().getString();
                graphics.text(label, this.list.x() + PADDING + GROUP_INSET, y + (GROUP_HEIGHT - metrics.capHeight(GROUP)) * 0.5F, GROUP,
                    ArkColors.TEXT_LABEL);
                y += GROUP_HEIGHT;
            }
            Box row = new Box(this.list.x() + PADDING * 0.5F, y, this.list.width() - PADDING, ROW_HEIGHT);
            this.rows.add(new Row(index, row));
            if (moved && row.contains(mouseX, mouseY) && this.list.contains(mouseX, mouseY)) {
                this.selected = index;
            }
            if (row.bottom() >= this.list.y() && row.y() <= this.list.bottom()) {
                this.renderRow(graphics, entry, row, index == this.selected);
            }
            y += ROW_HEIGHT;
        }
        this.contentHeight = y + this.scroll - this.list.y();
        if (this.results.isEmpty()) {
            String empty = Component.translatable("arkea.palette.empty").getString();
            graphics.text(empty, this.list.centerX() - metrics.width(empty, LABEL) * 0.5F, this.list.y() + ROW_HEIGHT, LABEL, ArkColors.TEXT_FAINT);
        }
        graphics.endClip();
    }

    private void renderRow(UiGraphics graphics, PaletteEntry entry, Box row, boolean isSelected) {
        TextMetrics metrics = graphics.metrics();
        if (isSelected) {
            graphics.fill(row, ArkColors.withAlpha(Theme.accent().base(), SELECTED_FILL));
            graphics.fill(row.x(), row.y(), MARKER, row.height(), Theme.accent().light());
        }
        Box icon = new Box(row.x() + PADDING * 0.5F, row.centerY() - ICON_BOX * 0.5F, ICON_BOX, ICON_BOX);
        graphics.fill(icon, ICON_FILL);
        graphics.border(icon, 1.0F, ArkColors.BORDER_DEFAULT);
        int iconColor = isSelected ? Theme.accent().light() : ArkColors.TEXT_SOFT;
        graphics.icon(entry.icon(), icon.centerX() - ICON * 0.5F, icon.centerY() - ICON * 0.5F, ICON, ICON, iconColor);
        float textX = icon.right() + GAP;
        float width = row.right() - PADDING - textX;
        float block = metrics.capHeight(LABEL) + LINE_GAP + metrics.capHeight(DETAIL);
        float y = row.centerY() - block * 0.5F;
        graphics.text(metrics.ellipsize(entry.label().getString(), LABEL, width), textX, y, LABEL, ArkColors.TEXT_PRIMARY);
        graphics.text(metrics.ellipsize(entry.detail().getString(), DETAIL, width), textX, y + metrics.capHeight(LABEL) + LINE_GAP, DETAIL,
            ArkColors.TEXT_FAINT);
    }

    private void renderFooter(UiGraphics graphics) {
        TextMetrics metrics = graphics.metrics();
        float line = graphics.scale().snapThickness(1.0F);
        float top = this.frame.bottom() - FOOTER_HEIGHT;
        graphics.fill(this.frame.x(), top, this.frame.width(), line, ArkColors.BORDER_SUBTLE);
        String hint = Component.translatable("arkea.palette.keys").getString();
        graphics.text(hint, this.frame.x() + PADDING, top + (FOOTER_HEIGHT - metrics.capHeight(HINT)) * 0.5F, HINT, ArkColors.TEXT_LABEL);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.isEscape()) {
            this.minecraft.gui.popScreenLayer();
            return true;
        }
        if (event.key() == InputConstants.KEY_DOWN || event.key() == InputConstants.KEY_UP) {
            this.move(event.key() == InputConstants.KEY_DOWN ? 1 : -1);
            return true;
        }
        if (event.isConfirmation()) {
            this.run(this.selected);
            return true;
        }
        return super.keyPressed(event);
    }

    private void move(int step) {
        if (this.results.isEmpty()) {
            return;
        }
        this.selected = Math.floorMod(this.selected + step, this.results.size());
        for (Row row : this.rows) {
            if (row.index() == this.selected) {
                if (row.box().y() < this.list.y()) {
                    this.scroll -= this.list.y() - row.box().y();
                } else if (row.box().bottom() > this.list.bottom()) {
                    this.scroll += row.box().bottom() - this.list.bottom();
                }
                this.scroll = Math.max(0.0F, this.scroll);
            }
        }
    }

    private void run(int index) {
        if (index < 0 || index >= this.results.size()) {
            return;
        }
        PaletteEntry entry = this.results.get(index);
        this.minecraft.gui.popScreenLayer();
        Screen origin = this.minecraft.gui.screen();
        entry.action().accept(origin);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        UiScale scale = this.uiScale();
        float x = scale.toDesign(event.x());
        float y = scale.toDesign(event.y());
        if (!this.frame.contains(x, y)) {
            this.minecraft.gui.popScreenLayer();
            return true;
        }
        for (Row row : this.rows) {
            if (this.list.contains(x, y) && row.box().contains(x, y)) {
                this.run(row.index());
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        this.scroll = Math.clamp(this.scroll - (float) scrollY * ROW_HEIGHT, 0.0F, Math.max(0.0F, this.contentHeight - LIST_MAX));
        return true;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
    }

    @Override
    protected int exitDuration() {
        return Motion.POPUP_OUT;
    }

    private record Row(int index, Box box) {
    }
}
