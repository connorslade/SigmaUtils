package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.SigmaUtils;
import com.connorcode.sigmautils.modules._interface.UiTweaks;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> implements RecipeBookProvider {
    public InventoryScreenMixin(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Redirect(method = "drawEntity(Lnet/minecraft/client/gui/DrawContext;IIIIIFFFLnet/minecraft/entity/LivingEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;enableScissor(IIII)V"))
    private static void onEnableScissor(DrawContext instance, int x1, int y1, int x2, int y2) {
        if (!UiTweaks.unMaskedDoll()) instance.enableScissor(x1, y1, x2, y2);
    }

    @Redirect(method = "drawEntity(Lnet/minecraft/client/gui/DrawContext;IIIIIFFFLnet/minecraft/entity/LivingEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;disableScissor()V"))
    private static void onDisableScissor(DrawContext instance) {
        if (!UiTweaks.unMaskedDoll()) instance.disableScissor();
    }

    @Inject(method = "drawEntity(Lnet/minecraft/client/gui/DrawContext;FFFLorg/joml/Vector3f;Lorg/joml/Quaternionf;Lorg/joml/Quaternionf;Lnet/minecraft/entity/LivingEntity;)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;runAsFancy(Ljava/lang/Runnable;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void onDrawEntity(DrawContext context, float x, float y, float size, Vector3f vector3f, Quaternionf quaternionf, Quaternionf quaternionf2, LivingEntity entity, CallbackInfo ci, EntityRenderDispatcher entityRenderDispatcher) {
        if (!UiTweaks.fastDoll()) return;
        var tickDelta = SigmaUtils.client.getRenderTickCounter().getTickDelta(true);

        float h = entity.prevBodyYaw;
        float i = entity.prevYaw;
        float j = entity.prevPitch;
        float k = entity.prevHeadYaw;
        entity.prevBodyYaw = entity.bodyYaw;
        entity.prevYaw = entity.getYaw();
        entity.prevPitch = entity.getPitch();
        entity.prevHeadYaw = entity.headYaw;
        entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, tickDelta, context.getMatrices(), context.getVertexConsumers(), 0xF000F0);
        entity.prevBodyYaw = h;
        entity.prevYaw = i;
        entity.prevPitch = j;
        entity.prevHeadYaw = k;
    }

    @Redirect(method = "drawEntity(Lnet/minecraft/client/gui/DrawContext;FFFLorg/joml/Vector3f;Lorg/joml/Quaternionf;Lorg/joml/Quaternionf;Lnet/minecraft/entity/LivingEntity;)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;runAsFancy(Ljava/lang/Runnable;)V"))
    private static void onRender(Runnable runnable) {
        if (!UiTweaks.fastDoll()) runnable.run();
    }
}
