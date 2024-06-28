package com.connorcode.sigmautils.event.render;

import com.connorcode.sigmautils.event.Cancellable;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderTickCounter;
import org.joml.Matrix4f;

public class WorldRender {
    public static class PostWorldRenderEvent extends WorldRenderEvent {
        public PostWorldRenderEvent(RenderTickCounter renderTickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix, Matrix4f projectionMatrix) {
            super(renderTickCounter, renderBlockOutline, camera, gameRenderer, lightmapTextureManager, positionMatrix, projectionMatrix);
        }
    }

    public static class PreWorldRenderEvent extends WorldRenderEvent {
        public PreWorldRenderEvent(RenderTickCounter renderTickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix, Matrix4f projectionMatrix) {
            super(renderTickCounter, renderBlockOutline, camera, gameRenderer, lightmapTextureManager, positionMatrix, projectionMatrix);
        }
    }

    public static abstract class WorldRenderEvent extends Cancellable {
        RenderTickCounter renderTickCounter;
        boolean renderBlockOutline;
        Camera camera;
        GameRenderer gameRenderer;
        LightmapTextureManager lightmapTextureManager;
        Matrix4f positionMatrix;
        Matrix4f projectionMatrix;

        public WorldRenderEvent(RenderTickCounter renderTickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix, Matrix4f projectionMatrix) {
            this.renderTickCounter = renderTickCounter;
            this.renderBlockOutline = renderBlockOutline;
            this.camera = camera;
            this.gameRenderer = gameRenderer;
            this.lightmapTextureManager = lightmapTextureManager;
            this.positionMatrix = positionMatrix;
            this.projectionMatrix = projectionMatrix;
        }

        public RenderTickCounter getRenderTickCounter() {
            return renderTickCounter;
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

        public Matrix4f getProjectionMatrix() {
            return projectionMatrix;
        }
    }
}
