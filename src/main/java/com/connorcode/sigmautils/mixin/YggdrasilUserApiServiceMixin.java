package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.mojang.authlib.minecraft.TelemetrySession;
import com.mojang.authlib.yggdrasil.YggdrasilUserApiService;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.Executor;

@Mixin(YggdrasilUserApiService.class)
public class YggdrasilUserApiServiceMixin {
    @Inject(method = "newTelemetrySession", at = @At("RETURN"), cancellable = true)
    void onNewTelemetrySession(Executor executor, CallbackInfoReturnable<TelemetrySession> cir) throws Exception {
        if (Config.getEnabled("no_telemetry")) cir.setReturnValue(TelemetrySession.DISABLED);
    }

    @Inject(method = "fetchProperties", at = @At("HEAD"), cancellable = true)
    void onFetchProperties(CallbackInfo ci) throws Exception {
        if (Config.getEnabled("no_telemetry")) ci.cancel();
    }
}
