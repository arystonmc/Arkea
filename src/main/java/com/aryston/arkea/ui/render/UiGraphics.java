package com.aryston.arkea.ui.render;

import com.aryston.arkea.ui.layout.Box;
import com.aryston.arkea.ui.layout.UiScale;
import com.aryston.arkea.ui.theme.ArkColors;
import com.mojang.blaze3d.platform.cursor.CursorType;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.textures.FilterMode;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix3x2fStack;
import org.joml.Vector2f;

public final class UiGraphics {
    private static final String ELLIPSIS = "...";
    private static final int MAX_DEPTH = 64;
    private static final int TOP_LEFT = 0;
    private static final int BOTTOM_LEFT = 1;
    private static final int BOTTOM_RIGHT = 2;
    private static final int TOP_RIGHT = 3;
    private static final int SLICES = 3;
    private static final float STROKE_WIDTH = 1.5F;
    private static final float HALF = 0.5F;

    private final GuiGraphicsExtractor graphics;
    private final Matrix3x2fStack pose;
    private final UiScale scale;
    private final Font font;
    private final TextMetrics metrics;
    private final long now;
    private final float[] alphaStack = new float[MAX_DEPTH];
    private final Vector2f point = new Vector2f();
    private final QuadGeometry quad = new QuadGeometry();
    private final MeshBuilder mesh = new MeshBuilder();
    private final Deque<Box> clips = new ArrayDeque<>();
    private int depth;
    private float alpha = 1.0F;

    public UiGraphics(GuiGraphicsExtractor graphics, UiScale scale, Font font, long now) {
        this.graphics = graphics;
        this.pose = graphics.pose();
        this.scale = scale;
        this.font = font;
        this.metrics = new TextMetrics(font, scale);
        this.now = now;
    }

    public long now() {
        return this.now;
    }

    public UiScale scale() {
        return this.scale;
    }

    public void push() {
        this.pose.pushMatrix();
        this.alphaStack[this.depth++] = this.alpha;
    }

    public void pop() {
        this.pose.popMatrix();
        this.alpha = this.alphaStack[--this.depth];
    }

    public void translate(float x, float y) {
        this.pose.translate(x, y);
    }

    public void scaleAround(float factor, float pivotX, float pivotY) {
        this.pose.translate(pivotX, pivotY).scale(factor).translate(-pivotX, -pivotY);
    }

    public void rotateAround(float radians, float pivotX, float pivotY) {
        this.pose.translate(pivotX, pivotY).rotate(radians).translate(-pivotX, -pivotY);
    }

    public Box canvasBox(Box local) {
        float poseScale = this.scale.poseScale();
        this.pose.transformPosition(local.x(), local.y(), this.point);
        float x0 = this.point.x / poseScale;
        float y0 = this.point.y / poseScale;
        this.pose.transformPosition(local.right(), local.bottom(), this.point);
        float x1 = this.point.x / poseScale;
        float y1 = this.point.y / poseScale;
        return new Box(Math.min(x0, x1), Math.min(y0, y1), Math.abs(x1 - x0), Math.abs(y1 - y0));
    }

    public void clip(Box local) {
        this.graphics.enableScissor((int) Math.floor(local.x()), (int) Math.floor(local.y()), (int) Math.ceil(local.right()), (int) Math.ceil(local.bottom()));
        Box canvas = this.canvasBox(local);
        this.clips.push(this.clips.isEmpty() ? canvas : this.clips.peek().intersect(canvas));
    }

    public void endClip() {
        this.graphics.disableScissor();
        this.clips.pop();
    }

    public Box visible(Box canvas) {
        return this.clips.isEmpty() ? canvas : this.clips.peek().intersect(canvas);
    }

    public void fade(float factor) {
        this.alpha *= Math.clamp(factor, 0.0F, 1.0F);
    }

    public void fill(Box box, int color) {
        this.fill(box.x(), box.y(), box.width(), box.height(), color);
    }

    public void fill(float x, float y, float width, float height, int color) {
        this.rect(x, y, width, height, color, color, color, color);
    }

