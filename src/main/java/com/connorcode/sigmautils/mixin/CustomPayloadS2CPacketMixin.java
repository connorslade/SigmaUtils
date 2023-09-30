package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.SigmaUtils;
import com.connorcode.sigmautils.event.network.UnknownPacketEvent;
import com.connorcode.sigmautils.misc.CancelledUnknownPacket;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.UnknownCustomPayload;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(CustomPayloadS2CPacket.class)
public abstract class CustomPayloadS2CPacketMixin {
    @Shadow
    @Final
    private static Map<Identifier, PacketByteBuf.PacketReader<? extends CustomPayload>> ID_TO_READER;

    @Shadow
    private static UnknownCustomPayload readUnknownPayload(Identifier id, PacketByteBuf buf) {
        return null;
    }

    @Inject(method = "readPayload", at = @At("HEAD"), cancellable = true)
    private static void onReadPayload(Identifier id, PacketByteBuf buf, CallbackInfoReturnable<CustomPayload> cir) {
        if (ID_TO_READER.containsKey(id)) return;

        var event = new UnknownPacketEvent(buf, id);
        SigmaUtils.eventBus.post(event);

        if (event.isCancelled()) cir.setReturnValue(new CancelledUnknownPacket(id));
        else cir.setReturnValue(readUnknownPayload(id, buf));
    }
}
