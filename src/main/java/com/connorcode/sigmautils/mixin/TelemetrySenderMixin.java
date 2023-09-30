package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.modules.misc.NoTelemetry;
import net.minecraft.client.session.telemetry.TelemetryManager;
import net.minecraft.client.session.telemetry.TelemetrySender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TelemetryManager.class)
public class TelemetrySenderMixin {
    @Inject(method = "getSender", at = @At("HEAD"), cancellable = true)
    void onGetSender(CallbackInfoReturnable<TelemetrySender> cir) {
        if (Config.getEnabled(NoTelemetry.class))
            cir.setReturnValue(TelemetrySender.NOOP);
    }
}
