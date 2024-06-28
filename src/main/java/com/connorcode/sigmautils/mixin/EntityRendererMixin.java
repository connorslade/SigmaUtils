package com.connorcode.sigmautils.mixin;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity> {
    // TODO: fix
//    @Redirect(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Ljava/lang/String;equals(Ljava/lang/Object;)Z"))
//    private boolean isDeadmau5(String stringA, Object stringB) {
//        if (Config.getEnabled(Deadmau5Ears.class))
//            return true;
//        return stringA.equals(stringB);
//    }
//
//    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
//    private void renderLabelIfPresent(T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
//                                      int light, CallbackInfo ci) {
//        if (Titles.tamableOwner.value() && (entity instanceof TameableEntity || entity instanceof AbstractHorseEntity))
//            ci.cancel();
//    }
}
