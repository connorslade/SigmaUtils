package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import net.minecraft.client.render.entity.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {
    @Redirect(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Ljava/lang/String;equals(Ljava/lang/Object;)Z"))
    private boolean isDeadmau5(String stringA, Object stringB) throws Exception {
        if (Config.getEnabled("deadmau5_ears")) return true;
        return stringA.equals(stringB);
    }
}
