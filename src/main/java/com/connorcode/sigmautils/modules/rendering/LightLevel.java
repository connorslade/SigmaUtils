package com.connorcode.sigmautils.modules.rendering;

import com.connorcode.sigmautils.config.settings.EnumSetting;
import com.connorcode.sigmautils.config.settings.NumberSetting;
import com.connorcode.sigmautils.event.EventHandler;
import com.connorcode.sigmautils.event.render.WorldRender;
import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.module.ModuleInfo;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.Blocks;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;

import java.util.HashSet;
import java.util.Optional;

import static com.connorcode.sigmautils.SigmaUtils.client;
import static com.connorcode.sigmautils.misc.util.WorldRenderUtils.getMatrices;

@ModuleInfo(description = "Shows the light level the top of blocks")
public class LightLevel extends Module {
    EnumSetting<LightSource> source = new EnumSetting<>(LightLevel.class, "Light Source", LightSource.class).value(LightSource.All).build();
    EnumSetting<Coloring> coloring = new EnumSetting<>(LightLevel.class, "Coloring", Coloring.class).value(Coloring.Hue).build();
    NumberSetting scale = new NumberSetting(LightLevel.class, "Scale", 0.5, 3).value(2).build();
    NumberSetting rangeH = new NumberSetting(LightLevel.class, "Horizontal Range", 5, 25).value(5).precision(0).build();
    NumberSetting rangeV = new NumberSetting(LightLevel.class, "Vertical Range", 0, 10).value(5).precision(0).build();

    BlockPos centerPos;
    HashSet<BlockPos> positions = new HashSet<>();

    @EventHandler
    void onRender(WorldRender.PostWorldRenderEvent event) {
        if (!enabled || client.player == null || client.world == null) return;

        var profiler = client.getProfiler();
        profiler.push("SigmaUtils::LightLevel::Render");
        updatePositions();

        var immediate = client.getBufferBuilders().getEntityVertexConsumers();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        for (var pos : positions) {
            var light = lightLevel(pos);
            var color = getColor(light);
            if (color.isEmpty()) continue;

            var width = client.textRenderer.getWidth(Integer.toString(light));
            var text = Integer.toString(light);
            var matrix = getFloorMatrices(pos.toCenterPos().subtract(0, .5 - 0.01, 0));
            client.textRenderer.draw(text,
                                     width / -2f,
                                     client.textRenderer.fontHeight / -2f,
                                     color.get(),
                                     false,
                                     matrix.peek().getPositionMatrix(),
                                     immediate,
                                     TextRenderer.TextLayerType.NORMAL,
                                     0,
                                     255,
                                     false);
        }

        immediate.draw();
        RenderSystem.disableBlend();
        profiler.pop();
    }

    void updatePositions() {
        assert client.player != null;
        var currentPos = client.player.getBlockPos();
        if (centerPos.equals(currentPos)) return;
        centerPos = currentPos;
        positions.clear();

        for (var x = -rangeH.intValue(); x <= rangeH.intValue(); x++) {
            for (var z = -rangeH.intValue(); z <= rangeH.intValue(); z++) {
                for (var y = -rangeV.intValue(); y <= rangeV.intValue(); y++) {
                    var thisPos = currentPos.add(x, y, z);
                    if (isTransparent(thisPos.down()) || !isTransparent(thisPos)) continue;
                    positions.add(thisPos);
                }
            }
        }
    }

    boolean isTransparent(BlockPos pos) {
        assert client.world != null;
        var state = client.world.getBlockState(pos);
        return state.getBlock() == Blocks.AIR || state.isTransparent(client.world, pos);
    }

    MatrixStack getFloorMatrices(Vec3d pos) {
        var _scale = (float) scale.value();
        var camYaw = client.gameRenderer.getCamera().getYaw() + 45;
        var matrix = getMatrices(pos);
        matrix.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
        matrix.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(Math.round(camYaw / 90f) * 90));
        matrix.scale(-0.025f * _scale, -0.025f * _scale, 1);
        return matrix;
    }

    int lightLevel(BlockPos pos) {
        var world = client.world;
        assert world != null;
        return switch (source.value()) {
            case Block -> world.getLightLevel(LightType.BLOCK, pos);
            case Sky -> world.getLightLevel(LightType.SKY, pos);
            case All -> Math.max(world.getLightLevel(LightType.BLOCK, pos), world.getLightLevel(LightType.SKY, pos) - world.getAmbientDarkness());
        };
    }

    Optional<Integer> getColor(int light) {
        var lightf = MathHelper.clamp(light / 15f, 0, 1);
        return switch (coloring.value()) {
            case Hue -> Optional.of(MathHelper.hsvToRgb(lightf, 1, 1));
            case HostileSpawn -> Optional.ofNullable(light <= 1 ? 0xff0000 : null);
        };
    }

    @Override
    public NbtCompound saveConfig() {
        centerPos = null;
        return super.saveConfig();
    }

    enum Coloring {
        Hue, HostileSpawn
    }

    enum LightSource {
        Block, Sky, All
    }
}
