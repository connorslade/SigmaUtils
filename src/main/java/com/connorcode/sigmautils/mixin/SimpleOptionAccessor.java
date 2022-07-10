package com.connorcode.sigmautils.mixin;

import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SimpleOption.class)
public interface SimpleOptionAccessor {
    @Accessor
    <T> T getValue();

    @Accessor
    <T> void setValue(T value);
}
