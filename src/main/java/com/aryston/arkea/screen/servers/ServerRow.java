package com.aryston.arkea.screen.servers;

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
import java.util.List;
import net.minecraft.client.Minecraft;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

final class ServerRow extends ArkWidget {
    static final float HEIGHT = 64.0F;
    static final float ACTION_SIZE = 24.0F;
    static final float ACTION_GAP = 4.0F;
    private static final float PADDING_LEFT = 12.0F;
    private static final float PADDING_RIGHT = 18.0F;
    private static final float GAP = 14.0F;
    private static final float TILE = 40.0F;
    private static final float LINE_GAP = 8.0F;
    private static final float PING_WIDTH = 64.0F;
    private static final float BAR_WIDTH = 3.0F;
    private static final float BAR_GAP = 2.0F;
    private static final float BAR_AREA = 13.0F;
    private static final float PING_GAP = 6.0F;
    private static final float BAR_STEP = 3.0F;
    private static final float BAR_BASE = 1.0F;
    private static final int BARS = 4;
    private static final long GOOD_PING = 60L;
    private static final long FAIR_PING = 110L;
    private static final long SLOW_PING = 200L;
    private static final long PINGING_STEP_MS = 150L;
    private static final float SELECTED_FILL = 0.12F;
    private static final int BORDER = ArkColors.rgba(255, 255, 255, 0.07F);
    private static final int BAR_IDLE = ArkColors.rgba(255, 255, 255, 0.12F);
    private static final int BAR_OFFLINE = ArkColors.rgba(224, 112, 95, 0.4F);
    private static final TextStyle NAME = TextStyle.of(14.0F);
    private static final TextStyle MOTD = TextStyle.of(11.0F);
    private static final TextStyle PLAYERS = TextStyle.of(11.0F);
    private static final TextStyle PING = TextStyle.of(9.0F);

    private final ServerData data;
    private final Supplier<@Nullable Identifier> icon;
    private final BooleanSupplier selected;
    private final Runnable select;
    private final Runnable join;
    private final List<ArkWidget> actions;

    ServerRow(UiHost host, ServerData data, Supplier<@Nullable Identifier> icon, BooleanSupplier selected, Runnable select, Runnable join,
        List<ArkWidget> actions) {
        super(host);
        this.data = data;
        this.icon = icon;
        this.selected = selected;
        this.select = select;
        this.join = join;
        this.actions = actions;
    }

    @Override
    protected void renderWidget(UiGraphics graphics) {
        Box box = this.bounds();
        boolean isSelected = this.selected.getAsBoolean();
        int accent = Theme.accent().base();
        graphics.fill(box, isSelected ? ArkColors.withAlpha(accent, SELECTED_FILL) : ArkColors.lerp(this.hoverProgress(), ArkColors.ROW, ArkColors.ROW_HOVER));
        graphics.border(box, 1.0F, isSelected ? accent : BORDER);
        Box tile = new Box(box.x() + PADDING_LEFT, box.centerY() - TILE * 0.5F, TILE, TILE);
        this.renderTile(graphics, tile);
        TextMetrics metrics = graphics.metrics();
        float pingX = box.right() - PADDING_RIGHT - PING_WIDTH;
        float right = this.renderStatus(graphics, box, pingX);
        if (!this.actions.isEmpty()) {
            right = Math.min(right, this.actions.getFirst().bounds().x()) - GAP;
        }
        float textX = tile.right() + GAP;
        float block = metrics.capHeight(NAME) + LINE_GAP + metrics.capHeight(MOTD);
        float nameY = box.centerY() - block * 0.5F;
        Tag tag = this.tag();
        float tagWidth = tag != null ? Tag.GAP + tag.width(metrics) : 0.0F;
        String name = metrics.ellipsize(this.data.name, NAME, right - textX - tagWidth);
        graphics.text(name, textX, nameY, NAME, ArkColors.TEXT_PRIMARY);
        if (tag != null) {
            tag.draw(graphics, textX + metrics.width(name, NAME) + Tag.GAP, nameY + metrics.capHeight(NAME) * 0.5F);
        }
        float motdY = nameY + metrics.capHeight(NAME) + LINE_GAP;
        graphics.richText(this.motd(), textX, motdY, right - textX, MOTD, this.offline() ? ArkColors.ERROR : ArkColors.TEXT_MUTED);
    }

    void renderActions(UiGraphics graphics, float mouseX, float mouseY) {
        for (ArkWidget action : this.actions) {
            action.render(graphics, mouseX, mouseY);
        }
    }

