package com.connorcode.sigmautils.misc;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class CancelledUnknownPacket implements CustomPayload {
    Identifier id;

    public CancelledUnknownPacket(Identifier id) {
        this.id = id;
    }

    @Override
    public void write(PacketByteBuf buf) {

    }

    @Override
    public Identifier id() {
        return this.id;
    }
}
