package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.modules._interface.RandomBackground;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static net.minecraft.client.gui.DrawableHelper.OPTIONS_BACKGROUND_TEXTURE;

@Mixin(EntryListWidget.class)
public class EntryListWidgetMixin {
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/util/Identifier;)V", ordinal = 0))
    private void setShaderTexture(int texture, Identifier id) {
        if (Config.getEnabled(RandomBackground.class)) RandomBackground.setTexture();
        else RenderSystem.setShaderTexture(0, OPTIONS_BACKGROUND_TEXTURE);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/util/Identifier;)V", ordinal = 1))
    private void setShaderTexture2(int texture, Identifier id) {
        if (Config.getEnabled(RandomBackground.class)) RandomBackground.setTexture();
        else RenderSystem.setShaderTexture(0, OPTIONS_BACKGROUND_TEXTURE);
    }
}
