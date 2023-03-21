package com.connorcode.sigmautils.ui.components;

import com.connorcode.sigmautils.ui.Component;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;

import java.util.Objects;

import static com.connorcode.sigmautils.SigmaUtils.client;

public class Window extends Component {
    Screen parent;

    public Window() {
        this.parent = Objects.requireNonNull(client).currentScreen;
    }

    @Override
    protected void render(Component.RenderState state) {
        RenderSystem.enableBlend();
//        System.out.printf("(%d, %d) (%d, %d)\n", state.x(), state.y(), state.width() + state.x(), state.height() + state.y());
        DrawableHelper.fill(state.matrixStack(), state.x(), state.y(), state.x() + 20, state.y() + 20, 0x88000000);
    }

    @Override
    protected void keyEvent(KeypressState state) {
        if (state.type() == KeyPressType.Down && state.keyCode() == 256) client.setScreen(parent);
    }
}
