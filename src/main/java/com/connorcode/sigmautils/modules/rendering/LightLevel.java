package com.connorcode.sigmautils.modules.rendering;

import com.connorcode.sigmautils.event.render.WorldRender;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.font.TextRenderer;

import static com.connorcode.sigmautils.SigmaUtils.client;

public class LightLevel extends Module {
    void onRender(WorldRender.PostWorldRenderEvent event) {
        if (!enabled || client.player == null) return;

        var pos = client.player.getBlockPos();
        var immediate = client.getBufferBuilders().getEntityVertexConsumers();
        client.textRenderer.draw("TEST", 0f, 0f, 0, true, event.getMatrices().peek().getPositionMatrix(), immediate, TextRenderer.TextLayerType.NORMAL, 0, 0, true);
    }
}
