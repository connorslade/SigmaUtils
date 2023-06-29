package com.connorcode.sigmautils.event.render;

import com.connorcode.sigmautils.event.Cancellable;
import net.minecraft.client.util.math.MatrixStack;

public class WorldRender {
    public static class PostWorldRenderEvent extends WorldRenderEvent {
        public PostWorldRenderEvent(float tickDelta, MatrixStack matrixStack) {
            super(tickDelta, matrixStack);
        }
    }

    public static class PreWorldRenderEvent extends WorldRenderEvent {
        public PreWorldRenderEvent(float tickDelta, MatrixStack matrixStack) {
            super(tickDelta, matrixStack);
        }
    }

    public static abstract class WorldRenderEvent extends Cancellable {
        public float tickDelta;
        public MatrixStack matrixStack;

        public WorldRenderEvent(float tickDelta, MatrixStack matrixStack) {
            this.tickDelta = tickDelta;
            this.matrixStack = matrixStack;
        }

        public float getTickDelta() {
            return tickDelta;
        }

        public MatrixStack getMatrices() {
            return matrixStack;
        }
    }
}
