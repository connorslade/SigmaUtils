package com.connorcode.sigmautils.config.settings;

import net.minecraft.client.util.math.MatrixStack;

public interface Setting {
    String getName();

    String getCategory();

    void render(RenderData data, int x, int y);

    record RenderData(MatrixStack matrices, int mouseX, int mouseY, float delta) {
    }
}