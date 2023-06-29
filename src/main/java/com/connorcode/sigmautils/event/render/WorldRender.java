package com.connorcode.sigmautils.event.render;

import com.connorcode.sigmautils.event.Cancellable;
import com.connorcode.sigmautils.event.EventI;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.util.math.MatrixStack;

public class WorldRender {
    public interface PreWorldRenderCallback extends EventI {
        Event<PreWorldRenderCallback> EVENT =
                EventFactory.createArrayBacked(PreWorldRenderCallback.class, callbacks -> event -> {
                    for (PreWorldRenderCallback i : callbacks)
                        i.handle(event);
                });

        void handle(WorldRenderEvent event);
    }

    public interface PostWorldRenderCallback extends EventI {
        Event<PostWorldRenderCallback> EVENT =
                EventFactory.createArrayBacked(PostWorldRenderCallback.class, callbacks -> event -> {
                    for (PostWorldRenderCallback i : callbacks)
                        i.handle(event);
                });

        void handle(WorldRenderEvent event);
    }

    public static class WorldRenderEvent extends Cancellable {
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
