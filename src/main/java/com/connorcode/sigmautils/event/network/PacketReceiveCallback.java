package com.connorcode.sigmautils.event.network;

import com.connorcode.sigmautils.event.Cancellable;
import com.connorcode.sigmautils.event.EventI;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.packet.Packet;

public interface PacketReceiveCallback extends EventI {
    Event<PacketReceiveCallback> EVENT =
            EventFactory.createArrayBacked(PacketReceiveCallback.class, packetReceiveCallbacks -> event -> {
                for (PacketReceiveCallback i : packetReceiveCallbacks)
                    i.handle(event);
            });

    void handle(PacketReceiveEvent event);

    class PacketReceiveEvent extends Cancellable {
        public Packet<?> packet;

        public PacketReceiveEvent(Packet<?> packet) {
            this.packet = packet;
        }

        public Packet<?> get() {
            return packet;
        }
    }
}
