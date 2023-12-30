package com.connorcode.sigmautils.mixin.modmenu;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.modules._interface.RandomBackground;
import com.mojang.blaze3d.systems.RenderSystem;
import com.terraformersmc.modmenu.gui.widget.DescriptionListWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DescriptionListWidget.class)
public class DescriptionListWidgetMixin {
    @Inject(method = "renderList", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/util/Identifier;)V", shift = At.Shift.AFTER))
    private void render(DrawContext DrawContext, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        var id = Config.getEnabled(RandomBackground.class) ?
                RandomBackground.getTexture() : Screen.OPTIONS_BACKGROUND_TEXTURE;
        RenderSystem.setShaderTexture(0, id);
    }
}
