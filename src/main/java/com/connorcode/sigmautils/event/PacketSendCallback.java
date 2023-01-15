package com.connorcode.sigmautils.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.Packet;

public interface PacketSendCallback {
    Event<PacketSendCallback> EVENT =
            EventFactory.createArrayBacked(PacketSendCallback.class, packetReceiveCallbacks -> event -> {
                for (PacketSendCallback i : packetReceiveCallbacks) i.handle(event);
            });

    void handle(PacketSendEvent event);

    class PacketSendEvent extends EventData {
        public Packet<?> packet;

        public PacketSendEvent(Packet<?> packet) {
            this.packet = packet;
        }

        public Packet<?> get() {
            return packet;
        }
    }
}
