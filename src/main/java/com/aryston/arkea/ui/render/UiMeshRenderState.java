package com.aryston.arkea.ui.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import org.jspecify.annotations.Nullable;

final class UiMeshRenderState implements GuiElementRenderState {
    private final RenderPipeline pipeline;
    private final TextureSetup textureSetup;
    private final boolean textured;
    private final float[] xs;
    private final float[] ys;
    private final float[] us;
    private final float[] vs;
    private final int[] colors;
    private final @Nullable ScreenRectangle scissorArea;
    private final @Nullable ScreenRectangle bounds;

    UiMeshRenderState(RenderPipeline pipeline, TextureSetup textureSetup, boolean textured, MeshBuilder mesh, @Nullable ScreenRectangle scissorArea) {
        this.pipeline = pipeline;
        this.textureSetup = textureSetup;
        this.textured = textured;
        this.xs = mesh.xs();
        this.ys = mesh.ys();
        this.us = mesh.us();
        this.vs = mesh.vs();
        this.colors = mesh.colors();
        this.scissorArea = scissorArea;
        this.bounds = this.computeBounds();
    }

    @Override
    public void buildVertices(VertexConsumer vertexConsumer) {
        for (int vertex = 0; vertex < this.xs.length; vertex++) {
            VertexConsumer consumer = vertexConsumer.addVertex(this.xs[vertex], this.ys[vertex], 0.0F);
            if (this.textured) {
                consumer.setUv(this.us[vertex], this.vs[vertex]);
            }
            consumer.setColor(this.colors[vertex]);
        }
    }

    @Override
    public RenderPipeline pipeline() {
        return this.pipeline;
    }

    @Override
    public TextureSetup textureSetup() {
        return this.textureSetup;
    }

    @Override
    public @Nullable ScreenRectangle scissorArea() {
        return this.scissorArea;
    }

    @Override
    public @Nullable ScreenRectangle bounds() {
        return this.bounds;
    }

    private @Nullable ScreenRectangle computeBounds() {
        float minX = Float.POSITIVE_INFINITY;
        float minY = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY;
        float maxY = Float.NEGATIVE_INFINITY;
        for (int vertex = 0; vertex < this.xs.length; vertex++) {
            minX = Math.min(minX, this.xs[vertex]);
            minY = Math.min(minY, this.ys[vertex]);
            maxX = Math.max(maxX, this.xs[vertex]);
            maxY = Math.max(maxY, this.ys[vertex]);
        }
        int left = (int) Math.floor(minX);
        int top = (int) Math.floor(minY);
        ScreenRectangle area = new ScreenRectangle(left, top, Math.max(0, (int) Math.ceil(maxX) - left), Math.max(0, (int) Math.ceil(maxY) - top));
        return this.scissorArea != null ? this.scissorArea.intersection(area) : area;
    }
}
