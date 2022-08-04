package com.connorcode.sigmautils.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.CLIENT)
@Mixin(CustomPayloadC2SPacket.class)
public interface CustomPayloadC2SPacketAccessor {
    @Accessor
    Identifier getChannel();

    @Accessor
    PacketByteBuf getData();

    @Mutable
    @Accessor
    void setData(PacketByteBuf data);
}
