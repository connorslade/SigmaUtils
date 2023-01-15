package com.connorcode.sigmautils.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;

public interface UnknownPacketCallback {
    Event<UnknownPacketCallback> EVENT =
            EventFactory.createArrayBacked(UnknownPacketCallback.class, unknownPacketCallbacks -> event -> {
                for (UnknownPacketCallback i : unknownPacketCallbacks) i.handle(event);
            });

    void handle(UnknownPacketEvent unknownPacket);

    class UnknownPacketEvent extends EventData {
        public CustomPayloadS2CPacket packet;
        public Identifier identifier;

        public UnknownPacketEvent(CustomPayloadS2CPacket packet) {
            this.packet = packet;
            this.identifier = packet.getChannel();
        }

        public CustomPayloadS2CPacket get() {
            return packet;
        }

        public Identifier getIdentifier() {
            return identifier;
        }
    }
}
