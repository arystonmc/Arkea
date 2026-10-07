package com.aryston.arkea.ui.screen;

import com.aryston.arkea.ui.anim.Motion;
import com.aryston.arkea.ui.anim.Timeline;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.layout.UiScale;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import com.aryston.arkea.ui.widget.ArkButton;
import com.aryston.arkea.ui.widget.ArkIconButton;
import com.aryston.arkea.ui.widget.ArkNavItem;
import com.aryston.arkea.ui.widget.ArkWidget;
import com.aryston.arkea.ui.widget.IconButtonStyle;
import com.aryston.arkea.ui.widget.NavEntry;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public abstract class ArkWindowScreen extends ArkScreen {
    private static final float WINDOW_WIDTH = 1238.0F;
    private static final float WINDOW_HEIGHT = 704.0F;
    private static final float SIDEBAR_WIDTH = 232.0F;
    private static final float SIDEBAR_TOP = 28.0F;
    private static final float SIDEBAR_INSET = 20.0F;
    private static final float BRAND_BOX = 32.0F;
    private static final float BRAND_ICON = 16.0F;
    private static final float BRAND_INSET = 8.0F;
    private static final float BRAND_GAP = 12.0F;
    private static final float BRAND_LINE_GAP = 4.0F;
    private static final float BRAND_TO_NAV = 28.0F;
    private static final float GROUP_LABEL_HEIGHT = 20.0F;
    private static final float GROUP_LABEL_INSET = 10.0F;
    private static final float GROUP_LABEL_MARGIN = 2.0F;
    private static final float NAV_GAP = 2.0F;
    private static final float GROUP_GAP = 20.0F;
    private static final float HEADER_HEIGHT = 72.0F;
    private static final float HEADER_LEFT = 20.0F;
    private static final float HEADER_RIGHT = 24.0F;
    private static final float HEADER_GAP = 14.0F;
    private static final float BACK_SIZE = 32.0F;
    private static final float CLOSE_SIZE = 30.0F;
    private static final float CRUMB_GAP = 6.0F;
    private static final float FOOTER_HEIGHT = 64.0F;
    private static final float FOOTER_INSET = 32.0F;
    private static final float FOOTER_GAP = 8.0F;
    private static final float NOTE_GAP = 10.0F;
    private static final float CHECK_WIDTH = 10.0F;
    private static final float CHECK_HEIGHT = 8.0F;
    private static final float CONTENT_INSET_X = 32.0F;
    private static final float CONTENT_INSET_Y = 24.0F;
    private static final float SHADOW_BLUR = 90.0F;
    private static final float SHADOW_OFFSET = 30.0F;
    private static final float WINDOW_SLIDE = 16.0F;
    private static final float WINDOW_START_SCALE = 0.98F;
    private static final float ROW_SLIDE = 10.0F;
    private static final int ROW_IN = 420;
    private static final int ROW_STAGGER = 30;
    private static final float TITLE_SLIDE = 8.0F;
    private static final int TITLE_IN = 300;
    private static final int WINDOW_FILL = ArkColors.rgba(20, 20, 22, 0.86F);
    private static final int DIM = ArkColors.rgba(8, 8, 9, 0.55F);
    private static final int FOOTER_FILL = ArkColors.rgba(0, 0, 0, 0.12F);
    private static final int BRAND_FILL = ArkColors.rgba(255, 255, 255, 0.05F);
    private static final TextStyle BRAND_TITLE = TextStyle.of(16.0F).spacing(1.0F);
    private static final TextStyle BRAND_SUBTITLE = TextStyle.of(10.0F);
    private static final TextStyle GROUP_LABEL = TextStyle.of(10.0F).spacing(1.0F);
    private static final TextStyle CRUMB = TextStyle.of(10.0F).spacing(1.0F);
    private static final TextStyle TITLE = TextStyle.of(20.0F);
    private static final TextStyle NOTE = TextStyle.of(11.0F);

    protected final Screen lastScreen;
    private final List<ArkWidget> chrome = new ArrayList<>();
    private final List<GroupLabel> groupLabels = new ArrayList<>();
    private Box window = Box.EMPTY;
    private @Nullable Box sidebar;
    private Box header = Box.EMPTY;
    private Box content = Box.EMPTY;
    private Box footer = Box.EMPTY;

    protected ArkWindowScreen(Component title, Screen lastScreen) {
        super(title);
        this.lastScreen = lastScreen;
    }

    protected abstract Component crumb();

    protected abstract @Nullable SidebarBrand brand();

    protected abstract List<NavGroup> navigation();

    protected abstract String currentNav();

    protected abstract void openNav(NavEntry entry);

    protected abstract void buildContent(Box area);

    protected abstract void renderContent(UiGraphics graphics, float mouseX, float mouseY);

    protected abstract Component footerNote();

    protected abstract List<ArkButton> footerButtons();

    @Override
    protected final void buildUi() {
        UiScale scale = this.uiScale();
        this.chrome.clear();
        this.groupLabels.clear();
        this.window = new Box((scale.canvasWidth() - WINDOW_WIDTH) * 0.5F, (scale.canvasHeight() - WINDOW_HEIGHT) * 0.5F, WINDOW_WIDTH, WINDOW_HEIGHT);
        boolean hasSidebar = this.brand() != null;
        this.sidebar = hasSidebar ? new Box(this.window.x(), this.window.y(), SIDEBAR_WIDTH, WINDOW_HEIGHT) : null;
        float mainX = hasSidebar ? this.window.x() + SIDEBAR_WIDTH : this.window.x();
        float mainWidth = this.window.right() - mainX;
        this.header = new Box(mainX, this.window.y(), mainWidth, HEADER_HEIGHT);
        this.footer = new Box(mainX, this.window.bottom() - FOOTER_HEIGHT, mainWidth, FOOTER_HEIGHT);
        float contentTop = this.header.bottom() + CONTENT_INSET_Y;
        this.content = new Box(mainX + CONTENT_INSET_X, contentTop, mainWidth - CONTENT_INSET_X * 2.0F,
            this.footer.y() - CONTENT_INSET_Y - contentTop);
        this.buildContent(this.content);
        this.buildFooter();
        if (this.sidebar != null) {
            this.buildSidebar(this.sidebar);
        }
        this.buildHeader();
    }

    private void buildFooter() {
        float x = this.footer.right() - FOOTER_INSET;
        List<ArkButton> buttons = this.footerButtons();
        for (int index = buttons.size() - 1; index >= 0; index--) {
            ArkButton button = buttons.get(index);
            float width = button.preferredWidth();
            x -= width;
            button.setBounds(new Box(x, this.footer.centerY() - ArkButton.HEIGHT * 0.5F, width, ArkButton.HEIGHT));
            x -= FOOTER_GAP;
        }
        for (ArkButton button : buttons) {
            this.chrome.add(this.add(button));
        }
    }

    private void buildSidebar(Box area) {
        float x = area.x() + SIDEBAR_INSET;
        float width = area.width() - SIDEBAR_INSET * 2.0F;
        float y = area.y() + SIDEBAR_TOP + BRAND_BOX + BRAND_TO_NAV;
        String current = this.currentNav();
        for (NavGroup group : this.navigation()) {
            this.groupLabels.add(new GroupLabel(group.label(), x + GROUP_LABEL_INSET, y));
            y += GROUP_LABEL_HEIGHT + GROUP_LABEL_MARGIN;
            for (NavEntry entry : group.entries()) {
                ArkNavItem item = this.add(new ArkNavItem(this, entry, entry.id().equals(current), () -> this.openNav(entry)));
                item.setBounds(new Box(x, y, width, ArkNavItem.HEIGHT));
                this.chrome.add(item);
                y += ArkNavItem.HEIGHT + NAV_GAP;
            }
            y += GROUP_GAP - NAV_GAP;
        }
    }

    private void buildHeader() {
        ArkIconButton back = this.add(new ArkIconButton(this, Icons.CHEVRON_LEFT, CommonComponents.GUI_BACK, IconButtonStyle.BACK, this::onClose));
        back.setBounds(new Box(this.header.x() + HEADER_LEFT, this.header.centerY() - BACK_SIZE * 0.5F, BACK_SIZE, BACK_SIZE));
        this.chrome.add(back);
        Component closeLabel = Component.translatable("arkea.window.close");
        ArkIconButton close = this.add(new ArkIconButton(this, Icons.CLOSE, closeLabel, IconButtonStyle.CLOSE, this::closeAll));
        close.setBounds(new Box(this.header.right() - HEADER_RIGHT - CLOSE_SIZE, this.header.centerY() - CLOSE_SIZE * 0.5F, CLOSE_SIZE, CLOSE_SIZE));
        close.setTooltip(closeLabel);
        this.chrome.add(close);
    }

    @Override
    protected final void renderUi(UiGraphics graphics, float mouseX, float mouseY) {
        float enter = Timeline.enter(this.sinceOpened(), 0, Motion.WINDOW_IN);
        float exit = this.isLeaving() ? Timeline.exit(this.sinceLeft(), Motion.WINDOW_OUT) : 1.0F;
        float progress = enter * exit;
        UiScale scale = graphics.scale();
        graphics.fill(0.0F, 0.0F, scale.canvasWidth(), scale.canvasHeight(), ArkColors.multiplyAlpha(DIM, progress));
        graphics.push();
        graphics.fade(progress);
        graphics.translate(0.0F, WINDOW_SLIDE * (1.0F - progress));
        graphics.scaleAround(WINDOW_START_SCALE + (1.0F - WINDOW_START_SCALE) * progress, this.window.centerX(), this.window.centerY());
        this.renderFrame(graphics);
        if (this.sidebar != null) {
            this.renderSidebar(graphics, this.sidebar);
        }
        this.renderHeader(graphics);
        this.renderContent(graphics, mouseX, mouseY);
        this.renderFooter(graphics);
        for (ArkWidget widget : this.chrome) {
            widget.render(graphics, mouseX, mouseY);
        }
        graphics.pop();
    }

    protected void renderRow(UiGraphics graphics, int index, Runnable draw) {
        float enter = Timeline.enter(this.sinceOpened(), index * ROW_STAGGER, ROW_IN);
        graphics.push();
        graphics.fade(enter);
        graphics.translate(0.0F, ROW_SLIDE * (1.0F - enter));
        draw.run();
        graphics.pop();
    }

    private void renderFrame(UiGraphics graphics) {
        graphics.shadow(this.window, SHADOW_BLUR, SHADOW_OFFSET, ArkColors.SHADOW_WINDOW);
        graphics.fill(this.window, WINDOW_FILL);
        graphics.border(this.window, 1.0F, ArkColors.BORDER_DEFAULT);
        graphics.topHighlight(this.window, ArkColors.INNER_HIGHLIGHT);
    }

    private void renderSidebar(UiGraphics graphics, Box area) {
        float line = graphics.scale().snapThickness(1.0F);
        Box inner = new Box(area.x() + line, area.y() + line, area.width() - line, area.height() - line * 2.0F);
        graphics.fill(inner, ArkColors.SIDEBAR);
        graphics.fill(area.right() - line, inner.y(), line, inner.height(), ArkColors.BORDER_SUBTLE);
        SidebarBrand brand = this.brand();
        if (brand != null) {
            this.renderBrand(graphics, area, brand);
        }
        TextMetrics metrics = graphics.metrics();
        for (GroupLabel label : this.groupLabels) {
            float y = label.y() + (GROUP_LABEL_HEIGHT - metrics.capHeight(GROUP_LABEL)) * 0.5F;
            graphics.text(label.text().getString(), label.x(), y, GROUP_LABEL, ArkColors.TEXT_LABEL);
        }
    }

    private void renderBrand(UiGraphics graphics, Box area, SidebarBrand brand) {
        Box box = new Box(area.x() + SIDEBAR_INSET + BRAND_INSET, area.y() + SIDEBAR_TOP, BRAND_BOX, BRAND_BOX);
        graphics.fill(box, BRAND_FILL);
        graphics.border(box, 1.0F, ArkColors.BORDER_STRONG);
        float iconOffset = (BRAND_BOX - BRAND_ICON) * 0.5F;
        graphics.icon(brand.icon(), box.x() + iconOffset, box.y() + iconOffset, BRAND_ICON, BRAND_ICON, ArkColors.TEXT_SOFT);
        TextMetrics metrics = graphics.metrics();
        float blockHeight = metrics.capHeight(BRAND_TITLE) + BRAND_LINE_GAP + metrics.capHeight(BRAND_SUBTITLE);
        float textX = box.right() + BRAND_GAP;
        float titleY = box.centerY() - blockHeight * 0.5F;
        graphics.text(brand.title().getString(), textX, titleY, BRAND_TITLE, ArkColors.TEXT_PRIMARY);
        float subtitleY = titleY + metrics.capHeight(BRAND_TITLE) + BRAND_LINE_GAP;
        graphics.text(brand.subtitle().getString(), textX, subtitleY, BRAND_SUBTITLE, ArkColors.TEXT_FAINT);
    }

    private void renderHeader(UiGraphics graphics) {
        float line = graphics.scale().snapThickness(1.0F);
        graphics.fill(this.header.x(), this.header.bottom() - line, this.header.width() - line, line, ArkColors.BORDER_SUBTLE);
        TextMetrics metrics = graphics.metrics();
        float textX = this.header.x() + HEADER_LEFT + BACK_SIZE + HEADER_GAP;
        float blockHeight = metrics.capHeight(CRUMB) + CRUMB_GAP + metrics.capHeight(TITLE);
        float crumbY = this.header.centerY() - blockHeight * 0.5F;
        this.renderTitleText(graphics, () -> {
            graphics.text(this.crumb().getString(), textX, crumbY, CRUMB, ArkColors.TEXT_LABEL);
            float titleY = crumbY + metrics.capHeight(CRUMB) + CRUMB_GAP;
            graphics.text(this.title.getString(), textX, titleY, TITLE, ArkColors.TEXT_PRIMARY);
        });
    }

    private void renderFooter(UiGraphics graphics) {
        float line = graphics.scale().snapThickness(1.0F);
        Box inner = new Box(this.footer.x(), this.footer.y(), this.footer.width() - line, this.footer.height() - line);
        graphics.fill(inner, FOOTER_FILL);
        graphics.fill(inner.x(), inner.y(), inner.width(), line, ArkColors.BORDER_SUBTLE);
        float x = this.footer.x() + FOOTER_INSET;
        float y = this.footer.centerY();
        this.renderTitleText(graphics, () -> {
            graphics.icon(Icons.CHECK, x, y - CHECK_HEIGHT * 0.5F, CHECK_WIDTH, CHECK_HEIGHT, Theme.accent().light());
            float textY = y - graphics.metrics().capHeight(NOTE) * 0.5F;
            graphics.text(this.footerNote().getString(), x + CHECK_WIDTH + NOTE_GAP, textY, NOTE, ArkColors.TEXT_FAINT);
        });
    }

    private void renderTitleText(UiGraphics graphics, Runnable draw) {
        float enter = Timeline.enter(this.sinceOpened(), 0, TITLE_IN);
        graphics.push();
        graphics.fade(enter);
        graphics.translate(-TITLE_SLIDE * (1.0F - enter), 0.0F);
        draw.run();
        graphics.pop();
    }

    @Override
    protected int exitDuration() {
        return Motion.WINDOW_OUT;
    }

    @Override
    public void onClose() {
        this.navigate(() -> this.lastScreen);
    }

    private void closeAll() {
        if (this.minecraft.level != null) {
            this.leave(() -> this.minecraft.gui.setScreen(null));
            return;
        }
        this.navigate(TitleScreen::new);
    }

    @Override
    protected boolean blursBackground() {
        return this.menuBlurEnabled();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        if (this.minecraft.level == null) {
            this.minecraft.gameRenderer.panorama().extractRenderState(graphics, this.width, this.height);
        }
        if (this.blursBackground()) {
            graphics.blurBeforeThisStratum();
        }
    }

    private record GroupLabel(Component text, float x, float y) {
    }
}
