package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.modules._interface.NoBossBarValue;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossBarHud.class)
public class BossBarHudMixin {
    @Inject(method = "renderBossBar(Lnet/minecraft/client/gui/DrawContext;IILnet/minecraft/entity/boss/BossBar;)V", at = @At("HEAD"), cancellable = true)
    void onRenderBossBar(DrawContext context, int x, int y, BossBar bossBar, CallbackInfo ci) {
        if (Config.getEnabled(NoBossBarValue.class))
            ci.cancel();
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)I"))
    int onDrawWithShadow(DrawContext instance, TextRenderer textRenderer, Text text, int x, int y, int color) {
        if (Config.getEnabled(NoBossBarValue.class))
            y = 9 * (y / 19) + 2;
        return instance.drawTextWithShadow(textRenderer, text, x, y, color);
    }
}
