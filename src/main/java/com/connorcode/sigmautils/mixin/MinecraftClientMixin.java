package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.SigmaUtils;
import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.event.ScreenOpenCallback;
import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.modules._interface.InventoryMove;
import com.connorcode.sigmautils.modules._interface.SignClickThrough;
import com.connorcode.sigmautils.modules.misc.NoPause;
import com.connorcode.sigmautils.modules.misc.WindowTitle;
import com.connorcode.sigmautils.modules.rendering.GlowingPlayers;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallSignBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow
    @Nullable
    public HitResult crosshairTarget;

    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Shadow
    @Nullable
    public ClientWorld world;

    @Shadow
    @Nullable
    public Screen currentScreen;
    @Shadow
    @Final
    public GameOptions options;
    @Shadow
    @Nullable
    private Overlay overlay;
    @Shadow
    @Nullable
    private IntegratedServer server;
    @Shadow
    private volatile boolean paused;

    @Shadow
    public abstract boolean isIntegratedServerRunning();

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderTickCounter;beginRenderTick(J)I", shift = At.Shift.AFTER))
    void onTick(boolean tick, CallbackInfo ci) {
        assert tick;
        SigmaUtils.modules.values().forEach(Module::tick);
    }

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    void onSetScreen(Screen screen, CallbackInfo ci) {
        ScreenOpenCallback.ScreenOpenEvent event = new ScreenOpenCallback.ScreenOpenEvent(screen);
        ScreenOpenCallback.EVENT.invoker().handle(event);
        if (event.cancel) ci.cancel();
    }

    @Inject(method = "hasOutline", at = @At("RETURN"), cancellable = true)
    void onHasOutline(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (Config.getEnabled(GlowingPlayers.class) && GlowingPlayers.renderGlowing(entity) &&
                !(GlowingPlayers.disableF1.value() && this.options.hudHidden)) cir.setReturnValue(true);
    }

    @Inject(method = "doItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;"))
    void onGetStackInHand(CallbackInfo ci) {
        if (!Config.getEnabled(SignClickThrough.class) || crosshairTarget == null || Objects.requireNonNull(player)
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

            crosshairTarget = new BlockHitResult(crosshairTarget.getPos(), ((BlockHitResult) crosshairTarget).getSide(),
                    blockPos.offset(blockState.get(WallSignBlock.FACING)
                            .getOpposite()), false);
        }
    }

    // For inventory_move
    @Redirect(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/Screen;passEvents:Z", opcode = Opcodes.GETFIELD))
    boolean passScreenEvents(Screen instance) {
//        System.out.printf("PN: %b - PS: %b - PE: %b OT: %b\n", player != null, this.paused, instance.passEvents, (Config.getEnabled(InventoryMove.class) && this.player != null) || instance.passEvents);
        return (Config.getEnabled(InventoryMove.class) && this.player != null) || instance.passEvents;
    }

    // For no_pause
    @Redirect(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;paused:Z", opcode = Opcodes.GETFIELD))
    boolean isPaused(MinecraftClient instance) {
        if (!Config.getEnabled(NoPause.class)) return this.paused;
        return this.isIntegratedServerRunning() && (this.currentScreen != null && this.currentScreen.shouldPause() ||
                this.overlay != null && this.overlay.pausesGame()) &&
                !Objects.requireNonNull(this.server)
                        .isRemote();
    }

    // For window_title
    @Inject(method = "getWindowTitle", at = @At("HEAD"), cancellable = true)
    void onGetWindowTitle(CallbackInfoReturnable<String> cir) {
        if (Config.getEnabled(WindowTitle.class)) cir.setReturnValue(WindowTitle.getTitle());
    }
}
