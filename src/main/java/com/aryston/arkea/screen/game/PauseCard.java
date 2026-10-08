package com.aryston.arkea.screen.game;

import com.aryston.arkea.Arkea;
import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.render.Icons;
import com.aryston.arkea.ui.render.LetterTile;
import com.aryston.arkea.ui.render.TextMetrics;
import com.aryston.arkea.ui.render.TextStyle;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import com.aryston.arkea.ui.theme.Theme;
import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.FaviconTexture;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.GameType;
import org.jspecify.annotations.Nullable;

final class PauseCard implements AutoCloseable {
    static final float WIDTH = 300.0F;
    private static final float IMAGE_HEIGHT = 150.0F;
    private static final float PADDING = 16.0F;
    private static final float GAP = 12.0F;
    private static final float ROW_HEIGHT = 26.0F;
    private static final float CHIP_HEIGHT = 18.0F;
    private static final float CHIP_PADDING = 6.0F;
    private static final float CHIP_ICON = 10.0F;
    private static final float CHECK_WIDTH = 9.0F;
    private static final float CHECK_HEIGHT = 7.0F;
    private static final float ICON_GAP = 8.0F;
    private static final float SQUARE = 1.0F;
    private static final float VALUE_SHARE = 0.6F;
    private static final long TICKS_PER_DAY = 24000L;
    private static final long TICKS_PER_HOUR = 1000L;
    private static final long DAWN_HOUR = 6L;
    private static final long HOURS_PER_DAY = 24L;
    private static final long MINUTES_PER_HOUR = 60L;
    private static final int CARD_FILL = ArkColors.rgba(16, 16, 18, 0.85F);
    private static final int ROW_LINE = ArkColors.rgba(255, 255, 255, 0.05F);
    private static final int CHIP_FILL = ArkColors.rgba(255, 224, 102, 0.10F);
    private static final TextStyle DAY = TextStyle.of(14.0F);
    private static final TextStyle CHIP = TextStyle.of(10.0F);
    private static final TextStyle ROW = TextStyle.of(11.0F);
    private static final TextStyle FOOTER = TextStyle.of(10.0F);
    private static final Identifier PANORAMA = Identifier.withDefaultNamespace("textures/gui/title/background/panorama_0.png");

    private final Minecraft minecraft = Minecraft.getInstance();
    private @Nullable FaviconTexture image;
    private boolean loaded;

    static float height() {
        return IMAGE_HEIGHT + PADDING * 2.0F + CHIP_HEIGHT + GAP * 2.0F + ROW_HEIGHT * 4.0F + FOOTER.size();
    }

    static Component worldName() {
        Minecraft minecraft = Minecraft.getInstance();
        IntegratedServer server = minecraft.getSingleplayerServer();
        if (server != null) {
            return Component.literal(server.getWorldData().getLevelName());
        }
        ServerData data = minecraft.getCurrentServer();
        return data != null ? Component.literal(data.name) : Component.translatable("menu.game");
    }

    void draw(UiGraphics graphics, Box box) {
        graphics.fill(box, CARD_FILL);
        graphics.border(box, 1.0F, ArkColors.BORDER_OVERLAY);
        Box imageBox = new Box(box.x() + 1.0F, box.y() + 1.0F, box.width() - 2.0F, IMAGE_HEIGHT);
        this.drawImage(graphics, imageBox);
        ClientLevel level = this.minecraft.level;
        if (level == null) {
            return;
        }
        TextMetrics metrics = graphics.metrics();
        float x = box.x() + PADDING;
        float right = box.right() - PADDING;
        float y = imageBox.bottom() + PADDING;
        long clock = level.getOverworldClockTime();
        String day = Component.translatable("arkea.pause.day", clock / TICKS_PER_DAY + 1).getString();
        graphics.text(day, x, y + (CHIP_HEIGHT - metrics.capHeight(DAY)) * 0.5F, DAY, ArkColors.TEXT_PRIMARY);
        this.drawChip(graphics, level, clock, right, y);
        y += CHIP_HEIGHT + GAP;
        for (Row row : this.rows(level)) {
            float textY = y + (ROW_HEIGHT - metrics.capHeight(ROW)) * 0.5F;
            graphics.text(row.key().getString(), x, textY, ROW, ArkColors.TEXT_DESCRIPTION);
            String value = metrics.ellipsize(row.value().getString(), ROW, (right - x) * VALUE_SHARE);
            graphics.text(value, right - metrics.width(value, ROW), textY, ROW, ArkColors.TEXT_PRIMARY);
            graphics.fill(x, y + ROW_HEIGHT - 1.0F, right - x, 1.0F, ROW_LINE);
            y += ROW_HEIGHT;
        }
        y += GAP;
        graphics.icon(Icons.CHECK, x, y + (metrics.capHeight(FOOTER) - CHECK_HEIGHT) * 0.5F, CHECK_WIDTH, CHECK_HEIGHT, Theme.accent().light());
        graphics.text(this.footer().getString(), x + CHECK_WIDTH + ICON_GAP, y, FOOTER, ArkColors.TEXT_FAINT);
    }

