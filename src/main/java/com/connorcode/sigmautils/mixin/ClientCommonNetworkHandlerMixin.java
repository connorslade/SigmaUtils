package com.connorcode.sigmautils.mixin;

import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientCommonNetworkHandler.class)
public class ClientCommonNetworkHandlerMixin {
    @Inject(method = "onCustomPayload", at = @At("HEAD"))
    void onCustomPayload(CustomPayloadS2CPacket packet, CallbackInfo ci) {
//        var buf = packet.getPayload();
//        var event = new UnknownPacketEvent(buf, packet.payload().getId().id());
        System.out.println("CUSTOM PAYLOAD:" + packet.payload().getClass().getSimpleName());
    }
}


//    @Inject(method = "readPayload", at = @At("HEAD"), cancellable = true)
//    private static void onReadPayload(Identifier id, PacketByteBuf buf, CallbackInfoReturnable<CustomPayload> cir) {
//        if (ID_TO_READER.containsKey(id)) return;
//
//        var event = new UnknownPacketEvent(buf, id);
//        SigmaUtils.eventBus.post(event);
//
//        if (event.isCancelled()) cir.setReturnValue(new CancelledUnknownPacket(id));
//        else cir.setReturnValue(readUnknownPayload(id, buf));
//    }