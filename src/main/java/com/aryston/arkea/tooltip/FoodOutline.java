package com.aryston.arkea.tooltip;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

final class FoodOutline {
    private static final Identifier SHAPE = Identifier.withDefaultNamespace("hud/food_full");
    private static final int[][] NEIGHBOURS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
    private static final float HALF = 0.5F;
    private static @Nullable SpriteContents source;
    private static List<Pixel> edge = List.of();

    private FoodOutline() {
    }

    static void draw(GuiGraphicsExtractor graphics, int x, int y, int size, boolean half, int color, int fadedColor) {
        SpriteContents contents = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.GUI).getSprite(SHAPE).contents();
        if (contents != source) {
            source = contents;
            edge = edge(contents);
        }
        float middle = contents.width() * HALF;
        graphics.pose().pushMatrix();
        graphics.pose().translate(x, y);
        graphics.pose().scale((float) size / contents.width(), (float) size / contents.height());
        for (Pixel pixel : edge) {
            graphics.fill(pixel.x(), pixel.y(), pixel.x() + 1, pixel.y() + 1, half && pixel.x() >= middle ? fadedColor : color);
        }
        graphics.pose().popMatrix();
    }

    private static List<Pixel> edge(SpriteContents contents) {
        List<Pixel> pixels = new ArrayList<>();
        for (int y = 0; y < contents.height(); y++) {
            for (int x = 0; x < contents.width(); x++) {
                if (!contents.isTransparent(0, x, y) && touchesOutside(contents, x, y)) {
                    pixels.add(new Pixel(x, y));
                }
            }
        }
        return List.copyOf(pixels);
    }

    private static boolean touchesOutside(SpriteContents contents, int x, int y) {
        for (int[] offset : NEIGHBOURS) {
            int nx = x + offset[0];
            int ny = y + offset[1];
            if (nx < 0 || ny < 0 || nx >= contents.width() || ny >= contents.height() || contents.isTransparent(0, nx, ny)) {
                return true;
            }
        }
        return false;
    }

    private record Pixel(int x, int y) {
    }
}
