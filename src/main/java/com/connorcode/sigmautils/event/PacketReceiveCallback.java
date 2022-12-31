package com.connorcode.sigmautils.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.Packet;

public interface PacketReceiveCallback {
    Event<PacketReceiveCallback> EVENT =
            EventFactory.createArrayBacked(PacketReceiveCallback.class, packetReceiveCallbacks -> event -> {
                for (PacketReceiveCallback i : packetReceiveCallbacks) i.handle(event);
            });

    void handle(PacketReceiveEvent event);

    class PacketReceiveEvent {
        public Packet<?> packet;
        public boolean cancel = false;

        public PacketReceiveEvent(Packet<?> packet) {
            this.packet = packet;
        }

        public Packet<?> get() {
            return packet;
        }

        public void cancel() {
            cancel = true;
        }
    }
}
