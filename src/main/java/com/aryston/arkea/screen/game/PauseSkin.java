package com.aryston.arkea.screen.game;

import com.aryston.arkea.config.ArkeaConfig;
import com.aryston.arkea.screen.vanilla.VanillaSkin;
import com.aryston.arkea.ui.anim.Timeline;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.Icon;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.widget.ButtonVariant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.FriendsButton;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.neoforged.neoforge.client.gui.widget.ModsButton;
import org.jspecify.annotations.Nullable;

public final class PauseSkin extends VanillaSkin {
    private static final float WIDTH = 780.0F;
    private static final float COLUMN_GAP = 20.0F;
    private static final float TITLE_GAP = 10.0F;
    private static final float BLOCK_GAP = 18.0F;
    private static final float GRID_GAP = 8.0F;
    private static final float BACK_HEIGHT = 52.0F;
    private static final float TILE_HEIGHT = 44.0F;
    private static final float QUIT_HEIGHT = 40.0F;
    private static final float ICON_BUTTON = 30.0F;
    private static final float ICON_BUTTON_GAP = 6.0F;
    private static final float TILE_PADDING = 14.0F;
    private static final float TILE_ICON = 14.0F;
    private static final float TILE_ICON_GAP = 12.0F;
    private static final float EXTERNAL = 10.0F;
    private static final float SMALL_ICON = 14.0F;
    private static final float ROW_RISE = 8.0F;
    private static final float TITLE_SHADOW = 3.0F;
    private static final float HOVER_BRIGHTNESS = 0.25F;
    private static final float DISABLED_OPACITY = 0.4F;
    private static final float KICKER_SHARE = 0.6F;
    private static final int ROW_IN = 300;
    private static final int ROW_STAGGER = 40;
    private static final int CARD_DELAY = 120;
    private static final int TILE_FILL = ArkColors.rgba(16, 16, 18, 0.80F);
    private static final int TILE_BORDER = ArkColors.rgba(255, 255, 255, 0.10F);
    private static final int TILE_BORDER_HOVER = ArkColors.rgba(255, 255, 255, 0.22F);
    private static final int QUIT_FILL = ArkColors.withAlpha(ArkColors.DANGER, 0.16F);
    private static final int QUIT_FILL_HOVER = ArkColors.withAlpha(ArkColors.DANGER, 0.30F);
    private static final int QUIT_BORDER = ArkColors.withAlpha(ArkColors.DANGER_BORDER, 0.5F);
    private static final TextStyle KICKER = TextStyle.of(10.0F).spacing(2.0F);
    private static final int TITLE_SHADOW_COLOR = ArkColors.rgba(0, 0, 0, 0.45F);
    private static final TextStyle TITLE = TextStyle.of(32.0F).shadow(TITLE_SHADOW_COLOR, TITLE_SHADOW);
    private static final TextStyle LABEL = TextStyle.of(12.0F);
    private static final Map<String, Icon> TILE_ICONS = Map.of(
        "gui.advancements", Icons.SPARKLE,
        "gui.stats", Icons.GAUGE,
        "menu.options", Icons.SLIDERS,
        "options.worldOptions.button", Icons.WORLD,
        "arkea.pause.helion", Icons.SPARKLE);
    private static final Map<String, Icon> SMALL_ICONS = Map.of(
        "menu.reportBugs", Icons.WARNING,
        "menu.sendFeedback", Icons.CHAT,
        "menu.playerReporting", Icons.INFO);
    private static final String RETURN_KEY = "menu.returnToGame";
    private static final String OPTIONS_KEY = "menu.options";
    private static final List<Component> QUIT_LABELS = List.of(CommonComponents.GUI_RETURN_TO_MENU, CommonComponents.GUI_DISCONNECT);

    private @Nullable Screen cardOwner;
    private PauseCard card = new PauseCard();

    @Override
    public boolean enabled() {
        return ArkeaConfig.IN_GAME_SCREENS.get();
    }

    @Override
    public boolean handles(Screen screen) {
        return screen instanceof PauseScreen pause && pause.showsPauseMenu();
    }

