package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.misc.Raycast;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
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
        if (!Config.getEnabled("block_distance")) return;

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
}
