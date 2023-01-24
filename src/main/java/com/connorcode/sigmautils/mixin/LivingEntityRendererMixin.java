package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.modules.rendering.FlippedEntities;
import com.connorcode.sigmautils.modules.rendering.ShowInvisibleEntities;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {
    @Inject(method = "shouldFlipUpsideDown", at = @At("HEAD"), cancellable = true)
    private static void shouldFlipUpsideDown(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (Config.getEnabled(FlippedEntities.class) && FlippedEntities.isFlipped(entity))
            cir.setReturnValue(true);
    }

    @Redirect(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"))
    void onModelRender(M instance, MatrixStack matrixStack, VertexConsumer vertexConsumer, int light, int overlay,
                       float red, float green, float blue, float alpha, T livingEntity) {
        // TODO: make work with mobs
        if (Config.getEnabled(ShowInvisibleEntities.class) && ShowInvisibleEntities.isInvisible(livingEntity) && livingEntity.getFlag(5))
            alpha = (float) ShowInvisibleEntities.opacity.value();
        instance.render(matrixStack, vertexConsumer, light, overlay, red, green, blue, alpha);
    }
}
