package com.connorcode.sigmautils.mixin;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(Screen.class)
public interface ScreenAccessor {
    @Accessor
    int getWidth();

    @Accessor
    int getHeight();

    @Invoker
    <T extends Element & Drawable & Selectable> T invokeAddDrawableChild(T drawableElement);

    @Invoker
    void invokeRenderOrderedTooltip(MatrixStack matrices, List<OrderedText> sigma_utils, int mouseX, int mouseY);
}
