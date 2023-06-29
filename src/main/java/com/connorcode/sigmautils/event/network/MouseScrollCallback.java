package com.connorcode.sigmautils.event.network;

import com.connorcode.sigmautils.event.Cancellable;
import com.connorcode.sigmautils.event.EventI;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface MouseScrollCallback extends EventI {
    Event<MouseScrollCallback> EVENT =
            EventFactory.createArrayBacked(MouseScrollCallback.class, mouseScrollEvents -> event -> {
                for (MouseScrollCallback i : mouseScrollEvents)
                    i.handle(event);
            });

    void handle(MouseScrollEvent screen);

    class MouseScrollEvent extends Cancellable {
        public double horizontal;
        public double vertical;

        public MouseScrollEvent(double horizontal, double vertical) {
            this.horizontal = horizontal;
            this.vertical = vertical;
        }
    }
}
