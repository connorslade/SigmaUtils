package com.connorcode.sigmautils.modules.meta;

import com.connorcode.sigmautils.misc.Components;
import com.connorcode.sigmautils.mixin.ScreenAccessor;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

public class OpenFolder extends Module {
    public OpenFolder() {
        super("open_folder", "Open Folder", "Opens the folder of this minecraft instance.", Category.Meta);
    }

    public void drawConfigInterface(MinecraftClient client, Screen screen, int x, int y) {
        ScreenAccessor sa = (ScreenAccessor) screen;
        com.connorcode.sigmautils.misc.Util.addDrawable(screen, new Components.MultiClickButton(x, y, 150, 20,
                Text.of(name), button -> enable(MinecraftClient.getInstance()),
                ((button, matrices, mouseX, mouseY) -> screen.renderOrderedTooltip(matrices, sa.getTextRenderer()
                        .wrapLines(Text.of(description), 200), mouseX, mouseY))));
    }

    @Override
    public void enable(MinecraftClient client) {
        enabled = false;
        Util.getOperatingSystem()
                .open(MinecraftClient.getInstance().runDirectory);
    }
}
