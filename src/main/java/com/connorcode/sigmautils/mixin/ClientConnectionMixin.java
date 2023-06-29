package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.SigmaUtils;
import com.connorcode.sigmautils.event.network.PacketReceiveEvent;
import com.connorcode.sigmautils.event.network.PacketSendEvent;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"), cancellable = true)
    void onChannelRead(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci) {
        var event = new PacketReceiveEvent(packet);
        SigmaUtils.eventBus.post(event);
        if (event.isCancelled()) ci.cancel();
    }

    @Inject(method = "sendImmediately", at = @At("HEAD"), cancellable = true)
    void onPacketSend(Packet<?> packet, PacketCallbacks callbacks, CallbackInfo ci) {
        var event = new PacketSendEvent(packet);
        SigmaUtils.eventBus.post(event);
        if (event.isCancelled()) ci.cancel();
    }
}
