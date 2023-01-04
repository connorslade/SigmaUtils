package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.modules.misc.CameraClip;
import com.connorcode.sigmautils.modules.misc.CameraDistance;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public class CameraMixin {
    @ModifyVariable(method = "clipToSpace", at = @At("HEAD"), argsOnly = true)
    double clipToSpace(double desiredCameraDistance) {
        if (!Config.getEnabled(CameraDistance.class)) return desiredCameraDistance;
        return CameraDistance.distance.value();
    }

    @Inject(method = "clipToSpace", at = @At("HEAD"), cancellable = true)
    void onClipToSpace(double desiredCameraDistance, CallbackInfoReturnable<Double> cir) {
        if (!Config.getEnabled(CameraClip.class)) return;
        cir.setReturnValue(desiredCameraDistance);
    }
}
