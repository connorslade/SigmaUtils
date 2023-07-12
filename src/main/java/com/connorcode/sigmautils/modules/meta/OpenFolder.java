package com.connorcode.sigmautils.modules.meta;

import com.connorcode.sigmautils.misc.Components;
import com.connorcode.sigmautils.mixin.ScreenAccessor;
import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.module.ModuleInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import static com.connorcode.sigmautils.SigmaUtils.client;

@ModuleInfo(description = "Opens the folder of this minecraft instance.")
public class OpenFolder extends Module {
    public void drawInterface(MinecraftClient client, Screen screen, int x, int y) {
        ScreenAccessor sa = (ScreenAccessor) screen;
        com.connorcode.sigmautils.misc.util.Util.addChild(screen, new Components.MultiClickButton(x, y, 150, 20,
                Text.of(name), button -> enable(),
                ((button, matrices, mouseX, mouseY) ->
                        matrices.drawOrderedTooltip(sa.getTextRenderer(),
                                sa.getTextRenderer().wrapLines(Text.of(description), 200), mouseX, mouseY)
                )));
    }

    @Override
    public void enable() {
        enabled = false;
        Util.getOperatingSystem().open(client.runDirectory);
    }
}
