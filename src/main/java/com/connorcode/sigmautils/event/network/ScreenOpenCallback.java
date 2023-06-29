package com.connorcode.sigmautils.event.network;

import com.connorcode.sigmautils.event.Cancellable;
import com.connorcode.sigmautils.event.EventI;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.screen.Screen;

public interface ScreenOpenCallback extends EventI {
    Event<ScreenOpenCallback> EVENT =
            EventFactory.createArrayBacked(ScreenOpenCallback.class, screenOpenCallbacks -> event -> {
                for (ScreenOpenCallback i : screenOpenCallbacks)
                    i.handle(event);
            });

    void handle(ScreenOpenEvent screen);

    class ScreenOpenEvent extends Cancellable {
        public Screen screen;

        public ScreenOpenEvent(Screen screen) {
            this.screen = screen;
        }

        public Screen get() {
            return screen;
        }
    }
}
