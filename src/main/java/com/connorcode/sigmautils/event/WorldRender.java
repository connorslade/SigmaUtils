package com.connorcode.sigmautils.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.util.math.MatrixStack;

public class WorldRender {
    public interface PreWorldRenderCallback {
        Event<PreWorldRenderCallback> EVENT =
                EventFactory.createArrayBacked(PreWorldRenderCallback.class, callbacks -> event -> {
                    for (PreWorldRenderCallback i : callbacks) i.handle(event);
                });

        void handle(WorldRenderEvent event);
    }

    public interface PostWorldRenderCallback {
        Event<PostWorldRenderCallback> EVENT =
                EventFactory.createArrayBacked(PostWorldRenderCallback.class, callbacks -> event -> {
                    for (PostWorldRenderCallback i : callbacks) i.handle(event);
                });

        void handle(WorldRenderEvent event);
    }

    public static class WorldRenderEvent extends EventData {
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
