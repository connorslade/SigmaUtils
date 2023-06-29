package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.SigmaUtils;
import com.connorcode.sigmautils.event.misc.GameLifecycle;
import com.connorcode.sigmautils.event.network.UnknownPacketEvent;
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
        var event = new UnknownPacketEvent(packet);
        SigmaUtils.eventBus.post(event);
        if (event.isCancelled()) ci.cancel();
    }

    @Inject(method = "clearWorld", at = @At("HEAD"))
    void onClearWorld(CallbackInfo ci) {
        var event = new GameLifecycle.WorldCloseEvent();
        SigmaUtils.eventBus.post(event);
    }
}
