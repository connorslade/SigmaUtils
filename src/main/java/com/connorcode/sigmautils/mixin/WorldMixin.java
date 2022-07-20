package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.modules.misc.ForceWeather;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
@Environment(EnvType.CLIENT)
public class WorldMixin {
    @Inject(method = "getRainGradient", at = @At("HEAD"), cancellable = true)
    void onIsRaining(CallbackInfoReturnable<Float> cir) throws Exception {
        if (Config.getEnabled("force_weather")) cir.setReturnValue(ForceWeather.weather > 0 ? 1f : 0f);
    }

    @Inject(method = "getThunderGradient", at = @At("HEAD"), cancellable = true)
    void onIsThundering(CallbackInfoReturnable<Float> cir) throws Exception {
        if (Config.getEnabled("force_weather")) cir.setReturnValue(ForceWeather.weather == 2 ? 1f : 0f);
    }
}
