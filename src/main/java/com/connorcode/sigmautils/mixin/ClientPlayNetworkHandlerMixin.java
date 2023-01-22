package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.event.UnknownPacketCallback;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
    void onCustomPayload(CustomPayloadS2CPacket packet, CallbackInfo ci) {
        UnknownPacketCallback.UnknownPacketEvent event = new UnknownPacketCallback.UnknownPacketEvent(packet);
        UnknownPacketCallback.EVENT.invoker().handle(event);
        if (event.isCancelled()) ci.cancel();
    }

    @Inject(method = "onGameJoin", at = @At("HEAD"))
    void onGameJoin(CallbackInfo ci) {
        Thread.dumpStack();
    }
}
