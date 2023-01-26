package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.modules.chat.NoChatSignatures;
import com.mojang.authlib.minecraft.TelemetrySession;
import com.mojang.authlib.yggdrasil.YggdrasilUserApiService;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.Executor;

@Mixin(value = YggdrasilUserApiService.class, remap = false)
public class YggdrasilUserApiServiceMixin {
    @Inject(method = "newTelemetrySession", at = @At("HEAD"), cancellable = true)
    private void onNewTelemetrySession(Executor executor, CallbackInfoReturnable<TelemetrySession> cir) {
        if (Config.getEnabled(NoChatSignatures.class)) cir.setReturnValue(TelemetrySession.DISABLED);
    }
}
