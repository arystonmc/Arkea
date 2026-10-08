package com.aryston.arkea.screen.game;

import com.aryston.arkea.ui.anim.Motion;
import com.aryston.arkea.ui.anim.Timeline;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.layout.UiScale;
import com.aryston.arkea.ui.render.EmptyState;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.render.LetterTile;
import com.aryston.arkea.ui.render.Meter;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.screen.ArkScreen;
import com.aryston.arkea.ui.screen.ScrollArea;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import com.aryston.arkea.ui.widget.ArkIconButton;
import com.aryston.arkea.ui.widget.ArkTab;
import com.aryston.arkea.ui.widget.IconButtonStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.world.item.ItemStack;

public final class ArkStatsScreen extends ArkScreen {
    private static final float WIDTH = 860.0F;
    private static final float HEIGHT = 580.0F;
    private static final float HEADER = 64.0F;
    private static final float PADDING_X = 24.0F;
    private static final float PADDING_Y = 20.0F;
    private static final float TABS_GAP = 24.0F;
    private static final float TAB_GAP = 20.0F;
    private static final float CLOSE = 30.0F;
    private static final float TITLE_GAP = 6.0F;
    private static final float GENERAL_ROW = 40.0F;
    private static final float ROW_GAP = 6.0F;
    private static final float COLUMN_GAP = 20.0F;
    private static final float ITEM_ROW = 44.0F;
    private static final float ITEM_GAP = 4.0F;
    private static final float ITEM_HEADER = 32.0F;
    private static final float ITEM_COLUMN = 96.0F;
    private static final float ITEM_ICON = 28.0F;
    private static final float ITEM_NATIVE = 16.0F;
    private static final float MOB_ROW = 52.0F;
    private static final float MOB_NAME = 180.0F;
    private static final float MOB_RIGHT = 140.0F;
    private static final float MOB_TILE = 28.0F;
    private static final float MOB_BAR = 6.0F;
    private static final float ROW_PADDING = 14.0F;
    private static final float ICON = 14.0F;
    private static final float ICON_GAP = 12.0F;
    private static final float SHADOW_BLUR = 80.0F;
    private static final float SHADOW_OFFSET = 30.0F;
    private static final float ENTER_RISE = 14.0F;
    private static final float ENTER_SCALE = 0.98F;
    private static final float SORT_ARROW = 6.0F;
    private static final int GENERAL_COLUMNS = 2;
    private static final int ROW_IN = 300;
    private static final int ROW_STAGGER = 20;
    private static final int MAX_STAGGER = 20;
    private static final int FRAME_FILL = ArkColors.rgba(20, 20, 22, 0.94F);
    private static final int ROW_FILL = ArkColors.rgba(255, 255, 255, 0.03F);
    private static final int ROW_BORDER = ArkColors.rgba(255, 255, 255, 0.06F);
    private static final int BAR_TRACK = ArkColors.rgba(255, 255, 255, 0.06F);
    private static final TextStyle KICKER = TextStyle.of(10.0F).spacing(1.0F);
    private static final TextStyle TITLE = TextStyle.of(18.0F);
    private static final TextStyle TEXT = TextStyle.of(12.0F);
    private static final TextStyle MOB = TextStyle.of(13.0F);
    private static final TextStyle SMALL = TextStyle.of(10.0F);
    private static final TextStyle HEADER_TEXT = TextStyle.of(10.0F).spacing(1.0F);

    private final Screen lastScreen;
    private final ScrollArea scroll = new ScrollArea();
    private StatsData data = new StatsData(List.of(), List.of(), List.of());
    private boolean loading = true;
    private Tab tab = Tab.GENERAL;
    private int sortColumn = -1;
    private boolean descending = true;
    private Box frame = Box.EMPTY;
    private Box content = Box.EMPTY;
    private final List<Box> sortHeaders = new ArrayList<>();

    public ArkStatsScreen(Screen lastScreen) {
        super(Component.translatable("gui.stats"));
        this.lastScreen = lastScreen;
    }

