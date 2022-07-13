package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import net.minecraft.client.option.Perspective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Perspective.class)
public class PerspectiveMixin {
    @Inject(method = "next", at = @At("RETURN"), cancellable = true)
    void onNext(CallbackInfoReturnable<Perspective> cir) throws Exception {
        if (!Config.getEnabled("disable_front_perspective")) return;
        cir.setReturnValue(Perspective.values()[cir.getReturnValue()
                .ordinal() % 2]);
    }
}
