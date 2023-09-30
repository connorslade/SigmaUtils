package com.connorcode.sigmautils.event.network;

import com.connorcode.sigmautils.event.Cancellable;
import net.minecraft.network.packet.Packet;

public class PacketSendEvent extends Cancellable {
    public Packet<?> packet;
    public boolean flush;

    public PacketSendEvent(Packet<?> packet, boolean flush) {
        this.packet = packet;
        this.flush = flush;
    }

    public Packet<?> get() {
        return packet;
    }
}