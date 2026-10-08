package com.aryston.arkea.screen.game;

import com.aryston.arkea.config.ArkeaConfig;
import com.aryston.arkea.mixin.DeathScreenAccessor;
import com.aryston.arkea.screen.vanilla.VanillaSkin;
import com.aryston.arkea.ui.anim.Timeline;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.widget.ButtonVariant;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public final class DeathSkin extends VanillaSkin {
    private static final float TITLE_TO_CAUSE = 26.0F;
    private static final float CAUSE_TO_SCORE = 18.0F;
    private static final float SCORE_TO_BUTTONS = 30.0F;
    private static final float BUTTON_WIDTH = 320.0F;
    private static final float RESPAWN_HEIGHT = 48.0F;
    private static final float BUTTON_HEIGHT = 40.0F;
    private static final float BUTTON_GAP = 8.0F;
    private static final float TEXT_WIDTH = 600.0F;
    private static final float LINE = 18.0F;
    private static final float RISE = 10.0F;
    private static final float TITLE_SCALE_START = 1.08F;
    private static final int TITLE_IN = 600;
    private static final int CAUSE_DELAY = 500;
    private static final int SCORE_DELAY = 700;
    private static final int BUTTONS_DELAY = 800;
    private static final int TEXT_IN = 400;
    private static final int TINT = ArkColors.rgba(90, 10, 10, 0.35F);
    private static final int VIGNETTE = ArkColors.rgba(60, 0, 0, 0.85F);
    private static final int CAUSE = ArkColors.rgb(0xF0C8C0);
    private static final int TITLE_SHADOW = ArkColors.rgba(60, 0, 0, 0.8F);
    private static final TextStyle TITLE = TextStyle.of(52.0F).spacing(2.0F).shadow(TITLE_SHADOW, 4.0F);
    private static final TextStyle CAUSE_TEXT = TextStyle.of(13.0F);
    private static final TextStyle SCORE = TextStyle.of(12.0F);

    @Override
    public boolean enabled() {
        return ArkeaConfig.IN_GAME_SCREENS.get();
    }

    @Override
    public boolean handles(Screen screen) {
        return screen instanceof DeathScreen;
    }

    @Override
    protected void draw(Frame frame, Screen screen) {
        UiGraphics graphics = frame.graphics();
        TextMetrics metrics = graphics.metrics();
        Box canvas = new Box(0.0F, 0.0F, frame.scale().canvasWidth(), frame.scale().canvasHeight());
        float shade = Timeline.enter(frame.since(), 0, TITLE_IN);
        graphics.push();
        graphics.fade(shade);
        graphics.fill(canvas, TINT);
        graphics.vignette(canvas, VIGNETTE);
        graphics.pop();
        DeathScreenAccessor accessor = (DeathScreenAccessor) screen;
        Component cause = accessor.arkea$causeOfDeath();
        List<String> causeLines = cause == null ? List.of() : metrics.wrap(cause.getString(), CAUSE_TEXT, TEXT_WIDTH);
        List<AbstractButton> buttons = new ArrayList<>();
        for (GuiEventListener child : screen.children()) {
            if (child instanceof AbstractButton button && button.visible) {
                buttons.add(button);
            }
        }
        Component location = location();
        float height = metrics.capHeight(TITLE) + TITLE_TO_CAUSE + causeLines.size() * LINE + CAUSE_TO_SCORE + metrics.capHeight(SCORE)
            + (location != null ? LINE : 0.0F) + SCORE_TO_BUTTONS + RESPAWN_HEIGHT + (buttons.size() - 1) * (BUTTON_HEIGHT + BUTTON_GAP);
        float centerX = canvas.centerX();
        float y = (canvas.height() - height) * 0.5F;
        String title = screen.getTitle().getString();
        float titleIn = Timeline.enter(frame.since(), 0, TITLE_IN);
        graphics.push();
        graphics.fade(titleIn);
        graphics.scaleAround(TITLE_SCALE_START + (1.0F - TITLE_SCALE_START) * titleIn, centerX, y + metrics.capHeight(TITLE) * 0.5F);
        graphics.text(title, centerX - metrics.width(title, TITLE) * 0.5F, y, TITLE, ArkColors.TEXT_PRIMARY);
        graphics.pop();
        y += metrics.capHeight(TITLE) + TITLE_TO_CAUSE;
        float causeY = y;
        late(frame, CAUSE_DELAY, () -> {
            float lineY = causeY;
            for (String line : causeLines) {
                graphics.text(line, centerX - metrics.width(line, CAUSE_TEXT) * 0.5F, lineY, CAUSE_TEXT, CAUSE);
                lineY += LINE;
            }
        });
        y += causeLines.size() * LINE + CAUSE_TO_SCORE;
        Component score = accessor.arkea$deathScore();
        float scoreY = y;
        late(frame, SCORE_DELAY, () -> graphics.richText(score, centerX - metrics.width(score.getString(), SCORE) * 0.5F, scoreY, TEXT_WIDTH, SCORE,
            ArkColors.TEXT_SOFT));
        if (location != null) {
            float locationY = y + LINE;
            late(frame, SCORE_DELAY, () -> graphics.text(location.getString(), centerX - metrics.width(location.getString(), SCORE) * 0.5F, locationY, SCORE,
                ArkColors.TEXT_DESCRIPTION));
            y += LINE;
        }
        y += metrics.capHeight(SCORE) + SCORE_TO_BUTTONS;
        float buttonsY = y;
        late(frame, BUTTONS_DELAY, () -> this.drawButtons(frame, buttons, centerX - BUTTON_WIDTH * 0.5F, buttonsY));
    }

    private static @Nullable Component location() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.showOnlyReducedInfo() || !ArkeaConfig.on(ArkeaConfig.DEATH_POINT)) {
            return null;
        }
        BlockPos pos = minecraft.player.blockPosition();
        return Component.translatable("arkea.death.location", pos.getX(), pos.getY(), pos.getZ());
    }

    private void drawButtons(Frame frame, List<AbstractButton> buttons, float x, float top) {
        float y = top;
        for (int index = 0; index < buttons.size(); index++) {
            AbstractButton button = buttons.get(index);
            boolean first = index == 0;
            float height = first ? RESPAWN_HEIGHT : BUTTON_HEIGHT;
            frame.button(button, new Box(x, y, BUTTON_WIDTH, height), first ? ButtonVariant.PRIMARY : ButtonVariant.SECONDARY, first ? Icons.RESET : null,
                null);
            y += height + BUTTON_GAP;
        }
    }

    private static void late(Frame frame, int delay, Runnable draw) {
        float progress = Timeline.enter(frame.since(), delay, TEXT_IN);
        UiGraphics graphics = frame.graphics();
        graphics.push();
        graphics.fade(progress);
        graphics.translate(0.0F, RISE * (1.0F - progress));
        draw.run();
        graphics.pop();
    }
}
