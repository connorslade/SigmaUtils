package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallSignBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Shadow
    @Nullable
    public HitResult crosshairTarget;

    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Shadow
    @Nullable
    public ClientWorld world;

    @Inject(method = "hasOutline", at = @At("RETURN"), cancellable = true)
    void onHasOutline(Entity entity, CallbackInfoReturnable<Boolean> cir) throws Exception {
        if (Config.getEnabled("glowing_players") && entity instanceof PlayerEntity) cir.setReturnValue(true);
    }

    @Inject(method = "doItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;"))
    void onGetStackInHand(CallbackInfo ci) throws Exception {
        if (!Config.getEnabled("sign_click_through") || crosshairTarget == null || Objects.requireNonNull(player)
                .isSneaking()) return;

        // Handle Item frames
        if (crosshairTarget.getType() == HitResult.Type.ENTITY &&
                ((EntityHitResult) crosshairTarget).getEntity() instanceof ItemFrameEntity itemFrameEntity) {
            BlockPos behindBlockPos = itemFrameEntity.getDecorationBlockPos()
                    .offset(itemFrameEntity.getHorizontalFacing()
                            .getOpposite());

            crosshairTarget =
                    new BlockHitResult(crosshairTarget.getPos(), itemFrameEntity.getHorizontalFacing(), behindBlockPos,
                            false);
        }

        // Handle Wall Signs
        if (crosshairTarget.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos = ((BlockHitResult) crosshairTarget).getBlockPos();
            BlockState blockState = Objects.requireNonNull(world)
                    .getBlockState(blockPos);
            if (!(blockState.getBlock() instanceof WallSignBlock)) return;

            crosshairTarget =
                    new BlockHitResult(crosshairTarget.getPos(), ((BlockHitResult) crosshairTarget).getSide(),
                            blockPos.offset(blockState.get(
                                            WallSignBlock.FACING)
                                    .getOpposite()),
                            false);
        }
    }
}
