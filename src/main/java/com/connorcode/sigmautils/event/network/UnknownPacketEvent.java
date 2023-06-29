package com.connorcode.sigmautils.event.network;

import com.connorcode.sigmautils.event.Cancellable;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;

public class UnknownPacketEvent extends Cancellable {
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