package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.modules.misc.ForceGameTime;
import com.connorcode.sigmautils.modules.rendering.NoDarkSky;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientWorld.Properties.class)
public class ClientWorldPropertiesMixin {
    @Inject(method = "getTimeOfDay", at = @At("RETURN"), cancellable = true)
    void onGetTimeOfDay(CallbackInfoReturnable<Long> cir) {
        if (Config.getEnabled(ForceGameTime.class))
            cir.setReturnValue((long) ForceGameTime.forceTime.intValue());
    }

    @Inject(method = "getTime", at = @At("RETURN"), cancellable = true)
    void onGetTime(CallbackInfoReturnable<Long> cir) {
        if (Config.getEnabled(ForceGameTime.class))
            cir.setReturnValue((long) ForceGameTime.forceTime.intValue());
    }

    @Inject(method = "getSkyDarknessHeight", at = @At("RETURN"), cancellable = true)
    void onGetSkyDarknessHeight(CallbackInfoReturnable<Double> cir) {
        if (Config.getEnabled(NoDarkSky.class))
            cir.setReturnValue(-Double.MAX_VALUE);
    }
}
