package com.connorcode.sigmautils.mixin;

import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;


@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
//    int color;
//    @Shadow
//    @Nullable
//    private ClientWorld world;
//
//    @Shadow
//    @Nullable
//    private VertexBuffer lightSkyBuffer;
//
//    @Inject(method = "processGlobalEvent", at = @At("HEAD"))
//    void onProcessGlobalEvent(int eventId, BlockPos pos, int data, CallbackInfo ci) {
//        if (!Config.getEnabled(NoGlobalSounds.class)) return;
//        SoundEvent sound = switch (eventId) {
//            case 1023 -> ENTITY_WITHER_SPAWN;
//            case 1038 -> BLOCK_END_PORTAL_SPAWN;
//            case 1028 -> ENTITY_ENDER_DRAGON_DEATH;
//            default -> null;
//        };
//
//        if (sound == null || NoGlobalSounds.disabled(eventId))
//            return;
//        Objects.requireNonNull(world).playSoundAtBlockCenter(pos, sound, SoundCategory.HOSTILE, 1.0F, 1.0F, false);
//    }
//
//    @Inject(method = "renderWorldBorder", at = @At("HEAD"), cancellable = true)
//    void onRenderWorldBorder(Camera camera, CallbackInfo ci) {
//        var event = new WorldBorderRender(camera);
//        SigmaUtils.eventBus.post(event);
//        if (event.isCancelled()) ci.cancel();
//    }
//
//    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
//    void onRender(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix, CallbackInfo ci) {
//        var event = new WorldRender.PreWorldRenderEvent(matrices, tickDelta, limitTime, renderBlockOutline, camera, gameRenderer, lightmapTextureManager, positionMatrix);
//        SigmaUtils.eventBus.post(event);
//        if (event.isCancelled()) ci.cancel();
//    }
//
//    @Inject(method = "render", at = @At("RETURN"), cancellable = true)
//    void onPostRender(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix, CallbackInfo ci) {
//        var event = new WorldRender.PostWorldRenderEvent(matrices, tickDelta, limitTime, renderBlockOutline, camera, gameRenderer, lightmapTextureManager, positionMatrix);
//        SigmaUtils.eventBus.post(event);
//        if (event.isCancelled()) ci.cancel();
//    }
//
//    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;hasOutline(Lnet/minecraft/entity/Entity;)Z"))
//    boolean onHasOutline(MinecraftClient instance, Entity entity) {
//        var event =
//                new EntityRender.EntityHighlightEvent(entity, instance.hasOutline(entity), entity.getTeamColorValue());
//        SigmaUtils.eventBus.post(event);
//        this.color = event.getColor();
//        return event.hasOutline();
//    }
//
//    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getTeamColorValue()I"))
//    int onGetTeamColorValue(Entity instance) {
//        return this.color;
//    }
}
