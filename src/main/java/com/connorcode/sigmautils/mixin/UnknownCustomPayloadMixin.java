package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.SigmaUtils;
import com.connorcode.sigmautils.event.network.UnknownPacketEvent;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.UnknownCustomPayload;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.connorcode.sigmautils.misc.Consts.DUMMY_IDENT;

@Mixin(UnknownCustomPayload.class)
public class UnknownCustomPayloadMixin {
    @Inject(method = "method_56491(ILnet/minecraft/util/Identifier;Lnet/minecraft/network/PacketByteBuf;)Lnet/minecraft/network/packet/UnknownCustomPayload;", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketByteBuf;skipBytes(I)Lnet/minecraft/network/PacketByteBuf;", shift = At.Shift.BEFORE), cancellable = true)
    private static void onUnknownCustomPayload(int i, Identifier identifier, PacketByteBuf buf, CallbackInfoReturnable<UnknownCustomPayload> cir) {
        var event = new UnknownPacketEvent(buf, identifier);
        SigmaUtils.eventBus.post(event);
        if (event.isCancelled()) cir.setReturnValue(new UnknownCustomPayload(DUMMY_IDENT));
    }
}
