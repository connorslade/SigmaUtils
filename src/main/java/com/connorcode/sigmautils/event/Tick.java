package com.connorcode.sigmautils.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class Tick {
    public interface GameTickCallback {
        Event<GameTickCallback> EVENT =
                EventFactory.createArrayBacked(GameTickCallback.class, tickCallbacks -> event -> {
                    for (GameTickCallback i : tickCallbacks)
                        i.handle(event);
                });

        void handle(TickEvent tickEvent);
    }

    public interface RenderTickCallback {
        Event<RenderTickCallback> EVENT =
                EventFactory.createArrayBacked(RenderTickCallback.class, tickCallbacks -> event -> {
                    for (RenderTickCallback i : tickCallbacks)
                        i.handle(event);
                });

        void handle(TickEvent tickEvent);
    }

    public static class TickEvent extends EventData {
        public TickEvent() {}
    }
}
