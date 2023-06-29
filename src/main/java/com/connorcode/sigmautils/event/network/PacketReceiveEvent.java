package com.connorcode.sigmautils.event.network;

import com.connorcode.sigmautils.event.Cancellable;
import net.minecraft.network.packet.Packet;

public class PacketReceiveEvent extends Cancellable {
    public Packet<?> packet;

    public PacketReceiveEvent(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> get() {
        return packet;
    }
}