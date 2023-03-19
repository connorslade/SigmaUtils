package com.connorcode.sigmautils.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftClient.class)
public interface MinecraftClientAccessor {
    @Accessor
    static int getCurrentFps() {
        throw new RuntimeException();
    }

    @Accessor
    float getPausedTickDelta();

    @Accessor
    RenderTickCounter getRenderTickCounter();
}