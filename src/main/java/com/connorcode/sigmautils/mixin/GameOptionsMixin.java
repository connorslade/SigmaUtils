package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.modules.server.VictoryMute;
import net.minecraft.client.option.GameOptions;
import net.minecraft.sound.SoundCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameOptions.class)
public class GameOptionsMixin {
    @Inject(method = "getSoundVolume", at = @At("HEAD"), cancellable = true)
    void onGetSoundVolume(SoundCategory category, CallbackInfoReturnable<Float> cir) {
        if (VictoryMute.muted) cir.setReturnValue(0f);
    }
}
