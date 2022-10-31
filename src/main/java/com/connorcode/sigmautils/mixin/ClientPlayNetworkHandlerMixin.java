package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.modules.misc.ForceFly;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method = "onPlayerAbilities", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    void onPlayerAbilities(PlayerAbilitiesS2CPacket packet, CallbackInfo ci, PlayerEntity playerEntity) {
        ForceFly.oldValue = packet.allowFlying();
        if (!Config.getEnabled(ForceFly.class)) return;
        playerEntity.getAbilities().allowFlying = true;
    }
}