    public void gradientHorizontal(float x, float y, float width, float height, int left, int right) {
        this.rect(x, y, width, height, left, left, right, right);
    }

    public void gradientVertical(float x, float y, float width, float height, int top, int bottom) {
        this.rect(x, y, width, height, top, bottom, bottom, top);
    }

    public void border(Box box, float thickness, int color) {
        float line = this.scale.snapThickness(thickness);
        this.fill(box.x(), box.y(), box.width(), line, color);
        this.fill(box.x(), box.bottom() - line, box.width(), line, color);
        this.fill(box.x(), box.y() + line, line, box.height() - line * 2.0F, color);
        this.fill(box.right() - line, box.y() + line, line, box.height() - line * 2.0F, color);
    }

    public void dashedBorder(Box box, float dash, int color) {
        float line = this.scale.snapThickness(1.0F);
        for (float x = box.x(); x < box.right(); x += dash * 2.0F) {
            float width = Math.min(dash, box.right() - x);
            this.fill(x, box.y(), width, line, color);
            this.fill(x, box.bottom() - line, width, line, color);
        }
        for (float y = box.y(); y < box.bottom(); y += dash * 2.0F) {
            float height = Math.min(dash, box.bottom() - y);
            this.fill(box.x(), y, line, height, color);
            this.fill(box.right() - line, y, line, height, color);
        }
    }

    public void topHighlight(Box box, int color) {
        float line = this.scale.snapThickness(1.0F);
        float edge = this.scale.snapThickness(1.0F);
        this.fill(box.x() + edge, box.y() + edge, box.width() - edge * 2.0F, line, color);
    }

    public void shadow(Box box, float blur, float offsetY, int color) {
        int tinted = this.applyAlpha(color);
        if (blur <= 0.0F || ArkColors.alpha(tinted) <= 0.0F) {
            return;
        }
        Box shifted = box.offset(0.0F, offsetY);
        float[] xs = sliceEdges(shifted.x(), shifted.right(), blur);
        float[] ys = sliceEdges(shifted.y(), shifted.bottom(), blur);
        float[] us = sliceCoordinates(xs, blur);
        float[] vs = sliceCoordinates(ys, blur);
        for (int row = 0; row < SLICES; row++) {
            for (int column = 0; column < SLICES; column++) {
                this.addTexturedQuad(xs[column], ys[row], xs[column + 1], ys[row + 1], us[column], vs[row], us[column + 1], vs[row + 1], tinted);
            }
        }
        this.flush(RenderPipelines.GUI_TEXTURED, GeneratedTextures.softEdge(), true);
    }

    public void vignette(Box box, int color) {
        this.addTexturedQuad(box.x(), box.y(), box.right(), box.bottom(), 0.0F, 0.0F, 1.0F, 1.0F, this.applyAlpha(color));
        this.flush(RenderPipelines.GUI_TEXTURED, GeneratedTextures.vignette(), true);
    }

    public void image(Identifier texture, Box box, float u0, float v0, float u1, float v1, int color) {
        AbstractTexture source = Minecraft.getInstance().getTextureManager().getTexture(texture);
        this.addTexturedQuad(box.x(), box.y(), box.right(), box.bottom(), u0, v0, u1, v1, this.applyAlpha(color));
        this.flush(RenderPipelines.GUI_TEXTURED, TextureSetup.singleTexture(source.getTextureView(), source.getSampler()), true);
    }

    public void imageCover(Identifier texture, Box box, float sourceAspect, int color) {
        float boxAspect = box.width() / box.height();
        float u = boxAspect < sourceAspect ? (1.0F - boxAspect / sourceAspect) * 0.5F : 0.0F;
        float v = boxAspect > sourceAspect ? (1.0F - sourceAspect / boxAspect) * 0.5F : 0.0F;
        this.image(texture, box, u, v, 1.0F - u, 1.0F - v, color);
    }

