package com.connorcode.sigmautils.event.network;

import com.connorcode.sigmautils.event.Event;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class UnknownPacketEvent implements Event {
    public Identifier identifier;
    PacketByteBuf buf;

    public UnknownPacketEvent(PacketByteBuf buf, Identifier identifier) {
        this.buf = buf;
        this.identifier = identifier;
    }

    public PacketByteBuf get() {
        return buf;
    }

    public Identifier getIdentifier() {
        return identifier;
    }
}