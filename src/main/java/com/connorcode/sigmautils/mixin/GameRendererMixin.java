package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.modules.rendering.NoHurtTilt;
import com.connorcode.sigmautils.modules.rendering.Zoom;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "getFov(Lnet/minecraft/client/render/Camera;FZ)D", at = @At("RETURN"), cancellable = true)
    public void getZoomLevel(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(Zoom.getFov(cir.getReturnValueD(), tickDelta));
    }

    @Inject(method = "bobViewWhenHurt", at = @At("HEAD"), cancellable = true)
    void onBobViewWhenHurt(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (Config.getEnabled(NoHurtTilt.class)) ci.cancel();
    }
}