    public void image(AbstractTexture texture, Box box, float u0, float v0, float u1, float v1, int color) {
        this.addTexturedQuad(box.x(), box.y(), box.right(), box.bottom(), u0, v0, u1, v1, this.applyAlpha(color));
        TextureSetup setup = TextureSetup.singleTexture(texture.getTextureView(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR));
        this.flush(RenderPipelines.GUI_TEXTURED, setup, true);
    }

    public void icon(Icon icon, float x, float y, float width, float height, int color) {
        int tinted = this.applyAlpha(color);
        if (ArkColors.alpha(tinted) <= 0.0F) {
            return;
        }
        this.pose.pushMatrix();
        this.pose.translate(x, y).scale(width / icon.viewWidth(), height / icon.viewHeight());
        for (Polyline polyline : icon.polylines()) {
            if (icon.style() == IconStyle.STROKE) {
                this.strokePolyline(polyline, tinted);
            } else {
                this.fillPolygon(polyline, tinted);
            }
        }
        this.pose.popMatrix();
        this.flush(RenderPipelines.GUI, TextureSetup.noTexture(), false);
    }

    public TextMetrics metrics() {
        return this.metrics;
    }

    public void text(String text, float x, float y, TextStyle style, int color) {
        if (text.isEmpty()) {
            return;
        }
        if (style.hasShadow()) {
            this.drawText(text, x + style.shadowOffset(), y + style.shadowOffset(), style, style.shadowColor());
        }
        this.drawText(text, x, y, style, color);
    }

    public void richText(Component text, float x, float y, float maxWidth, TextStyle style, int color) {
        int tinted = this.applyAlpha(color);
        if (ArkColors.alpha(tinted) <= 0.0F) {
            return;
        }
        float fontScale = this.metrics.fontScale(style);
        int available = Math.max(0, (int) Math.ceil(maxWidth / fontScale) + 1);
        FormattedText clipped = this.font.width(text) <= available ? text
            : FormattedText.composite(this.font.substrByWidth(text, Math.max(0, available - this.font.width(ELLIPSIS))), FormattedText.of(ELLIPSIS));
        this.pose.pushMatrix();
        this.translateSnapped(x, y);
        this.pose.scale(fontScale);
        this.graphics.text(this.font, Language.getInstance().getVisualOrder(clipped), 0, 0, tinted, false);
        this.pose.popMatrix();
    }

    public void textLine(FormattedCharSequence line, float x, float y, TextStyle style, int color) {
        int tinted = this.applyAlpha(color);
        if (ArkColors.alpha(tinted) <= 0.0F) {
            return;
        }
        this.pose.pushMatrix();
        this.translateSnapped(x, y);
        this.pose.scale(this.metrics.fontScale(style));
        this.graphics.text(this.font, line, 0, 0, tinted, false);
        this.pose.popMatrix();
    }

    public void trueTypeLine(FormattedCharSequence line, float x, float y, TextStyle style, int color) {
        int tinted = this.applyAlpha(color);
        if (ArkColors.alpha(tinted) <= 0.0F) {
            return;
        }
        float fontScale = this.metrics.fontScale(style);
        float pixels = fontScale * this.scale.scale();
        float baseline = TextStyle.BASELINE * pixels;
        this.pose.pushMatrix();
        this.translateSnapped(x, y);
        this.pose.scale(fontScale);
        this.pose.translate(0.0F, ((float) Math.ceil(baseline) - baseline) / pixels);
        this.graphics.text(this.font, line, 0, 0, tinted, false);
        this.pose.popMatrix();
    }

    public void vanilla(Box box, float nativeSize, Consumer<GuiGraphicsExtractor> draw) {
        this.pose.pushMatrix();
        this.pose.translate(box.x(), box.y());
        this.pose.scale(box.width() / nativeSize, box.height() / nativeSize);
        draw.accept(this.graphics);
        this.pose.popMatrix();
    }

    public void cursor(CursorType cursor) {
        this.graphics.requestCursor(cursor);
    }

