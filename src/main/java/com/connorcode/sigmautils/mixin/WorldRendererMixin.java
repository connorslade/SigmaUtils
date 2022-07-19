package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Shadow
    @Nullable
    private ClientWorld world;

    @Inject(method = "processGlobalEvent", at = @At("HEAD"))
    void onProcessGlobalEvent(int eventId, BlockPos pos, int data, CallbackInfo ci) throws Exception {
        if (!Config.getEnabled("no_global_sounds")) return;
        SoundEvent sound = switch (eventId) {
            case 1023 -> SoundEvents.ENTITY_WITHER_SPAWN;
            case 1038 -> SoundEvents.BLOCK_END_PORTAL_SPAWN;
            case 1028 -> SoundEvents.ENTITY_ENDER_DRAGON_DEATH;
            default -> null;
        };

        if (sound == null) return;
        Objects.requireNonNull(world)
                .playSound(pos, sound, SoundCategory.HOSTILE, 1.0F, 1.0F, false);
    }
}
