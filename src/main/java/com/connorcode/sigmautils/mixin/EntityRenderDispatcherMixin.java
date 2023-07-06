package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.SigmaUtils;
import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.event.render.EntityRender;
import com.connorcode.sigmautils.modules.rendering.DisableShadows;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @Inject(method = "renderShadow", at = @At("HEAD"), cancellable = true)
    private static void onRenderShadow(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Entity entity,
                                       float opacity, float tickDelta, WorldView world, float radius, CallbackInfo ci) {
        if (Config.getEnabled(DisableShadows.class) && DisableShadows.isDisabled(entity))
            ci.cancel();
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderer;render(Lnet/minecraft/entity/Entity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", shift = At.Shift.BEFORE), cancellable = true)
    <E extends Entity> void onRender(E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        var event =
                new EntityRender.EntityRenderEvent<>(entity, x, y, z, yaw, tickDelta, matrices, vertexConsumers, light);
        SigmaUtils.eventBus.post(event);
        if (event.isCancelled()) ci.cancel();
    }
}
