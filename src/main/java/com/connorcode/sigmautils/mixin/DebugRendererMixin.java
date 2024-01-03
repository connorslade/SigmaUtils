package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.modules.rendering.DebugRenderers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Pair;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DebugRenderer.class)
public class DebugRendererMixin {
    @Shadow
    @Final
    public PathfindingDebugRenderer pathfindingDebugRenderer;
    @Shadow
    @Final
    public DebugRenderer.Renderer waterDebugRenderer;
    @Shadow
    @Final
    public DebugRenderer.Renderer heightmapDebugRenderer;
    @Shadow
    @Final
    public DebugRenderer.Renderer collisionDebugRenderer;
    @Shadow
    @Final
    public DebugRenderer.Renderer supportingBlockDebugRenderer;
    @Shadow
    @Final
    public DebugRenderer.Renderer neighborUpdateDebugRenderer;
    @Shadow
    @Final
    public StructureDebugRenderer structureDebugRenderer;
    @Shadow
    @Final
    public DebugRenderer.Renderer skyLightDebugRenderer;
    @Shadow
    @Final
    public DebugRenderer.Renderer worldGenAttemptDebugRenderer;
    @Shadow
    @Final
    public DebugRenderer.Renderer blockOutlineDebugRenderer;
    @Shadow
    @Final
    public VillageDebugRenderer villageDebugRenderer;
    @Shadow
    @Final
    public VillageSectionsDebugRenderer villageSectionsDebugRenderer;
    @Shadow
    @Final
    public BeeDebugRenderer beeDebugRenderer;
    @Shadow
    @Final
    public RaidCenterDebugRenderer raidCenterDebugRenderer;
    @Shadow
    @Final
    public GoalSelectorDebugRenderer goalSelectorDebugRenderer;
    @Shadow
    @Final
    public GameTestDebugRenderer gameTestDebugRenderer;
    @Shadow
    @Final
    public GameEventDebugRenderer gameEventDebugRenderer;
    @Shadow
    @Final
    public LightDebugRenderer lightDebugRenderer;


    @Inject(method = "render", at = @At("TAIL"))
    void render(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
        if (!Config.getEnabled(DebugRenderers.class)) return;

        var debug = List.of(new Pair<>(pathfindingDebugRenderer, DebugRenderers.pathfinding),
                            new Pair<>(waterDebugRenderer, DebugRenderers.water),
                            new Pair<>(heightmapDebugRenderer, DebugRenderers.heightMap),
                            new Pair<>(collisionDebugRenderer, DebugRenderers.collision),
                            new Pair<>(supportingBlockDebugRenderer, DebugRenderers.supportingBlock),
                            new Pair<>(neighborUpdateDebugRenderer, DebugRenderers.neighborUpdate),
                            new Pair<>(structureDebugRenderer, DebugRenderers.structure),
                            new Pair<>(skyLightDebugRenderer, DebugRenderers.skyLight),
                            new Pair<>(worldGenAttemptDebugRenderer, DebugRenderers.worldGenAttempt),
                            new Pair<>(blockOutlineDebugRenderer, DebugRenderers.blockOutline),
                            new Pair<>(villageDebugRenderer, DebugRenderers.village),
                            new Pair<>(villageSectionsDebugRenderer, DebugRenderers.villageSections),
                            new Pair<>(beeDebugRenderer, DebugRenderers.bee),
                            new Pair<>(raidCenterDebugRenderer, DebugRenderers.raidCenter),
                            new Pair<>(goalSelectorDebugRenderer, DebugRenderers.goalSelector),
                            new Pair<>(gameTestDebugRenderer, DebugRenderers.gameTest),
                            new Pair<>(gameEventDebugRenderer, DebugRenderers.gameEvent),
                            new Pair<>(lightDebugRenderer, DebugRenderers.light));

        for (var renderer : debug) {
            if (renderer.getRight().value()) renderer.getLeft().render(matrices, vertexConsumers, cameraX, cameraY, cameraZ);
        }
    }
}
