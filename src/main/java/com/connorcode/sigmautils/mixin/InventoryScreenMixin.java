package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.SigmaUtils;
import com.connorcode.sigmautils.modules._interface.UiTweaks;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler>
        implements RecipeBookProvider {
    public InventoryScreenMixin(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Inject(method = "drawEntity(Lnet/minecraft/client/util/math/MatrixStack;IIILorg/joml/Quaternionf;Lorg/joml/Quaternionf;Lnet/minecraft/entity/LivingEntity;)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;runAsFancy(Ljava/lang/Runnable;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void onDrawEntity(MatrixStack matrices, int x, int y, int size, Quaternionf quaternionf, Quaternionf quaternionf2, LivingEntity entity, CallbackInfo ci, double d, MatrixStack matrixStack, EntityRenderDispatcher entityRenderDispatcher, VertexConsumerProvider.Immediate immediate) {
        if (!UiTweaks.fastDoll()) return;
        var ca = ((MinecraftClientAccessor) SigmaUtils.client);
        var tickDelta = SigmaUtils.client.isPaused() ? ca.getPausedTickDelta() : ca.getRenderTickCounter().tickDelta;

        float h = entity.prevBodyYaw;
        float i = entity.prevYaw;
        float j = entity.prevPitch;
        float k = entity.prevHeadYaw;
        entity.prevBodyYaw = entity.bodyYaw;
        entity.prevYaw = entity.getYaw();
        entity.prevPitch = entity.getPitch();
        entity.prevHeadYaw = entity.headYaw;
        entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, tickDelta, matrices, immediate, 0xF000F0);
        entity.prevBodyYaw = h;
        entity.prevYaw = i;
        entity.prevPitch = j;
        entity.prevHeadYaw = k;
    }

    @Redirect(method = "drawEntity(Lnet/minecraft/client/util/math/MatrixStack;IIILorg/joml/Quaternionf;Lorg/joml/Quaternionf;Lnet/minecraft/entity/LivingEntity;)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;runAsFancy(Ljava/lang/Runnable;)V"))
    private static void onRender(Runnable runnable) {
        if (!UiTweaks.fastDoll()) runnable.run();
    }
}
