package com.aryston.arkea.screen.worlds;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.overlay.DialogImage;
import com.aryston.arkea.ui.render.UiGraphics;
import com.aryston.arkea.ui.theme.ArkColors;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.LevelSummary;

record WorldImage(WorldLibrary library, LevelSummary summary) implements DialogImage {
    private static final Identifier PANORAMA = Identifier.withDefaultNamespace("textures/gui/title/background/panorama_0.png");
    private static final float SQUARE = 1.0F;
    private static final int FALLBACK_TINT = ArkColors.rgb(0xB0B0B0);

    @Override
    public void draw(UiGraphics graphics, Box box) {
        Identifier icon = this.library.icon(this.summary);
        if (icon != null) {
            graphics.imageCover(icon, box, SQUARE, ArkColors.TEXT_PRIMARY);
            return;
        }
        graphics.imageCover(PANORAMA, box, SQUARE, FALLBACK_TINT);
    }
}