    private void drawChip(UiGraphics graphics, ClientLevel level, long clock, float right, float y) {
        TextMetrics metrics = graphics.metrics();
        long tick = clock % TICKS_PER_DAY;
        long hour = (tick / TICKS_PER_HOUR + DAWN_HOUR) % HOURS_PER_DAY;
        long minute = tick % TICKS_PER_HOUR * MINUTES_PER_HOUR / TICKS_PER_HOUR;
        String weather = Component.translatable(level.isThundering() ? "arkea.pause.weather.thunder"
            : level.isRaining() ? "arkea.pause.weather.rain" : "arkea.pause.weather.clear").getString();
        String text = String.format(Locale.ROOT, "%02d:%02d · %s", hour, minute, weather);
        float width = CHIP_PADDING * 2.0F + CHIP_ICON + CHIP_PADDING + metrics.width(text, CHIP);
        Box chip = new Box(right - width, y, width, CHIP_HEIGHT);
        graphics.fill(chip, CHIP_FILL);
        graphics.icon(Icons.SUN, chip.x() + CHIP_PADDING, chip.centerY() - CHIP_ICON * 0.5F, CHIP_ICON, CHIP_ICON, ArkColors.MINECRAFT_YELLOW);
        graphics.text(text, chip.x() + CHIP_PADDING * 2.0F + CHIP_ICON, chip.centerY() - metrics.capHeight(CHIP) * 0.5F, CHIP, ArkColors.MINECRAFT_YELLOW);
    }

    private List<Row> rows(ClientLevel level) {
        GameType mode = this.minecraft.gameMode != null ? this.minecraft.gameMode.getPlayerMode() : GameType.SURVIVAL;
        int players = this.minecraft.getConnection() != null ? this.minecraft.getConnection().getOnlinePlayers().size() : 1;
        return List.of(
            new Row(Component.translatable("arkea.pause.mode"), mode.getLongDisplayName()),
            new Row(Component.translatable("options.difficulty"), level.getDifficulty().getDisplayName()),
            new Row(Component.translatable("arkea.pause.players"), Component.translatable("arkea.pause.online", players)),
            new Row(Component.translatable("arkea.pause.dimension"), Component.literal(dimensionName(level))));
    }

    private static String dimensionName(ClientLevel level) {
        String path = level.dimension().identifier().getPath().replace('_', ' ');
        return path.isEmpty() ? path : Character.toUpperCase(path.charAt(0)) + path.substring(1);
    }

    private Component footer() {
        if (this.minecraft.isLocalServer()) {
            return Component.translatable("arkea.pause.local");
        }
        ServerData data = this.minecraft.getCurrentServer();
        return data != null ? Component.literal(data.ip) : Component.translatable("arkea.pause.online.world");
    }

    private void drawImage(UiGraphics graphics, Box box) {
        if (!this.loaded) {
            this.loaded = true;
            this.image = this.load();
        }
        if (this.image != null) {
            graphics.imageCover(this.image.textureLocation(), box, SQUARE, ArkColors.TEXT_PRIMARY);
            return;
        }
        graphics.imageCover(PANORAMA, box, SQUARE, ArkColors.TEXT_PRIMARY);
        LetterTile.draw(graphics, new Box(box.centerX() - IMAGE_HEIGHT * 0.25F, box.centerY() - IMAGE_HEIGHT * 0.25F, IMAGE_HEIGHT * 0.5F,
            IMAGE_HEIGHT * 0.5F), worldName().getString(), Locale.ROOT);
    }

    private @Nullable FaviconTexture load() {
        IntegratedServer server = this.minecraft.getSingleplayerServer();
        if (server != null) {
            Optional<Path> file = server.getWorldScreenshotFile().filter(Files::isRegularFile);
            if (file.isEmpty()) {
                return null;
            }
            try (InputStream stream = Files.newInputStream(file.get())) {
                return this.upload(FaviconTexture.forWorld(this.minecraft.getTextureManager(), server.getWorldData().getLevelName()), NativeImage.read(stream));
            } catch (IOException exception) {
                Arkea.LOGGER.warn("Could not load the world icon for the pause menu", exception);
                return null;
            }
        }
        ServerData data = this.minecraft.getCurrentServer();
        byte[] bytes = data != null ? data.getIconBytes() : null;
        if (bytes == null) {
            return null;
        }
        try {
            return this.upload(FaviconTexture.forServer(this.minecraft.getTextureManager(), data.ip), NativeImage.read(bytes));
        } catch (IOException exception) {
            Arkea.LOGGER.warn("Could not load the server icon for the pause menu", exception);
            return null;
        }
    }

    private FaviconTexture upload(FaviconTexture texture, NativeImage image) {
        try {
            texture.upload(image);
            return texture;
        } catch (RuntimeException exception) {
            texture.close();
            throw exception;
        }
    }

    @Override
    public void close() {
        if (this.image != null) {
            this.image.close();
            this.image = null;
        }
        this.loaded = false;
    }

    private record Row(Component key, Component value) {
    }
}
