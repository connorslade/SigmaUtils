package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.modules._interface.RandomBackground;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;
import java.util.Random;

import static com.connorcode.sigmautils.modules._interface.RandomBackground.assetIndex;
import static net.minecraft.client.gui.DrawableHelper.OPTIONS_BACKGROUND_TEXTURE;

@Mixin(Screen.class)
public class ScreenMixin {
    int screenHash = -1;

    @Redirect(method = "renderBackgroundTexture", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/util/Identifier;)V"))
    private void setShaderTexture(int texture, Identifier id) {
        if (Config.getEnabled("random_background")) {
            int currentScreenHash = Objects.requireNonNull(MinecraftClient.getInstance().currentScreen)
                    .hashCode();

            if (screenHash != currentScreenHash || assetIndex < 0) {
                screenHash = currentScreenHash;
                assetIndex = new Random().nextInt(RandomBackground.validBackgrounds.size());
            }
            RenderSystem.setShaderTexture(0, Identifier.tryParse("textures/block/" +
                    RandomBackground.validBackgrounds.get(assetIndex) + ".png"));
        } else RenderSystem.setShaderTexture(0, OPTIONS_BACKGROUND_TEXTURE);
    }
}
