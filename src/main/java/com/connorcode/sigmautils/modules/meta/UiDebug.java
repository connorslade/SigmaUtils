package com.connorcode.sigmautils.modules.meta;

import com.connorcode.sigmautils.misc.Components;
import com.connorcode.sigmautils.misc.util.Util;
import com.connorcode.sigmautils.mixin.ScreenAccessor;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.ui.components.Window;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class UiDebug extends Module {
    public UiDebug() {
        super("ui_debug", "Ui Debug", "For debugging the upcoming SigmaUtils GUI overhaul.", Category.Meta);
    }

    public void drawInterface(MinecraftClient client, Screen screen, int x, int y) {
        ScreenAccessor sa = (ScreenAccessor) screen;
        Util.addChild(screen, new Components.MultiClickButton(x, y, 150, 20,
                Text.of(name), button -> enable(client),
                ((button, matrices, mouseX, mouseY) -> screen.renderOrderedTooltip(matrices, sa.getTextRenderer()
                        .wrapLines(Text.of(description), 200), mouseX, mouseY))));
    }

    @Override
    public void enable(MinecraftClient client) {
        enabled = false;
        client.setScreen(new com.connorcode.sigmautils.ui.Screen(new Window()));
    }
}
