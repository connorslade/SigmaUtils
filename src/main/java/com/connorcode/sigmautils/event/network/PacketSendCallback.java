package com.connorcode.sigmautils.event.network;

import com.connorcode.sigmautils.event.Cancellable;
import com.connorcode.sigmautils.event.EventI;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.packet.Packet;

public interface PacketSendCallback extends EventI {
    Event<PacketSendCallback> EVENT =
            EventFactory.createArrayBacked(PacketSendCallback.class, packetReceiveCallbacks -> event -> {
                for (PacketSendCallback i : packetReceiveCallbacks)
                    i.handle(event);
            });

    void handle(PacketSendEvent event);

    class PacketSendEvent extends Cancellable {
        public Packet<?> packet;

        public PacketSendEvent(Packet<?> packet) {
            this.packet = packet;
        }

        public Packet<?> get() {
            return packet;
        }
    }
}
