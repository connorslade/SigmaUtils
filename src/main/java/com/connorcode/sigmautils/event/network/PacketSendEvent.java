package com.connorcode.sigmautils.event.network;

import com.connorcode.sigmautils.event.Cancellable;
import net.minecraft.network.packet.Packet;

public class PacketSendEvent extends Cancellable {
    public Packet<?> packet;

    public PacketSendEvent(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> get() {
        return packet;
    }
}