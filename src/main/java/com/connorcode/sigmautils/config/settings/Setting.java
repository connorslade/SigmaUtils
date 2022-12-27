package com.connorcode.sigmautils.config.settings;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public interface Setting {
    String getName();

    String getCategory();

    @Nullable
    Text getDescription();

    void initRender(Screen screen, int x, int y);

    void render(RenderData data, int x, int y);

    record RenderData(Screen screen, MatrixStack matrices, int mouseX, int mouseY, float delta) {
    }
}