    @Override
    public void added() {
        super.added();
        if (this.minecraft.getConnection() != null) {
            this.minecraft.getConnection().send(new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.REQUEST_STATS));
        }
    }

    public void onStatsUpdated() {
        if (this.minecraft.player != null) {
            this.data = StatsData.read(this.minecraft.player.getStats());
        }
        this.loading = false;
        this.rebuild();
    }

    @Override
    protected void buildUi() {
        UiScale scale = this.uiScale();
        this.frame = new Box((scale.canvasWidth() - WIDTH) * 0.5F, (scale.canvasHeight() - HEIGHT) * 0.5F, WIDTH, HEIGHT);
        float tabX = this.frame.x() + PADDING_X + this.titleWidth() + TABS_GAP;
        for (Tab each : Tab.values()) {
            ArkTab widget = this.add(new ArkTab(this, each.label(), null, () -> this.tab == each, () -> this.select(each)));
            widget.key("tab:" + each.name());
            float width = widget.preferredWidth();
            widget.setBounds(new Box(tabX, this.frame.y() + HEADER - ArkTab.HEIGHT, width, ArkTab.HEIGHT));
            widget.setActive(!this.loading);
            tabX += width + TAB_GAP;
        }
        Component closeLabel = Component.translatable("arkea.window.close");
        ArkIconButton close = this.add(new ArkIconButton(this, Icons.CLOSE, closeLabel, IconButtonStyle.CLOSE, this::onClose));
        close.setBounds(new Box(this.frame.right() - PADDING_X * 0.5F - CLOSE, this.frame.y() + (HEADER - CLOSE) * 0.5F, CLOSE, CLOSE));
        Box viewport = new Box(this.frame.x(), this.frame.y() + HEADER, WIDTH, HEIGHT - HEADER);
        this.content = new Box(viewport.x() + PADDING_X, viewport.y() + PADDING_Y, viewport.width() - PADDING_X * 2.0F, 0.0F);
        this.scroll.layout(viewport, this.contentHeight() + PADDING_Y * 2.0F);
    }

    private float titleWidth() {
        return Math.max(this.metrics().width(this.title.getString(), TITLE), this.metrics().width(PauseCard.worldName().getString(), KICKER));
    }

    private void select(Tab next) {
        if (next != this.tab) {
            this.tab = next;
            this.sortColumn = -1;
            this.scroll.scrollTo(0.0F, this.now());
            this.rebuild();
        }
    }

    private float contentHeight() {
        if (this.loading) {
            return EmptyState.HEIGHT;
        }
        return switch (this.tab) {
            case GENERAL -> (float) Math.ceil(this.data.general().size() / (double) GENERAL_COLUMNS) * (GENERAL_ROW + ROW_GAP);
            case ITEMS -> ITEM_HEADER + ITEM_GAP + this.data.items().size() * (ITEM_ROW + ITEM_GAP);
            case MOBS -> this.data.mobs().size() * (MOB_ROW + ROW_GAP);
        };
    }

    @Override
    protected void renderUi(UiGraphics graphics, float mouseX, float mouseY) {
        float enter = Timeline.enter(this.sinceOpened(), 0, Motion.WINDOW_IN);
        float exit = this.isLeaving() ? Timeline.exit(this.sinceLeft(), Motion.WINDOW_OUT) : 1.0F;
        float progress = enter * exit;
        UiScale scale = graphics.scale();
        graphics.fill(0.0F, 0.0F, scale.canvasWidth(), scale.canvasHeight(), ArkColors.multiplyAlpha(ArkColors.SCRIM_IN_GAME, progress));
        graphics.push();
        graphics.fade(progress);
        graphics.translate(0.0F, ENTER_RISE * (1.0F - progress));
        graphics.scaleAround(ENTER_SCALE + (1.0F - ENTER_SCALE) * progress, this.frame.centerX(), this.frame.centerY());
        graphics.shadow(this.frame, SHADOW_BLUR, SHADOW_OFFSET, ArkColors.SHADOW_WINDOW);
        graphics.fill(this.frame, FRAME_FILL);
        graphics.border(this.frame, 1.0F, ArkColors.BORDER_OVERLAY);
        this.renderHeader(graphics, mouseX, mouseY);
        graphics.clip(this.scroll.viewport());
        graphics.push();
        graphics.translate(0.0F, -this.scroll.offset(graphics.now()));
        this.renderContent(graphics, mouseX, mouseY + this.scroll.offset(graphics.now()));
        graphics.pop();
        graphics.endClip();
        this.scroll.render(graphics, mouseX, mouseY);
        graphics.pop();
    }

    private void renderHeader(UiGraphics graphics, float mouseX, float mouseY) {
        TextMetrics metrics = graphics.metrics();
        float blockHeight = metrics.capHeight(KICKER) + TITLE_GAP + metrics.capHeight(TITLE);
        float y = this.frame.y() + (HEADER - blockHeight) * 0.5F;
        String kicker = PauseCard.worldName().getString().toUpperCase(Locale.ROOT);
        graphics.text(kicker, this.frame.x() + PADDING_X, y, KICKER, ArkColors.TEXT_LABEL);
        graphics.text(this.title.getString(), this.frame.x() + PADDING_X, y + metrics.capHeight(KICKER) + TITLE_GAP, TITLE, ArkColors.TEXT_PRIMARY);
        float line = graphics.scale().snapThickness(1.0F);
        graphics.fill(this.frame.x(), this.frame.y() + HEADER - line, WIDTH, line, ArkColors.BORDER_DEFAULT);
        for (var child : this.children()) {
            if (child instanceof ArkTab tabWidget) {
                tabWidget.render(graphics, mouseX, mouseY);
            } else if (child instanceof ArkIconButton button) {
                button.render(graphics, mouseX, mouseY);
            }
        }
    }

    private void renderContent(UiGraphics graphics, float mouseX, float mouseY) {
        Box area = new Box(this.content.x(), this.content.y(), this.content.width(), this.contentHeight());
        if (this.loading) {
            EmptyState.render(graphics, new Box(area.x(), area.y(), area.width(), EmptyState.HEIGHT), Component.translatable("multiplayer.downloadingStats"),
                Component.empty(), true);
            return;
        }
        switch (this.tab) {
            case GENERAL -> this.renderGeneral(graphics, area);
            case ITEMS -> this.renderItems(graphics, area, mouseX, mouseY);
            case MOBS -> this.renderMobs(graphics, area);
        }
    }

    private void row(UiGraphics graphics, int index, Runnable draw) {
        float progress = Timeline.enter(this.sinceOpened(), Math.min(index, MAX_STAGGER) * ROW_STAGGER, ROW_IN);
        graphics.push();
        graphics.fade(progress);
        draw.run();
        graphics.pop();
    }

    private boolean visible(UiGraphics graphics, Box box) {
        return !graphics.visible(graphics.canvasBox(box)).isEmpty();
    }

    private void renderGeneral(UiGraphics graphics, Box area) {
        TextMetrics metrics = graphics.metrics();
        float columnWidth = (area.width() - COLUMN_GAP) * 0.5F;
        List<StatsData.General> rows = this.data.general();
        for (int index = 0; index < rows.size(); index++) {
            StatsData.General stat = rows.get(index);
            float x = area.x() + index % GENERAL_COLUMNS * (columnWidth + COLUMN_GAP);
            float y = area.y() + index / GENERAL_COLUMNS * (GENERAL_ROW + ROW_GAP);
            Box box = new Box(x, y, columnWidth, GENERAL_ROW);
            if (!this.visible(graphics, box)) {
                continue;
            }
            this.row(graphics, index / GENERAL_COLUMNS, () -> {
                frame(graphics, box);
                graphics.icon(Icons.GAUGE, box.x() + ROW_PADDING, box.centerY() - ICON * 0.5F, ICON, ICON, ArkColors.TEXT_LABEL);
                float textX = box.x() + ROW_PADDING + ICON + ICON_GAP;
                float valueWidth = metrics.width(stat.value(), TEXT);
                String name = metrics.ellipsize(stat.name().getString(), TEXT, box.right() - ROW_PADDING - valueWidth - ICON_GAP - textX);
                float textY = box.centerY() - metrics.capHeight(TEXT) * 0.5F;
                graphics.text(name, textX, textY, TEXT, ArkColors.TEXT_SOFT);
                graphics.text(stat.value(), box.right() - ROW_PADDING - valueWidth, textY, TEXT, ArkColors.TEXT_PRIMARY);
            });
        }
    }

    private void renderItems(UiGraphics graphics, Box area, float mouseX, float mouseY) {
        TextMetrics metrics = graphics.metrics();
        List<Component> columns = StatsData.columnNames();
        this.sortHeaders.clear();
        float columnsX = area.right() - ROW_PADDING - columns.size() * ITEM_COLUMN;
        graphics.text(Component.translatable("arkea.stats.item").getString().toUpperCase(Locale.ROOT), area.x() + ROW_PADDING,
            area.y() + (ITEM_HEADER - metrics.capHeight(HEADER_TEXT)) * 0.5F, HEADER_TEXT, ArkColors.TEXT_LABEL);
        for (int column = 0; column < columns.size(); column++) {
            Box header = new Box(columnsX + column * ITEM_COLUMN, area.y(), ITEM_COLUMN, ITEM_HEADER);
            this.sortHeaders.add(header);
            boolean active = column == this.sortColumn;
            boolean hovered = header.contains(mouseX, mouseY);
            String label = metrics.ellipsize(columns.get(column).getString().toUpperCase(Locale.ROOT), HEADER_TEXT, ITEM_COLUMN - SORT_ARROW * 2.0F);
            float labelX = header.right() - metrics.width(label, HEADER_TEXT) - (active ? SORT_ARROW * 2.0F : 0.0F);
            int color = active ? Theme.accent().light() : hovered ? ArkColors.TEXT_PRIMARY : ArkColors.TEXT_LABEL;
            graphics.text(label, labelX, header.centerY() - metrics.capHeight(HEADER_TEXT) * 0.5F, HEADER_TEXT, color);
            if (active) {
                graphics.icon(this.descending ? Icons.DOWN : Icons.UP, header.right() - SORT_ARROW, header.centerY() - SORT_ARROW * 0.5F, SORT_ARROW,
                    SORT_ARROW, color);
            }
        }
        List<StatsData.ItemStats> rows = this.sortedItems();
        float y = area.y() + ITEM_HEADER + ITEM_GAP;
        for (int index = 0; index < rows.size(); index++) {
            StatsData.ItemStats stat = rows.get(index);
            Box box = new Box(area.x(), y, area.width(), ITEM_ROW);
            y += ITEM_ROW + ITEM_GAP;
            if (!this.visible(graphics, box)) {
                continue;
            }
            this.row(graphics, index, () -> {
                frame(graphics, box);
                Box icon = new Box(box.x() + ROW_PADDING, box.centerY() - ITEM_ICON * 0.5F, ITEM_ICON, ITEM_ICON);
                ItemStack stack = new ItemStack(stat.item());
                graphics.vanilla(icon, ITEM_NATIVE, vanilla -> vanilla.fakeItem(stack, 0, 0));
                float textX = icon.right() + ICON_GAP;
                String name = metrics.ellipsize(stack.getHoverName().getString(), TEXT, columnsX - ICON_GAP - textX);
                float textY = box.centerY() - metrics.capHeight(TEXT) * 0.5F;
                graphics.text(name, textX, textY, TEXT, ArkColors.TEXT_PRIMARY);
                for (int column = 0; column < stat.values().length; column++) {
                    int value = stat.values()[column];
                    String text = value > 0 ? Integer.toString(value) : "-";
                    float right = columnsX + (column + 1) * ITEM_COLUMN;
                    graphics.text(text, right - metrics.width(text, TEXT), textY, TEXT, value > 0 ? ArkColors.TEXT_PRIMARY : ArkColors.TEXT_LABEL);
                }
            });
        }
    }

    private List<StatsData.ItemStats> sortedItems() {
        List<StatsData.ItemStats> rows = new ArrayList<>(this.data.items());
        if (this.sortColumn >= 0) {
            Comparator<StatsData.ItemStats> order = Comparator.comparingInt(row -> row.values()[this.sortColumn]);
            rows.sort(this.descending ? order.reversed() : order);
        }
        return rows;
    }

    private void renderMobs(UiGraphics graphics, Box area) {
        TextMetrics metrics = graphics.metrics();
        List<StatsData.MobStats> rows = this.data.mobs();
        int most = rows.stream().mapToInt(StatsData.MobStats::killed).max().orElse(1);
        for (int index = 0; index < rows.size(); index++) {
            StatsData.MobStats stat = rows.get(index);
            Box box = new Box(area.x(), area.y() + index * (MOB_ROW + ROW_GAP), area.width(), MOB_ROW);
            if (!this.visible(graphics, box)) {
                continue;
            }
            this.row(graphics, index, () -> {
                frame(graphics, box);
                String name = stat.type().getDescription().getString();
                Box tile = new Box(box.x() + ROW_PADDING, box.centerY() - MOB_TILE * 0.5F, MOB_TILE, MOB_TILE);
                LetterTile.draw(graphics, tile, name, Locale.ROOT);
                float nameX = tile.right() + ICON_GAP;
                graphics.text(metrics.ellipsize(name, MOB, MOB_NAME - MOB_TILE - ICON_GAP), nameX, box.centerY() - metrics.capHeight(MOB) * 0.5F, MOB,
                    ArkColors.TEXT_PRIMARY);
                float barX = box.x() + ROW_PADDING + MOB_NAME + ROW_PADDING;
                float barWidth = box.right() - ROW_PADDING - MOB_RIGHT - ROW_PADDING - barX;
                float barY = box.centerY() - MOB_BAR;
                Box bar = new Box(barX, barY, barWidth, MOB_BAR);
                graphics.fill(bar, BAR_TRACK);
                Meter.progress(graphics, bar, stat.killed() / (float) Math.max(1, most), Theme.accent().base());
                String killed = Component.translatable("arkea.stats.killed", stat.killed()).getString();
                graphics.text(killed, barX, barY + MOB_BAR + ROW_GAP, SMALL, ArkColors.TEXT_DESCRIPTION);
                String by = stat.killedBy() > 0 ? Component.translatable("arkea.stats.killedBy", stat.killedBy()).getString()
                    : Component.translatable("arkea.stats.killedBy.none").getString();
                graphics.text(by, box.right() - ROW_PADDING - metrics.width(by, SMALL), box.centerY() - metrics.capHeight(SMALL) * 0.5F, SMALL,
                    stat.killedBy() > 0 ? ArkColors.DANGER_TEXT : ArkColors.TEXT_LABEL);
            });
        }
    }

    private static void frame(UiGraphics graphics, Box box) {
        graphics.fill(box, ROW_FILL);
        graphics.border(box, 1.0F, ROW_BORDER);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        UiScale scale = this.uiScale();
        float x = scale.toDesign(event.x());
        float y = scale.toDesign(event.y());
        if (this.isInteractive() && this.scroll.press(x, y, this.now())) {
            return true;
        }
        if (this.isInteractive() && this.tab == Tab.ITEMS && !this.loading && this.scroll.isOver(x, y)) {
            float contentY = y + this.scroll.offset(this.now());
            for (int column = 0; column < this.sortHeaders.size(); column++) {
                if (this.sortHeaders.get(column).contains(x, contentY)) {
                    this.descending = column != this.sortColumn || !this.descending;
                    this.sortColumn = column;
                    return true;
                }
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        UiScale scale = this.uiScale();
        if (this.isInteractive() && this.scroll.isOver(scale.toDesign(mouseX), scale.toDesign(mouseY))) {
            this.scroll.scrollBy((float) -scrollY * ScrollArea.WHEEL_STEP, this.now());
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (this.scroll.isDragging()) {
            this.scroll.drag(this.uiScale().toDesign(event.y()));
            return true;
        }
        return super.mouseDragged(event, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (this.scroll.isDragging()) {
            this.scroll.release();
            return true;
        }
        return super.mouseReleased(event);
    }

    @Override
    protected boolean blursBackground() {
        return this.menuBlurEnabled();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        if (this.blursBackground()) {
            graphics.blurBeforeThisStratum();
        }
    }

    @Override
    protected int exitDuration() {
        return Motion.WINDOW_OUT;
    }

    @Override
    public void onClose() {
        this.navigate(() -> this.lastScreen);
    }

    private enum Tab {
        GENERAL("stat.generalButton"),
        ITEMS("stat.itemsButton"),
        MOBS("stat.mobsButton");

        private final String key;

        Tab(String key) {
            this.key = key;
        }

        Component label() {
            return Component.translatable(this.key);
        }
    }
}
