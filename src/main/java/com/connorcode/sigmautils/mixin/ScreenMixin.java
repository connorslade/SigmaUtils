package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.modules._interface.RandomBackground;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    @Shadow
    public int width;
    @Shadow
    public int height;

    @Shadow
    public static void renderBackgroundTexture(DrawContext context, Identifier texture, int x, int y, float u, float v, int width, int height) {
        throw new RuntimeException();
    }

    @Shadow
    public abstract void renderInGameBackground(DrawContext context);

    @Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true)
    void onRenderBackground(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!Config.getEnabled(RandomBackground.class) || (Screen) (Object) this instanceof GameMenuScreen || (Screen) (Object) this instanceof AdvancementsScreen)
            return;

        renderBackgroundTexture(context, RandomBackground.getTexture(), 0, 0, 0, 0, this.width, this.height);
        renderInGameBackground(context);
        ci.cancel();
    }
}
