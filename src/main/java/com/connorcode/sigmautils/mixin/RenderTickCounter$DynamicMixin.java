package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.modules.misc.TickSpeed;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(RenderTickCounter.Dynamic.class)
public class RenderTickCounter$DynamicMixin {
    @ModifyArg(method = "beginRenderTick(J)I", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/floats/FloatUnaryOperator;apply(F)F", remap = false))
    float onBeginRenderTick(float v) {
        if (Config.getEnabled(TickSpeed.class)) return (float) TickSpeed.mspt.value();
        return v;
    }
}
