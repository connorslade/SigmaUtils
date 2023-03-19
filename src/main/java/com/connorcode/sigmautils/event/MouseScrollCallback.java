package com.connorcode.sigmautils.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface MouseScrollCallback {
    Event<MouseScrollCallback> EVENT =
            EventFactory.createArrayBacked(MouseScrollCallback.class, mouseScrollEvents -> event -> {
                for (MouseScrollCallback i : mouseScrollEvents)
                    i.handle(event);
            });

    void handle(MouseScrollEvent screen);

    class MouseScrollEvent extends EventData {
        public double horizontal;
        public double vertical;

        public MouseScrollEvent(double horizontal, double vertical) {
            this.horizontal = horizontal;
            this.vertical = vertical;
        }
    }
}
