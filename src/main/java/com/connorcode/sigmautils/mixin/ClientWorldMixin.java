package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.modules.rendering.AllowShowBarrier;
import net.minecraft.block.Block;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin {
    @Shadow
    @Final
    private static Set<Item> BLOCK_MARKER_ITEMS;

    @Shadow
    public abstract void randomBlockDisplayTick(int centerX, int centerY, int centerZ, int radius, Random random, @Nullable Block block, BlockPos.Mutable pos);

    @Inject(method = "doRandomBlockDisplayTicks", at = @At("HEAD"), cancellable = true)
    void onDoRandomBlockDisplayTicks(int centerX, int centerY, int centerZ, CallbackInfo ci) {
        if (!Config.getEnabled(AllowShowBarrier.class)) return;
        Random random = Random.create();
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (Item i : BLOCK_MARKER_ITEMS) {
            Block block = ((BlockItem) i).getBlock();
            for (int j = 0; j < 667; ++j) {
                this.randomBlockDisplayTick(centerX, centerY, centerZ, 16, random, block, mutable);
                this.randomBlockDisplayTick(centerX, centerY, centerZ, 32, random, block, mutable);
            }
        }
        ci.cancel();
    }
}
