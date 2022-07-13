package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.misc.Raycast;
import com.connorcode.sigmautils.modules.CoordinatesHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

import static com.connorcode.sigmautils.config.ConfigGui.getPadding;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private int scaledWidth;

    @Shadow
    private int scaledHeight;

    @Inject(method = "renderCrosshair", at = @At("TAIL"))
    void onRenderCrosshair(MatrixStack matrices, CallbackInfo ci) throws Exception {
        if (Config.getEnabled("block_distance")) blockDistance(matrices);
        if (Config.getEnabled("coordinates_hud")) coordinatesHud(matrices);
    }

    void blockDistance(MatrixStack matrices) {
        Vec3d cameraDirection = Objects.requireNonNull(client.cameraEntity)
                .getRotationVec(client.getTickDelta());
        double fov = client.options.getFov()
                .getValue();
        double angleSize = fov / scaledHeight;
        Vec3f verticalRotationAxis = new Vec3f(cameraDirection);
        verticalRotationAxis.cross(Vec3f.POSITIVE_Y);
        if (!verticalRotationAxis.normalize()) return;

        Vec3f horizontalRotationAxis = new Vec3f(cameraDirection);
        horizontalRotationAxis.cross(verticalRotationAxis);
        horizontalRotationAxis.normalize();

        verticalRotationAxis = new Vec3f(cameraDirection);
        verticalRotationAxis.cross(horizontalRotationAxis);

        Vec3d direction = Raycast.map((float) angleSize, cameraDirection, horizontalRotationAxis, verticalRotationAxis,
                scaledWidth / 2, scaledHeight / 2, scaledWidth, scaledHeight);
        HitResult hitResult = Raycast.raycastInDirection(client, client.getTickDelta(), 500, direction);
        if (Objects.requireNonNull(hitResult)
                .getType() == HitResult.Type.MISS) {
            return;
        }
        double distance = Objects.requireNonNull(hitResult)
                .getPos()
                .distanceTo(Objects.requireNonNull(client.player)
                        .getPos());

        String text = String.format("§f[%d]", (int) distance);
        client.textRenderer.draw(matrices, Text.of(text), scaledWidth / 2f - client.textRenderer.getWidth(text) / 2f,
                scaledHeight / 2f + client.textRenderer.fontHeight, 0);
    }

    void coordinatesHud(MatrixStack matrices) {
        int padding = getPadding();
        ClientPlayerEntity player = Objects.requireNonNull(client.player);
        String text = String.format("§f%d, %d, %d", (int) Math.round(player.getX()), (int) Math.round(player.getY()),
                (int) Math.round(player.getZ()));
        int size = client.textRenderer.getWidth(text);

        Pair<Integer, Integer> pos = switch (CoordinatesHud.location) {
            case 3:
                yield new Pair<>(padding, scaledHeight - client.textRenderer.fontHeight - padding);
            case 2:
                yield new Pair<>(scaledWidth - size - padding, scaledHeight - client.textRenderer.fontHeight - padding);
            case 1:
                yield new Pair<>(scaledWidth - size - padding, padding);
            case 0:
                yield new Pair<>(padding, padding);
            default:
                throw new IllegalStateException("Unexpected value: " + CoordinatesHud.location);
        };

        client.textRenderer.draw(matrices, Text.of(text), pos.getLeft(), pos.getRight(), 0);
    }
}
