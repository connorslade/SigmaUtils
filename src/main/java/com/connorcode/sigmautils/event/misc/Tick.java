package com.connorcode.sigmautils.event.misc;

import com.connorcode.sigmautils.event.Cancellable;
import com.connorcode.sigmautils.event.EventI;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class Tick {
    public interface GameTickCallback extends EventI {
        Event<GameTickCallback> EVENT =
                EventFactory.createArrayBacked(GameTickCallback.class, tickCallbacks -> event -> {
                    for (GameTickCallback i : tickCallbacks)
                        i.handle(event);
                });

        void handle(TickEvent tickEvent);
    }

    public interface RenderTickCallback extends EventI {
        Event<RenderTickCallback> EVENT =
                EventFactory.createArrayBacked(RenderTickCallback.class, tickCallbacks -> event -> {
                    for (RenderTickCallback i : tickCallbacks)
                        i.handle(event);
                });

        void handle(TickEvent tickEvent);
    }

    public static class TickEvent extends Cancellable {
        public TickEvent() {}
    }
}
