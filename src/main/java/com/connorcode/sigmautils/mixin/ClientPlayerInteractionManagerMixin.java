package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.event._interface.Interact;
import com.connorcode.sigmautils.modules.misc.NoCooldown;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @Shadow
    private int blockBreakingCooldown;

    // Idea from https://github.com/blackd/IPN-Rejects
    @Inject(method = "updateBlockBreakingProgress", at = @At("HEAD"), cancellable = true)
    void onUpdateBlockBreakingProgress(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (this.blockBreakingCooldown <= 0 || !Config.getEnabled(NoCooldown.class) || !NoCooldown.noBlockBreak.value())
            return;
        this.blockBreakingCooldown = 0;
        cir.cancel();
    }

    @Inject(method = "breakBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;onBreak(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/player/PlayerEntity;)V"))
    void onBlockBreak(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (this.blockBreakingCooldown <= 0 || !Config.getEnabled(NoCooldown.class) || !NoCooldown.noBlockBreak.value())
            return;
        this.blockBreakingCooldown = 0;
    }
    // End

    @Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
    void onInteractBlock(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        var event = new Interact.InteractBlockCallback.InteractBlockEvent(player, hand, hitResult);
        Interact.InteractBlockCallback.EVENT.invoker().handle(event);
        if (event.isCancelled()) cir.setReturnValue(event.getResult());
    }
}
