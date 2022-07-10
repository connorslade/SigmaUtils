package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public class CameraMixin {
    @Inject(method = "clipToSpace", at = @At("HEAD"), cancellable = true)
    void onClipToSpace(double desiredCameraDistance, CallbackInfoReturnable<Double> cir) throws Exception {
        if (!Config.getEnabled("camera_clip")) return;
        cir.setReturnValue(desiredCameraDistance);
    }
}