    @Override
    protected void draw(Frame frame, Screen screen) {
        if (screen != this.cardOwner) {
            this.card.close();
            this.card = new PauseCard();
            this.cardOwner = screen;
        }
        Layout layout = classify(screen);
        TextMetrics metrics = frame.graphics().metrics();
        List<List<Tile>> rows = rows(layout.tiles());
        float headerHeight = metrics.capHeight(KICKER) + TITLE_GAP + metrics.capHeight(TITLE);
        float leftHeight = headerHeight + BLOCK_GAP + BACK_HEIGHT + rows.size() * (TILE_HEIGHT + GRID_GAP);
        if (layout.quit() != null) {
            leftHeight += BLOCK_GAP - GRID_GAP + QUIT_HEIGHT;
        }
        float height = Math.max(leftHeight, PauseCard.height());
        float left = (frame.scale().canvasWidth() - WIDTH) * 0.5F;
        float top = (frame.scale().canvasHeight() - height) * 0.5F;
        float columnWidth = WIDTH - PauseCard.WIDTH - COLUMN_GAP;
        int row = 0;
        float y = top;
        this.enter(frame, row++, () -> this.drawHeader(frame, layout, left, top, columnWidth));
        y += headerHeight + BLOCK_GAP;
        if (layout.back() != null) {
            Box back = new Box(left, y, columnWidth, BACK_HEIGHT);
            AbstractButton button = layout.back();
            this.enter(frame, row++, () -> frame.button(button, back, ButtonVariant.PRIMARY, Icons.PLAY, null));
        }
        y += BACK_HEIGHT + GRID_GAP;
        for (List<Tile> tiles : rows) {
            float tileWidth = (columnWidth - GRID_GAP * (tiles.size() - 1)) / tiles.size();
            float x = left;
            float rowY = y;
            for (Tile tile : tiles) {
                Box box = new Box(x, rowY, tileWidth, TILE_HEIGHT);
                this.enter(frame, row, () -> this.drawTile(frame, tile, box));
                x += tileWidth + GRID_GAP;
            }
            row++;
            y += TILE_HEIGHT + GRID_GAP;
        }
        if (layout.quit() != null) {
            Box quit = new Box(left, y - GRID_GAP + BLOCK_GAP, columnWidth, QUIT_HEIGHT);
            AbstractButton button = layout.quit();
            this.enter(frame, row, () -> this.drawQuit(frame, button, quit));
        }
        Box cardBox = new Box(left + columnWidth + COLUMN_GAP, top, PauseCard.WIDTH, PauseCard.height());
        float cardEnter = Timeline.enter(frame.since(), CARD_DELAY, ROW_IN);
        UiGraphics graphics = frame.graphics();
        graphics.push();
        graphics.fade(cardEnter);
        graphics.translate(0.0F, ROW_RISE * (1.0F - cardEnter));
        this.card.draw(graphics, cardBox);
        graphics.pop();
    }

    private void enter(Frame frame, int index, Runnable draw) {
        float progress = Timeline.enter(frame.since(), index * ROW_STAGGER, ROW_IN);
        UiGraphics graphics = frame.graphics();
        graphics.push();
        graphics.fade(progress);
        graphics.translate(0.0F, ROW_RISE * (1.0F - progress));
        draw.run();
        graphics.pop();
    }

    private void drawHeader(Frame frame, Layout layout, float left, float top, float width) {
        UiGraphics graphics = frame.graphics();
        TextMetrics metrics = graphics.metrics();
        String kicker = PauseCard.worldName().getString().toUpperCase(Locale.ROOT);
        graphics.text(metrics.ellipsize(kicker, KICKER, width * KICKER_SHARE), left, top, KICKER, ArkColors.TEXT_MUTED);
        float titleY = top + metrics.capHeight(KICKER) + TITLE_GAP;
        graphics.text(Component.translatable("arkea.pause.title").getString(), left, titleY, TITLE, ArkColors.TEXT_PRIMARY);
        float x = left + width;
        float iconY = titleY + metrics.capHeight(TITLE) - ICON_BUTTON;
        for (int index = layout.small().size() - 1; index >= 0; index--) {
            AbstractButton button = layout.small().get(index);
            x -= ICON_BUTTON;
            Box box = new Box(x, iconY, ICON_BUTTON, ICON_BUTTON);
            this.drawSmall(frame, button, box);
            x -= ICON_BUTTON_GAP;
        }
    }

    private void drawSmall(Frame frame, AbstractButton button, Box box) {
        UiGraphics graphics = frame.graphics();
        frame.place(button, box);
        boolean highlighted = frame.highlighted(button);
        graphics.push();
        if (!button.active) {
            graphics.fade(DISABLED_OPACITY);
        }
        graphics.fill(box, highlighted ? ArkColors.brighten(TILE_FILL, 1.0F + HOVER_BRIGHTNESS) : TILE_FILL);
        graphics.border(box, 1.0F, highlighted ? TILE_BORDER_HOVER : TILE_BORDER);
        graphics.icon(smallIcon(button), box.centerX() - SMALL_ICON * 0.5F, box.centerY() - SMALL_ICON * 0.5F, SMALL_ICON, SMALL_ICON,
            highlighted ? ArkColors.TEXT_PRIMARY : ArkColors.TEXT_SOFT);
        graphics.pop();
        frame.focusRing(button, box);
    }

