package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossBarHud.class)
public class BossBarHudMixin {
    @Inject(method = "renderBossBar(Lnet/minecraft/client/util/math/MatrixStack;IILnet/minecraft/entity/boss/BossBar;)V", at = @At("HEAD"), cancellable = true)
    void onRenderBossBar(MatrixStack matrices, int x, int y, BossBar bossBar, CallbackInfo ci) throws Exception {
        if (Config.getEnabled("no_boss_bar_value")) ci.cancel();
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I"))
    int onDrawWithShadow(TextRenderer instance, MatrixStack matrices, Text text, float x, float y, int color) throws Exception {
        if (Config.getEnabled("no_boss_bar_value")) y = 9 * (y / 19) + 4;
        return instance.drawWithShadow(matrices, text, x, y, color);
    }
}
