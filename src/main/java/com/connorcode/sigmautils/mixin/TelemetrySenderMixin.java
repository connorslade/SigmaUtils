package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.modules.misc.NoTelemetry;
import net.minecraft.SharedConstants;
import net.minecraft.client.util.telemetry.TelemetrySender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TelemetrySender.class)
public class TelemetrySenderMixin {
    @Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/SharedConstants;isDevelopment:Z"))
    boolean isDevelopment() {
        if (Config.getEnabled(NoTelemetry.class)) return true;
        return SharedConstants.isDevelopment;
    }
}
