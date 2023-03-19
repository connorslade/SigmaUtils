package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.event.MouseScrollCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
    void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (client.getWindow().getHandle() != window) return;
        var event = new MouseScrollCallback.MouseScrollEvent(horizontal, vertical);
        MouseScrollCallback.EVENT.invoker().handle(event);
        if (event.isCancelled()) ci.cancel();
    }
}
