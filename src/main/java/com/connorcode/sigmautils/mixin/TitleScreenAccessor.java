package com.connorcode.sigmautils.mixin;

import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TitleScreen.class)
public interface TitleScreenAccessor {
    @Invoker
    void invokeInitWidgetsNormal(int y, int spacingY);
}
