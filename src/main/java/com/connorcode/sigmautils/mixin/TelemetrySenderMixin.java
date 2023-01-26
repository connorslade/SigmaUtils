package com.connorcode.sigmautils.mixin;

import net.minecraft.client.util.telemetry.TelemetryManager;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TelemetryManager.class)
public class TelemetrySenderMixin {
    // TODO: Fix this
//    @Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/SharedConstants;isDevelopment:Z"))
//    boolean isDevelopment() {
//        if (Config.getEnabled(NoTelemetry.class))
//            return true;
//        return SharedConstants.isDevelopment;
//    }
}
