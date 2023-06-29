package com.connorcode.sigmautils.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class GameLifecycle {
    public interface WorldCloseCallback {
        Event<WorldCloseCallback> EVENT =
                EventFactory.createArrayBacked(WorldCloseCallback.class, callbacks -> {
                    for (WorldCloseCallback callback : callbacks)
                        callback.handle();
                    return null;
                });

        void handle();
    }
}
