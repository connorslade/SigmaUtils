package com.connorcode.sigmautils.event.render;

import com.connorcode.sigmautils.event.Cancellable;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

public class WorldRender {
    public static class PostWorldRenderEvent extends WorldRenderEvent {
        public PostWorldRenderEvent(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer,
                                    LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix) {
            super(matrices, tickDelta, limitTime, renderBlockOutline, camera, gameRenderer, lightmapTextureManager, positionMatrix);
        }
    }

    public static class PreWorldRenderEvent extends WorldRenderEvent {
        public PreWorldRenderEvent(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer,
                                   LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix) {
            super(matrices, tickDelta, limitTime, renderBlockOutline, camera, gameRenderer, lightmapTextureManager, positionMatrix);
        }
    }

    public static abstract class WorldRenderEvent extends Cancellable {
        MatrixStack matrices;
        float tickDelta;
        long limitTime;
        boolean renderBlockOutline;
        Camera camera;
        GameRenderer gameRenderer;
        LightmapTextureManager lightmapTextureManager;
        Matrix4f positionMatrix;

        public WorldRenderEvent(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer,
                                LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix) {
            this.matrices = matrices;
            this.tickDelta = tickDelta;
            this.limitTime = limitTime;
            this.renderBlockOutline = renderBlockOutline;
            this.camera = camera;
            this.gameRenderer = gameRenderer;
            this.lightmapTextureManager = lightmapTextureManager;
            this.positionMatrix = positionMatrix;
        }

        public MatrixStack getMatrices() {
            return matrices;
        }

        public float getTickDelta() {
            return tickDelta;
        }

        public long getLimitTime() {
            return limitTime;
        }

        public boolean isRenderBlockOutline() {
            return renderBlockOutline;
        }

        public Camera getCamera() {
            return camera;
        }

        public GameRenderer getGameRenderer() {
            return gameRenderer;
        }

        public LightmapTextureManager getLightmapTextureManager() {
            return lightmapTextureManager;
        }

        public Matrix4f getPositionMatrix() {
            return positionMatrix;
        }
    }
}