    private void renderTile(UiGraphics graphics, Box tile) {
        Identifier texture = this.icon.get();
        if (texture != null) {
            graphics.image(texture, tile, 0.0F, 0.0F, 1.0F, 1.0F, ArkColors.TEXT_PRIMARY);
            return;
        }
        LetterTile.draw(graphics, tile, this.data.name, Minecraft.getInstance().getLanguageManager().getJavaLocale());
    }

    private float renderStatus(UiGraphics graphics, Box box, float pingX) {
        TextMetrics metrics = graphics.metrics();
        int bars = this.bars();
        int barColor = bars >= BARS - 1 ? Theme.accent().light() : ArkColors.WARNING;
        float barsWidth = BARS * BAR_WIDTH + (BARS - 1) * BAR_GAP;
        float columnRight = pingX + PING_WIDTH;
        float barsBottom = box.centerY() - PING_GAP * 0.5F + BAR_BASE;
        long pulse = Util.getMillis() / PINGING_STEP_MS % BARS;
        for (int index = 0; index < BARS; index++) {
            float height = (index + 1) * BAR_STEP + BAR_BASE;
            int color = this.offline() ? BAR_OFFLINE : this.pinging() ? (index == pulse ? ArkColors.TEXT_MUTED : BAR_IDLE)
                : index < bars ? barColor : BAR_IDLE;
            graphics.fill(columnRight - barsWidth + index * (BAR_WIDTH + BAR_GAP), barsBottom - height, BAR_WIDTH, height, color);
        }
        String ping = this.pingText().getString();
        graphics.text(ping, columnRight - metrics.width(ping, PING), barsBottom + PING_GAP, PING, this.offline() ? ArkColors.ERROR : ArkColors.TEXT_FAINT);
        Component players = this.playersText();
        if (players.getString().isEmpty()) {
            return pingX;
        }
        float width = metrics.width(players.getString(), PLAYERS);
        float x = pingX - GAP - width;
        graphics.richText(players, x, box.centerY() - metrics.capHeight(PLAYERS) * 0.5F, width + 1.0F, PLAYERS, ArkColors.TEXT_MUTED);
        return x;
    }

    private @Nullable Tag tag() {
        if (this.data.state() != ServerData.State.INCOMPATIBLE) {
            return null;
        }
        return Tag.tone(Component.translatable("arkea.servers.tag.incompatible"), ArkColors.WARNING);
    }

    private Component motd() {
        if (this.pinging()) {
            return Component.translatable("multiplayer.status.pinging");
        }
        if (this.data.state() == ServerData.State.INCOMPATIBLE) {
            return Component.translatable("arkea.servers.version", this.data.version);
        }
        return this.data.motd == null ? Component.empty() : this.data.motd;
    }

    private Component playersText() {
        boolean answered = this.data.state() == ServerData.State.SUCCESSFUL || this.data.state() == ServerData.State.INCOMPATIBLE;
        return answered && this.data.status != null ? this.data.status : Component.empty();
    }

    private Component pingText() {
        if (this.offline()) {
            return Component.translatable("arkea.servers.offline");
        }
        if (this.pinging()) {
            return Component.empty();
        }
        return Component.translatable("arkea.servers.ping", this.data.ping);
    }

    private int bars() {
        long ping = this.data.ping;
        if (this.offline() || this.pinging()) {
            return 0;
        }
        return ping < GOOD_PING ? BARS : ping < FAIR_PING ? BARS - 1 : ping < SLOW_PING ? BARS - 2 : 1;
    }

    private boolean offline() {
        return this.data.state() == ServerData.State.UNREACHABLE;
    }

    private boolean pinging() {
        return this.data.state() == ServerData.State.INITIAL || this.data.state() == ServerData.State.PINGING;
    }

    @Override
    public @Nullable Component tooltip() {
        if (this.data.state() != ServerData.State.SUCCESSFUL || this.data.playerList.isEmpty()) {
            return null;
        }
        MutableComponent players = Component.empty();
        for (int index = 0; index < this.data.playerList.size(); index++) {
            if (index > 0) {
                players.append(CommonComponents.NEW_LINE);
            }
            players.append(this.data.playerList.get(index));
        }
        return players;
    }

    @Override
    protected boolean containsPoint(float x, float y) {
        return super.containsPoint(x, y) && this.actions.stream().noneMatch(action -> action.hitBox().contains(x, y));
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        boolean handled = super.mouseClicked(event, doubleClick);
        if (handled && doubleClick) {
            this.join.run();
        }
        return handled;
    }

    @Override
    protected void onPress() {
        if (this.selected.getAsBoolean() && this.host.showsKeyboardFocus()) {
            this.join.run();
            return;
        }
        this.select.run();
    }

    @Override
    protected Component narrationMessage() {
        return Component.translatable("narrator.select", this.data.name);
    }
}
