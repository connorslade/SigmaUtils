package com.connorcode.sigmautils.mixin.modmenu;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.modules._interface.RandomBackground;
import com.mojang.blaze3d.systems.RenderSystem;
import com.terraformersmc.modmenu.gui.widget.DescriptionListWidget;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = DescriptionListWidget.class, remap = false)
public class DescriptionListWidgetMixin {
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/util/Identifier;)V"))
    private void render(int texture, Identifier id) {
        if (Config.getEnabled(RandomBackground.class)) id = RandomBackground.getTexture();
        RenderSystem.setShaderTexture(texture, id);
    }
}
