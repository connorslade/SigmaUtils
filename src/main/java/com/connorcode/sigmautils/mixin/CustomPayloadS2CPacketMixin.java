package com.connorcode.sigmautils.mixin;

import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(CustomPayloadS2CPacket.class)
public class CustomPayloadS2CPacketMixin {
    @Inject(method = "apply", at = @At("HEAD"))
    void onApply() {

    }
}
