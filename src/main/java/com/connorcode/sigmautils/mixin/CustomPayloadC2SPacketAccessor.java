package com.connorcode.sigmautils.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import org.spongepowered.asm.mixin.Mixin;

@Environment(EnvType.CLIENT)
@Mixin(CustomPayloadC2SPacket.class)
public interface CustomPayloadC2SPacketAccessor {
//    @Accessor
//    Identifier getChannel();
//
//    @Accessor
//    PacketByteBuf getData();
//
//    @Mutable
//    @Accessor
//    void setData(PacketByteBuf data);
}
