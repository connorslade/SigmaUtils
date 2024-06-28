package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.SigmaUtils;
import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.event.render.EntityRender;
import com.connorcode.sigmautils.event.render.WorldBorderRender;
import com.connorcode.sigmautils.event.render.WorldRender;
import com.connorcode.sigmautils.modules.misc.NoGlobalSounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
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
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Objects;

import static net.minecraft.sound.SoundEvents.*;


@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
    int color;

    @Shadow
    @Nullable
    private ClientWorld world;

    @Shadow
    @Nullable
    private VertexBuffer lightSkyBuffer;

    @Shadow
    private int ticks;

    @Inject(method = "processGlobalEvent", at = @At("HEAD"))
    void onProcessGlobalEvent(int eventId, BlockPos pos, int data, CallbackInfo ci) {
        if (!Config.getEnabled(NoGlobalSounds.class)) return;
        SoundEvent sound = switch (eventId) {
            case 1023 -> ENTITY_WITHER_SPAWN;
            case 1038 -> BLOCK_END_PORTAL_SPAWN;
            case 1028 -> ENTITY_ENDER_DRAGON_DEATH;
            default -> null;
        };

        if (sound == null || NoGlobalSounds.disabled(eventId)) return;
        Objects.requireNonNull(world).playSoundAtBlockCenter(pos, sound, SoundCategory.HOSTILE, 1.0F, 1.0F, false);
    }

    @Inject(method = "renderWorldBorder", at = @At("HEAD"), cancellable = true)
    void onRenderWorldBorder(Camera camera, CallbackInfo ci) {
        var event = new WorldBorderRender(camera);
        SigmaUtils.eventBus.post(event);
        if (event.isCancelled()) ci.cancel();
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    void onRender(RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
        var event = new WorldRender.PreWorldRenderEvent(tickCounter, renderBlockOutline, camera, gameRenderer, lightmapTextureManager, positionMatrix, projectionMatrix);
        SigmaUtils.eventBus.post(event);
        if (event.isCancelled()) ci.cancel();
    }

    @Inject(method = "render", at = @At("RETURN"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    void onPostRender(RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
        var event = new WorldRender.PostWorldRenderEvent(tickCounter, renderBlockOutline, camera, gameRenderer, lightmapTextureManager, positionMatrix, projectionMatrix);
        SigmaUtils.eventBus.post(event);
        if (event.isCancelled()) ci.cancel();
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;hasOutline(Lnet/minecraft/entity/Entity;)Z"))
    boolean onHasOutline(MinecraftClient instance, Entity entity) {
        var event = new EntityRender.EntityHighlightEvent(entity, instance.hasOutline(entity), entity.getTeamColorValue());
        SigmaUtils.eventBus.post(event);
        this.color = event.getColor();
        return event.hasOutline();
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getTeamColorValue()I"))
    int onGetTeamColorValue(Entity instance) {
        return this.color;
    }
}
