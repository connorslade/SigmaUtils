package com.connorcode.sigmautils.ui;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class Screen extends net.minecraft.client.gui.screen.Screen {
    Component component;
    int cmpWidth, cmpHeight;

    public Screen(Component component) {
        super(Text.of("SigmaUtils GUI"));
        this.component = component;
    }

    @Override
    protected void init() {
        super.init();
        cmpWidth = 1920;
        cmpHeight = 1080;
        component.init();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        var x = width / 2 - cmpWidth / 2;
        var y = height / 2 - cmpHeight / 2;
        var state = new Component.RenderState(matrices, x, y, cmpWidth, cmpHeight, mouseX, mouseY, delta);
        component.render(state);
    }

    @Override
    public void tick() {
        component.tick();
    }

    @Override
    public void close() {
        super.close();
        component.close();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        var state = new Component.KeypressState(Component.KeyPressType.Down, keyCode, scanCode, modifiers);
        component.keyEvent(state);
        return true;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        var state = new Component.KeypressState(Component.KeyPressType.Up, keyCode, scanCode, modifiers);
        component.keyEvent(state);
        return true;
    }

//    @Override
//    public void resize(MinecraftClient client, int width, int height) {
//        super.resize(client, width, height);
//        component.resize();
//    }
}
