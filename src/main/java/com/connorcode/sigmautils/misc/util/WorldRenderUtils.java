package com.connorcode.sigmautils.misc.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import java.util.List;

import static com.connorcode.sigmautils.SigmaUtils.client;

public class WorldRenderUtils {
    public static void renderText(Text text, Vec3d pos, double scale, boolean shadow) {
        var camera = client.gameRenderer.getCamera();
        var matrixStack = getMatrices(pos);

        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        matrixStack.scale(-0.025f * (float) scale, -0.025f * (float) scale, 1);

        var immediate = client.getBufferBuilders().getEntityVertexConsumers();
        client.textRenderer.draw(text, client.textRenderer.getWidth(text) / -2f, 0f, -1, shadow, matrixStack.peek()
                .getPositionMatrix(), immediate, true, 0, 0xf000f0);
        immediate.draw();

        RenderSystem.disableBlend();
    }

    public static MatrixStack getMatrices(Vec3d pos) {
        var matrixStack = new MatrixStack();

        var camera = client.gameRenderer.getCamera();
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0F));
        matrixStack.translate(pos.getX() - camera.getPos().x, pos.getY() - camera.getPos().y,
                pos.getZ() - camera.getPos().z);

        return matrixStack;
    }

    public static void renderLines(List<Text> lines, Vec3d pos, double scale, boolean shadow) {
        for (int i = lines.size() - 1; i > 0; i--) {
            var line = lines.get(i);
            renderText(line, pos.add(0, i * 0.25 * scale, 0), scale, shadow);
        }
    }
}
