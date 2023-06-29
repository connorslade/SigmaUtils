package com.connorcode.sigmautils.event.misc;

import com.connorcode.sigmautils.event.EventI;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class GameLifecycle {
    public interface WorldCloseCallback extends EventI {
        Event<WorldCloseCallback> EVENT =
                EventFactory.createArrayBacked(WorldCloseCallback.class, callbacks -> {
                    for (WorldCloseCallback callback : callbacks)
                        callback.handle();
                    return null;
                });

        void handle();
    }
}