    private void drawText(String text, float x, float y, TextStyle style, int color) {
        int tinted = this.applyAlpha(color);
        if (ArkColors.alpha(tinted) <= 0.0F) {
            return;
        }
        float fontScale = this.metrics.fontScale(style);
        this.pose.pushMatrix();
        this.translateSnapped(x, y);
        this.pose.scale(fontScale);
        if (style.letterSpacing() == 0.0F) {
            this.graphics.text(this.font, text, 0, 0, tinted, false);
        } else {
            this.drawSpacedText(text, style.letterSpacing() / fontScale, tinted);
        }
        this.pose.popMatrix();
    }

    private void drawSpacedText(String text, float spacing, int color) {
        float cursor = 0.0F;
        int index = 0;
        while (index < text.length()) {
            int codePoint = text.codePointAt(index);
            String glyph = new String(Character.toChars(codePoint));
            this.pose.pushMatrix();
            this.pose.translate(cursor, 0.0F);
            this.graphics.text(this.font, glyph, 0, 0, color, false);
            this.pose.popMatrix();
            cursor += this.font.width(glyph) + spacing;
            index += Character.charCount(codePoint);
        }
    }

    private void rect(float x, float y, float width, float height, int topLeft, int bottomLeft, int bottomRight, int topRight) {
        if (width <= 0.0F || height <= 0.0F) {
            return;
        }
        this.transformCorner(TOP_LEFT, x, y, 0.0F, 0.0F, this.applyAlpha(topLeft));
        this.transformCorner(BOTTOM_LEFT, x, y + height, 0.0F, 0.0F, this.applyAlpha(bottomLeft));
        this.transformCorner(BOTTOM_RIGHT, x + width, y + height, 0.0F, 0.0F, this.applyAlpha(bottomRight));
        this.transformCorner(TOP_RIGHT, x + width, y, 0.0F, 0.0F, this.applyAlpha(topRight));
        if (this.isAxisAligned()) {
            this.snapQuad();
        }
        this.mesh.add(this.quad);
        this.flush(RenderPipelines.GUI, TextureSetup.noTexture(), false);
    }

    private void addTexturedQuad(float x0, float y0, float x1, float y1, float u0, float v0, float u1, float v1, int color) {
        if (x1 <= x0 || y1 <= y0 || ArkColors.alpha(color) <= 0.0F) {
            return;
        }
        this.transformCorner(TOP_LEFT, x0, y0, u0, v0, color);
        this.transformCorner(BOTTOM_LEFT, x0, y1, u0, v1, color);
        this.transformCorner(BOTTOM_RIGHT, x1, y1, u1, v1, color);
        this.transformCorner(TOP_RIGHT, x1, y0, u1, v0, color);
        this.mesh.add(this.quad);
    }

    private static float[] sliceEdges(float start, float end, float blur) {
        float center = (start + end) * HALF;
        return new float[] {start - blur, Math.min(start + blur, center), Math.max(end - blur, center), end + blur};
    }

    private static float[] sliceCoordinates(float[] edges, float blur) {
        float band = blur * 2.0F;
        return new float[] {0.0F, (edges[1] - edges[0]) / band * HALF, 1.0F - (edges[3] - edges[2]) / band * HALF, 1.0F};
    }

    private void strokePolyline(Polyline polyline, int color) {
        int count = polyline.size();
        if (count == 1) {
            this.segment(polyline.x(0), polyline.y(0), polyline.x(0), polyline.y(0), color);
            return;
        }
        for (int index = 0; index < count - 1; index++) {
            this.segment(polyline.x(index), polyline.y(index), polyline.x(index + 1), polyline.y(index + 1), color);
        }
        if (polyline.closed() && count > 2) {
            this.segment(polyline.x(count - 1), polyline.y(count - 1), polyline.x(0), polyline.y(0), color);
        }
    }

