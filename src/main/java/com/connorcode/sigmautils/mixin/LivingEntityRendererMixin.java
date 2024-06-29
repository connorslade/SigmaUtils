package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.modules.rendering.FlippedEntities;
import com.connorcode.sigmautils.modules.rendering.ShowInvisibleEntities;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {
    @Inject(method = "shouldFlipUpsideDown", at = @At("HEAD"), cancellable = true)
    private static void shouldFlipUpsideDown(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (Config.getEnabled(FlippedEntities.class) && FlippedEntities.isFlipped(entity)) cir.setReturnValue(true);
    }

    @Redirect(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;isVisible(Lnet/minecraft/entity/LivingEntity;)Z"))
    boolean isVisible(LivingEntityRenderer<T, M> livingEntityRenderer, T entity) {
        return !entity.getFlag(5);
    }

    @Redirect(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isInvisibleTo(Lnet/minecraft/entity/player/PlayerEntity;)Z"))
    boolean isVisibleTo(LivingEntity instance, PlayerEntity playerEntity) {
        if (Config.getEnabled(ShowInvisibleEntities.class) && ShowInvisibleEntities.isVisible(instance)) return false;
        return instance.isInvisibleTo(playerEntity);
    }
}
