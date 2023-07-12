package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.SigmaUtils;
import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.event.render.WorldBorderRender;
import com.connorcode.sigmautils.event.render.WorldRender;
import com.connorcode.sigmautils.modules.misc.NoGlobalSounds;
import com.connorcode.sigmautils.modules.rendering.EntityHighlight;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

import static net.minecraft.sound.SoundEvents.*;


@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Shadow
    @Nullable
    private ClientWorld world;

    @Inject(method = "processGlobalEvent", at = @At("HEAD"))
    void onProcessGlobalEvent(int eventId, BlockPos pos, int data, CallbackInfo ci) {
        if (!Config.getEnabled(NoGlobalSounds.class))
            return;
        SoundEvent sound = switch (eventId) {
            case 1023 -> ENTITY_WITHER_SPAWN;
            case 1038 -> BLOCK_END_PORTAL_SPAWN;
            case 1028 -> ENTITY_ENDER_DRAGON_DEATH;
            default -> null;
        };

        if (sound == null || NoGlobalSounds.disabled(eventId))
            return;
        Objects.requireNonNull(world).playSoundAtBlockCenter(pos, sound, SoundCategory.HOSTILE, 1.0F, 1.0F, false);
    }

    @Inject(method = "renderWorldBorder", at = @At("HEAD"), cancellable = true)
    void onRenderWorldBorder(Camera camera, CallbackInfo ci) {
        var event = new WorldBorderRender(camera);
        SigmaUtils.eventBus.post(event);
        if (event.isCancelled()) ci.cancel();
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    void onRender(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix, CallbackInfo ci) {
        var event = new WorldRender.PreWorldRenderEvent(tickDelta, matrices);
        SigmaUtils.eventBus.post(event);
        if (event.isCancelled()) ci.cancel();
    }

    @Inject(method = "render", at = @At("RETURN"), cancellable = true)
    void onPostRender(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix, CallbackInfo ci) {
        var event = new WorldRender.PostWorldRenderEvent(tickDelta, matrices);
        SigmaUtils.eventBus.post(event);
        if (event.isCancelled()) ci.cancel();
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getTeamColorValue()I"))
    int onGetTeamColorValue(Entity instance) {
        var base = instance.getTeamColorValue();
        if (!Config.getEnabled(EntityHighlight.class))
            return base;
        return EntityHighlight.getGlowingColor(instance).orElse(base);
    }
}
