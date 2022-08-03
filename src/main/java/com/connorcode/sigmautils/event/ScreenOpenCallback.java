package com.connorcode.sigmautils.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.screen.Screen;

public interface ScreenOpenCallback {
    Event<ScreenOpenCallback> EVENT =
            EventFactory.createArrayBacked(ScreenOpenCallback.class, screenOpenCallbacks -> screen -> {
                boolean cancel = false;
                for (ScreenOpenCallback i : screenOpenCallbacks) cancel |= i.handle(screen);
                return cancel;
            });

    boolean handle(Screen screen);
}
