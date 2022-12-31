package com.connorcode.sigmautils.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.screen.Screen;

public interface ScreenOpenCallback {
    Event<ScreenOpenCallback> EVENT =
            EventFactory.createArrayBacked(ScreenOpenCallback.class, screenOpenCallbacks -> event -> {
                for (ScreenOpenCallback i : screenOpenCallbacks) i.handle(event);
            });

    void handle(ScreenOpenEvent screen);

    class ScreenOpenEvent {
        public Screen screen;
        public boolean cancel = false;

        public ScreenOpenEvent(Screen screen) {
            this.screen = screen;
        }

        public Screen get() {
            return screen;
        }

        public void cancel() {
            cancel = true;
        }
    }
}
