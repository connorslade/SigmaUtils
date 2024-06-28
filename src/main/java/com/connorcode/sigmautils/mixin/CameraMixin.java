package com.connorcode.sigmautils.mixin;

import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Camera.class)
public class CameraMixin {
//    @ModifyVariable(method = "clipToSpace", at = @At("HEAD"), argsOnly = true)
//    double clipToSpace(double desiredCameraDistance) {
//        if (!Config.getEnabled(CameraDistance.class))
//            return desiredCameraDistance;
//        return CameraDistance.getDistance();
//    }
//
//    @Inject(method = "clipToSpace", at = @At("HEAD"), cancellable = true)
//    void onClipToSpace(double desiredCameraDistance, CallbackInfoReturnable<Double> cir) {
//        if (!Config.getEnabled(CameraClip.class))
//            return;
//        cir.setReturnValue(desiredCameraDistance);
//    }
}
