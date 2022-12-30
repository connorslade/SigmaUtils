package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.modules.misc.DisableFrontPerspective;
import net.minecraft.client.option.Perspective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Perspective.class)
public abstract class PerspectiveMixin {
    @Shadow
    public static Perspective[] values() {
        return new Perspective[0];
    }

    @Inject(method = "next", at = @At("RETURN"), cancellable = true)
    void onNext(CallbackInfoReturnable<Perspective> cir) {
        if (!Config.getEnabled(DisableFrontPerspective.class)) return;
        cir.setReturnValue(DisableFrontPerspective.nextPerspective(values()[(cir.getReturnValue()
                .ordinal() + 2) % 3]));
    }
}
