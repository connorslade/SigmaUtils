package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.event.PacketReceiveCallback;
import com.connorcode.sigmautils.event.PacketSendCallback;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketCallbacks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    void onChannelRead(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci) {
        PacketReceiveCallback.PacketReceiveEvent event = new PacketReceiveCallback.PacketReceiveEvent(packet);
        PacketReceiveCallback.EVENT.invoker().handle(event);
        if (event.isCancelled()) ci.cancel();
    }

    @Inject(method = "sendImmediately", at = @At("HEAD"), cancellable = true)
    void onPacketSend(Packet<?> packet, PacketCallbacks callbacks, CallbackInfo ci) {
        PacketSendCallback.PacketSendEvent event = new PacketSendCallback.PacketSendEvent(packet);
        PacketSendCallback.EVENT.invoker().handle(event);
        if (event.isCancelled()) ci.cancel();
    }
}
