package com.connorcode.sigmautils.ui;

import net.minecraft.client.util.math.MatrixStack;

public abstract class Component {
    // == Events ==
    protected void init() {}

    protected void render(RenderState state) {}

    protected void tick() {}

    protected void resize() {}

    protected void close() {}

    protected void keyEvent(KeypressState state) {}

    protected enum KeyPressType {
        Down,
        Up
    }

    protected record RenderState(MatrixStack matrixStack, int x, int y, int width, int height, int mouseX, int mouseY,
                                 float tickDelta) {}

    protected record KeypressState(KeyPressType type, int keyCode, int scanCode, int modifiers) {}
}
