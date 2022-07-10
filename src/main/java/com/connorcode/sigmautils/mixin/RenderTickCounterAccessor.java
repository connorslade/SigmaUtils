package com.connorcode.sigmautils.mixin;

import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderTickCounter.class)
public interface RenderTickCounterAccessor {
    @Accessor("tickTime")
    @Mutable
    void tickTime(float tickTime);
}
