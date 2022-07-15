package com.connorcode.sigmautils.mixin;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.misc.Raycast;
import com.connorcode.sigmautils.modules.hud.Hud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
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
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
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

    @Inject(method = "render", at = @At("TAIL"))
    void onRender(MatrixStack matrices, float tickDelta, CallbackInfo ci) throws Exception {
        if (client.options.hudHidden || !Config.getEnabled("hud")) return;

        int padding = getPadding();
        Pair<List<String>, Pair<Integer, Integer>> hud = Hud.getHud(scaledHeight, scaledWidth);

        int y = hud.getRight()
                .getRight();
        for (String i : hud.getLeft()) {
            client.textRenderer.drawWithShadow(matrices, i, hud.getRight()
                    .getLeft(), y, 0);
            y += client.textRenderer.fontHeight + padding;
        }
    }

    @Redirect(method = "renderScoreboardSidebar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;getWidth(Ljava/lang/String;)I"))
    int onGetWidth(TextRenderer instance, String text) throws Exception {
        if (Config.getEnabled("no_scoreboard_value")) return 0;
        return instance.getWidth(text);
    }


    @Redirect(method = "renderScoreboardSidebar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/client/util/math/MatrixStack;Ljava/lang/String;FFI)I"))
    int onDraw(TextRenderer instance, MatrixStack matrices, String text, float x, float y, int color) throws Exception {
        if (Config.getEnabled("no_scoreboard_value")) return 0;
        return instance.draw(matrices, text, x, y, color);
    }

    @Redirect(method = "renderScoreboardSidebar", at = @At(value = "INVOKE", target = "Ljava/lang/Integer;toString(I)Ljava/lang/String;"))
    String onToString(int buf) throws Exception {
        if (Config.getEnabled("no_scoreboard_value")) return "";
        return Integer.toString(buf);
    }
}