    private void drawTile(Frame frame, Tile tile, Box box) {
        UiGraphics graphics = frame.graphics();
        AbstractButton button = tile.button();
        frame.place(button, box);
        boolean highlighted = frame.highlighted(button);
        graphics.push();
        if (!button.active) {
            graphics.fade(DISABLED_OPACITY);
        }
        graphics.fill(box, highlighted ? ArkColors.brighten(TILE_FILL, 1.0F + HOVER_BRIGHTNESS) : TILE_FILL);
        graphics.border(box, 1.0F, highlighted ? TILE_BORDER_HOVER : TILE_BORDER);
        int color = highlighted ? ArkColors.TEXT_PRIMARY : ArkColors.TEXT_SOFT;
        float x = box.x() + TILE_PADDING;
        graphics.icon(tile.icon(), x, box.centerY() - TILE_ICON * 0.5F, TILE_ICON, TILE_ICON, color);
        x += TILE_ICON + TILE_ICON_GAP;
        TextMetrics metrics = graphics.metrics();
        float right = box.right() - TILE_PADDING - (tile.external() ? EXTERNAL + TILE_ICON_GAP : 0.0F);
        String label = metrics.ellipsize(button.getMessage().getString(), LABEL, right - x);
        graphics.text(label, x, box.centerY() - metrics.capHeight(LABEL) * 0.5F, LABEL, ArkColors.TEXT_PRIMARY);
        if (tile.external()) {
            graphics.icon(Icons.EXTERNAL, box.right() - TILE_PADDING - EXTERNAL, box.centerY() - EXTERNAL * 0.5F, EXTERNAL, EXTERNAL, color);
        }
        graphics.pop();
        frame.focusRing(button, box);
    }

    private void drawQuit(Frame frame, AbstractButton button, Box box) {
        UiGraphics graphics = frame.graphics();
        frame.place(button, box);
        boolean highlighted = frame.highlighted(button);
        graphics.push();
        if (!button.active) {
            graphics.fade(DISABLED_OPACITY);
        }
        graphics.fill(box, highlighted ? QUIT_FILL_HOVER : QUIT_FILL);
        graphics.border(box, 1.0F, QUIT_BORDER);
        TextMetrics metrics = graphics.metrics();
        String label = button.getMessage().getString();
        float contentWidth = TILE_ICON + TILE_ICON_GAP + metrics.width(label, LABEL);
        float x = box.centerX() - contentWidth * 0.5F;
        int color = highlighted ? ArkColors.TEXT_PRIMARY : ArkColors.DANGER_TEXT;
        graphics.icon(Icons.POWER, x, box.centerY() - TILE_ICON * 0.5F, TILE_ICON, TILE_ICON, color);
        graphics.text(label, x + TILE_ICON + TILE_ICON_GAP, box.centerY() - metrics.capHeight(LABEL) * 0.5F, LABEL, color);
        graphics.pop();
        frame.focusRing(button, box);
    }

    private static Layout classify(Screen screen) {
        List<AbstractButton> buttons = new ArrayList<>();
        for (GuiEventListener child : screen.children()) {
            if (child instanceof AbstractButton button && button.visible) {
                buttons.add(button);
            }
        }
        AbstractButton back = null;
        AbstractButton quit = null;
        List<Tile> tiles = new ArrayList<>();
        List<AbstractButton> small = new ArrayList<>();
        Tile mods = null;
        for (AbstractButton button : buttons) {
            String key = key(button);
            if (RETURN_KEY.equals(key)) {
                back = button;
            } else if (QUIT_LABELS.contains(button.getMessage())) {
                quit = button;
            } else if (button instanceof ModsButton) {
                mods = new Tile(button, Icons.PACK, true, true);
            } else if (button instanceof SpriteIconButton) {
                small.add(button);
            } else if (TILE_ICONS.containsKey(key)) {
                tiles.add(new Tile(button, TILE_ICONS.get(key), OPTIONS_KEY.equals(key) || PauseExtras.HELION.equals(button.getMessage()), false));
            } else {
                tiles.add(new Tile(button, Icons.ARROW_R, false, true));
            }
        }
        if (mods != null) {
            tiles.add(mods);
        }
        return new Layout(back, tiles, small, quit);
    }

    private static List<List<Tile>> rows(List<Tile> tiles) {
        List<List<Tile>> rows = new ArrayList<>();
        List<Tile> pending = new ArrayList<>();
        for (Tile tile : tiles) {
            if (tile.wide()) {
                if (!pending.isEmpty()) {
                    rows.add(List.copyOf(pending));
                    pending.clear();
                }
                rows.add(List.of(tile));
                continue;
            }
            pending.add(tile);
            if (pending.size() == 2) {
                rows.add(List.copyOf(pending));
                pending.clear();
            }
        }
        if (!pending.isEmpty()) {
            rows.add(List.copyOf(pending));
        }
        return rows;
    }

    private static String key(AbstractButton button) {
        return button.getMessage().getContents() instanceof TranslatableContents contents ? contents.getKey() : "";
    }

    private static Icon smallIcon(AbstractButton button) {
        if (button instanceof FriendsButton) {
            return Icons.USER;
        }
        return SMALL_ICONS.getOrDefault(key(button), Icons.INFO);
    }

    private record Tile(AbstractButton button, Icon icon, boolean external, boolean wide) {
    }

    private record Layout(@Nullable AbstractButton back, List<Tile> tiles, List<AbstractButton> small, @Nullable AbstractButton quit) {
    }
}
