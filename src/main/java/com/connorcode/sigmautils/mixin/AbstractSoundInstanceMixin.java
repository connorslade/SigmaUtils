package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.modules.misc.SoundControl;
import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSoundInstance.class)
public class AbstractSoundInstanceMixin {
    @Shadow
    @Final
    protected Identifier id;

    @Inject(method = "getVolume", at = @At("RETURN"), cancellable = true)
    void onGetVolume(CallbackInfoReturnable<Float> cir) {
        if (Config.getEnabled(SoundControl.class))
            cir.setReturnValue(cir.getReturnValueF() * SoundControl.getVolumeAdjuster(this.id));
    }
}
