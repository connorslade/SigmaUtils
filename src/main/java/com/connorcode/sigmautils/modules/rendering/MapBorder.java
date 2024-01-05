package com.connorcode.sigmautils.modules.rendering;

import com.connorcode.sigmautils.event.EventHandler;
import com.connorcode.sigmautils.event.render.WorldRender;
import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.module.ModuleInfo;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.WorldRenderer;

import static com.connorcode.sigmautils.SigmaUtils.client;
import static com.connorcode.sigmautils.misc.util.WorldRenderUtils.getMatrices;

@ModuleInfo(description = "Renders a border around the current map section you are in. Useful for positioning map arts.")
public class MapBorder extends Module {
    @EventHandler
    void onRender(WorldRender.PostWorldRenderEvent event) {
        if (!enabled || client.player == null || client.world == null) return;
        var pos = client.player.getPos();
        var x = (int) pos.x / 128;
        var z = (int) pos.z / 128;

        var layer = client.getBufferBuilders().getEntityVertexConsumers().getBuffer(RenderLayer.getLines());
        var matrices = getMatrices(client.player.getPos());
        WorldRenderer.drawBox(matrices, layer, x * 128, client.world.getBottomY(), z * 128, x * 128 + 128, client.world.getTopY(), z * 128 + 128, 1, 1, 1, 1);
//        DebugRenderer.drawBlockBox(event.getMatrices(), layer, client.player.getBlockPos(), 1f, 1f, 1f);
    }
}
