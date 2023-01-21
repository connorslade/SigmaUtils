package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.modules._interface.UiTweaks;
import com.connorcode.sigmautils.modules.server.VictoryMute;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.sound.SoundCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundSystem.class)
public class SoundSystemMixin {
    @Inject(method = "getAdjustedVolume(FLnet/minecraft/sound/SoundCategory;)F", at = @At("HEAD"), cancellable = true)
    void onGetAdjustedVolume(float volume, SoundCategory category, CallbackInfoReturnable<Float> cir) {
        if (VictoryMute.muted)
            cir.setReturnValue((float) (cir.getReturnValueF() * (1f - VictoryMute.reduction.value())));
        if (UiTweaks.isMuted()) cir.setReturnValue(0f);
    }
}
