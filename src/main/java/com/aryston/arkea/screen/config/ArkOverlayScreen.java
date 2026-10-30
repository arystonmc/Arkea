package com.aryston.arkea.screen.config;

import com.aryston.arkea.api.config.ArkeaConfigScreen;
import com.aryston.arkea.api.config.ConfigOption;
import com.aryston.arkea.api.config.ConfigPage;
import com.aryston.arkea.api.config.ConfigSection;
import com.aryston.arkea.ui.anim.Motion;
import com.aryston.arkea.ui.anim.Timeline;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.layout.UiScale;
import com.aryston.arkea.ui.overlay.TooltipHint;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.screen.ArkScreen;
import com.aryston.arkea.ui.screen.ScrollArea;
import com.aryston.arkea.ui.screen.SettingsPanel;
import com.aryston.arkea.ui.screen.SettingsSection;
import com.aryston.arkea.ui.screen.SidebarBrand;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import com.aryston.arkea.ui.widget.ArkIconButton;
import com.aryston.arkea.ui.widget.ArkSegmented;
import com.aryston.arkea.ui.widget.ArkWidget;
import com.aryston.arkea.ui.widget.IconButtonStyle;
import com.aryston.arkea.ui.widget.NoticeRow;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public final class ArkOverlayScreen extends ArkScreen {
    private static final float PANEL_WIDTH = 420.0F;
    private static final float MARGIN = 20.0F;
    private static final float INSET = 20.0F;
    private static final float HEADER_HEIGHT = 64.0F;
    private static final float BRAND_BOX = 28.0F;
    private static final float BRAND_ICON = 14.0F;
    private static final float BRAND_GAP = 12.0F;
    private static final float BRAND_LINE_GAP = 4.0F;
    private static final float CLOSE_SIZE = 28.0F;
    private static final float STATUS_PADDING = 12.0F;
    private static final float STATUS_LINE = 16.0F;
    private static final float BLOCK_GAP = 14.0F;
    private static final float CONTENT_BOTTOM = 16.0F;
    private static final float REVEAL_MARGIN = 12.0F;
    private static final float PANEL_SLIDE = 24.0F;
    private static final float SHADOW_BLUR = 60.0F;
    private static final float SHADOW_OFFSET = 16.0F;
    private static final int PANEL_FILL = ArkColors.rgba(20, 20, 22, 0.82F);
    private static final int STATUS_FILL = ArkColors.rgba(255, 255, 255, 0.04F);
    private static final TextStyle TITLE = TextStyle.of(15.0F).spacing(1.0F);
    private static final TextStyle SUBTITLE = TextStyle.of(10.0F);
    private static final TextStyle STATUS_FIRST = TextStyle.of(13.0F);
    private static final TextStyle STATUS = TextStyle.of(11.0F);

    private final ConfigSession session;
    private final ConfigRows rows;
    private final ScrollArea scroll = new ScrollArea();
    private final List<ArkWidget> chrome = new ArrayList<>();
    private SettingsPanel panel = new SettingsPanel();
    private Box frame = Box.EMPTY;
    private Box statusBox = Box.EMPTY;
    private int statusLines;

    private ArkOverlayScreen(ConfigSession session) {
        super(session.definition().title());
        this.session = session;
        this.rows = new ConfigRows(this, session, this::add, this::reset, null);
    }

    public static ArkOverlayScreen open(ArkeaConfigScreen.Definition definition) {
        return new ArkOverlayScreen(new ConfigSession(definition));
    }

    @Override
    protected void buildUi() {
        UiScale scale = this.uiScale();
        this.chrome.clear();
        this.rows.clear();
        this.frame = new Box(MARGIN, MARGIN, PANEL_WIDTH, scale.canvasHeight() - MARGIN * 2.0F);
        float x = this.frame.x() + INSET;
        float width = this.frame.width() - INSET * 2.0F;
        this.buildClose();
        this.statusLines = this.session.definition().status().get().size();
        float y = this.frame.y() + HEADER_HEIGHT;
        this.statusBox = Box.EMPTY;
        if (this.statusLines > 0) {
            this.statusBox = new Box(x, y, width, STATUS_PADDING * 2.0F + STATUS_LINE * this.statusLines);
            y = this.statusBox.bottom() + BLOCK_GAP;
        }
        y = this.buildTabs(x, y, width);
        Box viewport = new Box(this.frame.x(), y, this.frame.width(), this.frame.bottom() - CONTENT_BOTTOM - y);
        Box content = new Box(x, y, width, viewport.height());
        this.panel = new SettingsPanel();
        this.addPage(this.session.currentPage());
        float contentBottom = this.panel.layout(content, content.y());
        this.scroll.layout(viewport, contentBottom + CONTENT_BOTTOM - viewport.y());
    }

    private void buildClose() {
        Component label = Component.translatable("arkea.window.close");
        ArkIconButton close = this.add(new ArkIconButton(this, Icons.CLOSE, label, IconButtonStyle.CLOSE, this::onClose));
        float top = this.frame.y() + (HEADER_HEIGHT - CLOSE_SIZE) * 0.5F;
        close.setBounds(new Box(this.frame.right() - INSET - CLOSE_SIZE, top, CLOSE_SIZE, CLOSE_SIZE));
        close.setTooltip(label);
        this.chrome.add(close);
    }

    private float buildTabs(float x, float y, float width) {
        List<ConfigPage> pages = this.session.definition().pages();
        if (pages.size() < 2) {
            return y;
        }
        List<Component> labels = pages.stream().map(ConfigPage::title).toList();
        ArkSegmented tabs = this.add(new ArkSegmented(this, this.title, labels, () -> pages.indexOf(this.session.currentPage()), index -> {
            this.session.setPage(pages.get(index).id());
            this.scroll.scrollTo(0.0F, this.now());
            this.rebuild();
        }));
        tabs.setBounds(new Box(x, y, width, ArkSegmented.HEIGHT));
        this.chrome.add(tabs);
        return y + ArkSegmented.HEIGHT + BLOCK_GAP;
    }

    private void addPage(ConfigPage page) {
        Component notice = page.notice();
        if (notice != null) {
            this.panel.section(null, true).columns(1).add(new NoticeRow(Icons.INFO, ArkColors.INFO, ArkColors.INFO_TEXT, notice, null));
        }
        for (ConfigSection configSection : page.sections()) {
            SettingsSection section = this.panel.section(configSection.title(), true).columns(1).hint(configSection.hint());
            for (ConfigOption<?> option : configSection.options()) {
                this.rows.add(section, option);
            }
        }
    }

    private void reset(ConfigOption<?> option) {
        this.session.resetToDefault(option);
        this.rebuild();
    }

    @Override
    protected void renderUi(UiGraphics graphics, float mouseX, float mouseY) {
        List<Component> status = this.session.definition().status().get();
        if (status.size() != this.statusLines) {
            this.statusLines = status.size();
            this.minecraft.schedule(this::rebuild);
        }
        this.rows.refreshActive();
        float enter = Timeline.enter(this.sinceOpened(), 0, Motion.WINDOW_IN);
        float exit = this.isLeaving() ? Timeline.exit(this.sinceLeft(), Motion.WINDOW_OUT) : 1.0F;
        float progress = enter * exit;
        graphics.push();
        graphics.fade(progress);
        graphics.translate(-PANEL_SLIDE * (1.0F - progress), 0.0F);
        graphics.shadow(this.frame, SHADOW_BLUR, SHADOW_OFFSET, ArkColors.SHADOW_WINDOW);
        graphics.fill(this.frame, PANEL_FILL);
        graphics.border(this.frame, 1.0F, ArkColors.BORDER_DEFAULT);
        graphics.topHighlight(this.frame, ArkColors.INNER_HIGHLIGHT);
        this.renderHeader(graphics);
        this.renderStatus(graphics, status);
        graphics.clip(this.scroll.viewport());
        graphics.push();
        graphics.translate(0.0F, -this.scroll.offset(graphics.now()));
        this.panel.render(graphics, mouseX, mouseY, (index, draw) -> draw.run(), 0);
        graphics.pop();
        graphics.endClip();
        this.scroll.render(graphics, mouseX, mouseY);
        for (ArkWidget widget : this.chrome) {
            widget.render(graphics, mouseX, mouseY);
        }
        graphics.pop();
    }

    private void renderHeader(UiGraphics graphics) {
        SidebarBrand brand = this.session.definition().brand();
        float top = this.frame.y() + (HEADER_HEIGHT - BRAND_BOX) * 0.5F;
        Box box = new Box(this.frame.x() + INSET, top, BRAND_BOX, BRAND_BOX);
        if (brand.logo() != null) {
            graphics.image(brand.logo(), box, 0.0F, 0.0F, 1.0F, 1.0F, ArkColors.TEXT_PRIMARY);
        } else {
            graphics.border(box, 1.0F, ArkColors.BORDER_STRONG);
            float offset = (BRAND_BOX - BRAND_ICON) * 0.5F;
            graphics.icon(brand.icon(), box.x() + offset, box.y() + offset, BRAND_ICON, BRAND_ICON, Theme.accent().light());
        }
        TextMetrics metrics = graphics.metrics();
        float block = metrics.capHeight(TITLE) + BRAND_LINE_GAP + metrics.capHeight(SUBTITLE);
        float textX = box.right() + BRAND_GAP;
        float titleY = box.centerY() - block * 0.5F;
        graphics.text(brand.title().getString(), textX, titleY, TITLE, ArkColors.TEXT_PRIMARY);
        graphics.text(brand.subtitle().getString(), textX, titleY + metrics.capHeight(TITLE) + BRAND_LINE_GAP, SUBTITLE, ArkColors.TEXT_FAINT);
    }

    private void renderStatus(UiGraphics graphics, List<Component> status) {
        if (this.statusBox.height() <= 0.0F) {
            return;
        }
        graphics.fill(this.statusBox, STATUS_FILL);
        graphics.border(this.statusBox, 1.0F, ArkColors.BORDER_SUBTLE);
        TextMetrics metrics = graphics.metrics();
        float y = this.statusBox.y() + STATUS_PADDING;
        int shown = Math.min(status.size(), this.statusLines);
        for (int index = 0; index < shown; index++) {
            TextStyle style = index == 0 ? STATUS_FIRST : STATUS;
            int color = index == 0 ? Theme.accent().light() : ArkColors.TEXT_SOFT;
            float lineY = y + (STATUS_LINE - metrics.capHeight(style)) * 0.5F;
            graphics.text(status.get(index).getString(), this.statusBox.x() + STATUS_PADDING, lineY, style, color);
            y += STATUS_LINE;
        }
    }

    @Override
    protected @Nullable TooltipHint hoveredHint() {
        TooltipHint widgetHint = super.hoveredHint();
        return widgetHint != null ? widgetHint : this.panel.hoveredHint();
    }

    @Override
    public void setFocused(@Nullable GuiEventListener focused) {
        super.setFocused(focused);
        if (focused instanceof ArkWidget widget && !this.chrome.contains(widget)) {
            Box row = this.panel.rowOf(widget);
            this.scroll.reveal(row != null ? row : widget.bounds(), REVEAL_MARGIN, this.now());
        }
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        KeyMapping toggle = this.session.definition().toggleKey();
        if (toggle != null && toggle.matches(event) && this.isInteractive()) {
            this.onClose();
            return true;
        }
        return super.keyPressed(event);
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
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        UiScale scale = this.uiScale();
        if (this.isInteractive() && this.scroll.press(scale.toDesign(event.x()), scale.toDesign(event.y()), this.now())) {
            return true;
        }
        return super.mouseClicked(event, doubleClick);
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
    protected int exitDuration() {
        return Motion.WINDOW_OUT;
    }

    @Override
    public void onClose() {
        this.leave(() -> this.minecraft.gui.setScreen(null));
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
    }
}