    private void segment(float x0, float y0, float x1, float y1, int color) {
        float deltaX = x1 - x0;
        float deltaY = y1 - y0;
        float length = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        float directionX = length > 0.0F ? deltaX / length : 1.0F;
        float directionY = length > 0.0F ? deltaY / length : 0.0F;
        float half = STROKE_WIDTH * HALF;
        float capX = directionX * half;
        float capY = directionY * half;
        float normalX = -directionY * half;
        float normalY = directionX * half;
        this.transformCorner(TOP_LEFT, x0 - capX + normalX, y0 - capY + normalY, 0.0F, 0.0F, color);
        this.transformCorner(BOTTOM_LEFT, x0 - capX - normalX, y0 - capY - normalY, 0.0F, 0.0F, color);
        this.transformCorner(BOTTOM_RIGHT, x1 + capX - normalX, y1 + capY - normalY, 0.0F, 0.0F, color);
        this.transformCorner(TOP_RIGHT, x1 + capX + normalX, y1 + capY + normalY, 0.0F, 0.0F, color);
        this.mesh.add(this.quad);
    }

    private void fillPolygon(Polyline polyline, int color) {
        int count = polyline.size();
        for (int index = 1; index < count - 1; index++) {
            this.transformCorner(TOP_LEFT, polyline.x(0), polyline.y(0), 0.0F, 0.0F, color);
            this.transformCorner(BOTTOM_LEFT, polyline.x(index), polyline.y(index), 0.0F, 0.0F, color);
            this.transformCorner(BOTTOM_RIGHT, polyline.x(index + 1), polyline.y(index + 1), 0.0F, 0.0F, color);
            this.transformCorner(TOP_RIGHT, polyline.x(index + 1), polyline.y(index + 1), 0.0F, 0.0F, color);
            this.mesh.add(this.quad);
        }
    }

    private void transformCorner(int corner, float x, float y, float u, float v, int color) {
        this.pose.transformPosition(x, y, this.point);
        this.quad.corner(corner, this.point.x, this.point.y, u, v, color);
    }

    private void translateSnapped(float x, float y) {
        if (!this.isAxisAligned()) {
            this.pose.translate(x, y);
            return;
        }
        float guiScale = this.scale.guiScale();
        this.pose.transformPosition(x, y, this.point);
        float offsetX = (snap(this.point.x, guiScale) - this.point.x) / this.pose.m00();
        float offsetY = (snap(this.point.y, guiScale) - this.point.y) / this.pose.m11();
        this.pose.translate(x + offsetX, y + offsetY);
    }

    private boolean isAxisAligned() {
        return this.pose.m01() == 0.0F && this.pose.m10() == 0.0F;
    }

    private void snapQuad() {
        float guiScale = this.scale.guiScale();
        float pixel = 1.0F / guiScale;
        float left = snap(Math.min(this.quad.x(TOP_LEFT), this.quad.x(TOP_RIGHT)), guiScale);
        float right = Math.max(snap(Math.max(this.quad.x(TOP_LEFT), this.quad.x(TOP_RIGHT)), guiScale), left + pixel);
        float top = snap(Math.min(this.quad.y(TOP_LEFT), this.quad.y(BOTTOM_LEFT)), guiScale);
        float bottom = Math.max(snap(Math.max(this.quad.y(TOP_LEFT), this.quad.y(BOTTOM_LEFT)), guiScale), top + pixel);
        this.quad.corner(TOP_LEFT, left, top, this.quad.color(TOP_LEFT));
        this.quad.corner(BOTTOM_LEFT, left, bottom, this.quad.color(BOTTOM_LEFT));
        this.quad.corner(BOTTOM_RIGHT, right, bottom, this.quad.color(BOTTOM_RIGHT));
        this.quad.corner(TOP_RIGHT, right, top, this.quad.color(TOP_RIGHT));
    }

    private static float snap(float guiCoordinate, float guiScale) {
        return Math.round(guiCoordinate * guiScale) / guiScale;
    }

    private void flush(RenderPipeline pipeline, TextureSetup setup, boolean textured) {
        if (this.mesh.isEmpty()) {
            return;
        }
        this.graphics.submitGuiElementRenderState(new UiMeshRenderState(pipeline, setup, textured, this.mesh, this.graphics.peekScissorStack()));
        this.mesh.clear();
    }

    private int applyAlpha(int color) {
        return ArkColors.multiplyAlpha(color, this.alpha);
    }
}
