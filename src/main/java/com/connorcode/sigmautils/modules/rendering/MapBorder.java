package com.connorcode.sigmautils.modules.rendering;

import com.connorcode.sigmautils.event.EventHandler;
import com.connorcode.sigmautils.event.render.WorldRender;
import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.module.ModuleInfo;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.Vec3d;

import static com.connorcode.sigmautils.SigmaUtils.client;
import static com.connorcode.sigmautils.misc.util.WorldRenderUtils.getMatrices;

@ModuleInfo(description = "Renders a border around the current map section you are in. Useful for positioning map arts.")
public class MapBorder extends Module {
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
        var matrices = getMatrices(Vec3d.ZERO);
        var boxX = x * 128 + 64;
        var boxZ = z * 128 + 64;
        WorldRenderer.drawBox(matrices, layer, boxX, minY, boxZ, boxX + 128, maxY, boxZ + 128, 1f, .5f, .5f, 1f);
        immediate.drawCurrentLayer();
    }
}
