package com.connorcode.sigmautils.modules.rendering;

import com.connorcode.sigmautils.config.settings.EnumSetting;
import com.connorcode.sigmautils.config.settings.NumberSetting;
import com.connorcode.sigmautils.event.EventHandler;
import com.connorcode.sigmautils.event.render.WorldRender;
import com.connorcode.sigmautils.misc.TextStyle;
import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.module.ModuleInfo;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.math.Vec3d;

import static com.connorcode.sigmautils.SigmaUtils.client;
import static com.connorcode.sigmautils.misc.util.WorldRenderUtils.getMatrices;

@ModuleInfo(description = "Renders a border around the current map section you are in. Useful for positioning map arts.")
public class MapBorder extends Module {
    EnumSetting<TextStyle.Color> color = new EnumSetting<>(MapBorder.class, "Color", TextStyle.Color.class).description("The color of the border.").value(TextStyle.Color.LightPurple).build();
    NumberSetting step = new NumberSetting(MapBorder.class, "Step", 1, 16).description("The step between each line.").value(16).build();

    @EventHandler
    void onRender(WorldRender.PostWorldRenderEvent event) {
        if (!enabled || client.player == null || client.world == null) return;
        var minY = client.world.getBottomY();
        var maxY = client.world.getTopY();
        var pos = client.player.getPos();
        var x = (int) ((pos.x + 64) / 128);
        var z = (int) ((pos.z + 64) / 128);

        var immediate = client.getBufferBuilders().getEntityVertexConsumers();
        var layer = immediate.getBuffer(RenderLayer.getDebugLineStrip(1f));
        var matrices = getMatrices(Vec3d.ZERO).peek().getPositionMatrix();
        var boxX = x * 128 + 64;
        var boxZ = z * 128 + 64;

        var c = color.value().asHexColor() & 0xffffff;
        var d = c | 0x80 << 24;

        for (var y = minY; y <= maxY; y += step.value()) {
            layer.vertex(matrices, boxX, y, boxZ).color(c).next();
            layer.vertex(matrices, boxX - 128, y, boxZ).color(d).next();
            layer.vertex(matrices, boxX - 128, y, boxZ).color(c).next();
            layer.vertex(matrices, boxX - 128, y, boxZ - 128).color(d).next();
            layer.vertex(matrices, boxX - 128, y, boxZ - 128).color(c).next();
            layer.vertex(matrices, boxX, y, boxZ - 128).color(d).next();
            layer.vertex(matrices, boxX, y, boxZ - 128).color(c).next();
            layer.vertex(matrices, boxX, y, boxZ).color(d).next();
        }

        // Add connecting lines
        layer.vertex(matrices, boxX, minY, boxZ).color(c).next();
        layer.vertex(matrices, boxX, maxY, boxZ).color(d).next();
        layer.vertex(matrices, boxX - 128, minY, boxZ).color(c).next();
        layer.vertex(matrices, boxX - 128, maxY, boxZ).color(d).next();
        layer.vertex(matrices, boxX - 128, minY, boxZ - 128).color(c).next();
        layer.vertex(matrices, boxX - 128, maxY, boxZ - 128).color(d).next();
        layer.vertex(matrices, boxX, minY, boxZ - 128).color(c).next();
        layer.vertex(matrices, boxX, maxY, boxZ - 128).color(d).next();

        immediate.draw();
    }
}
