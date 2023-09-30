package com.connorcode.sigmautils.event.network;

import com.connorcode.sigmautils.event.Cancellable;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class UnknownPacketEvent extends Cancellable {
    public Identifier identifier;

    public UnknownPacketEvent(CustomPayload payload) {
        this.identifier = payload.id();
    }

//    public CustomPayloadS2CPacket get() {
//        return packet;
//    }

    public Identifier getIdentifier() {
        return identifier;
    }
}