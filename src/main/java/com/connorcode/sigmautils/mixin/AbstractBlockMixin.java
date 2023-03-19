package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.modules.rendering.TextureRotations;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.class)
public class AbstractBlockMixin {
    @Inject(method = "getRenderingSeed", at = @At("HEAD"), cancellable = true)
    void onGetRenderingSeed(BlockState state, BlockPos pos, CallbackInfoReturnable<Long> cir) {
        if (!Config.getEnabled(TextureRotations.class)) return;
        cir.setReturnValue(TextureRotations.hash(pos.getX(), pos.getY(), pos.getZ()));
    }
}
