package com.connorcode.sigmautils.modules.rendering;

import com.connorcode.sigmautils.config.settings.NumberSetting;
import com.connorcode.sigmautils.misc.Components;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import static com.connorcode.sigmautils.config.ConfigGui.getPadding;

public class Zoom extends Module {
    public static NumberSetting zoom = new NumberSetting(Zoom.class, "Zoom", 0, 10).description("FOV Multiplier")
            .value(5)
            .build();

    public Zoom() {
        super("zoom", "Zoom", "Zoom (Fov multiplier)", Category.Rendering);
    }

    @Override
    public void drawInterface(MinecraftClient client, Screen screen, int x, int y) {
        int padding = getPadding();
        Components.addToggleButton(screen, this, x, y, 20, true);
        zoom.initRender(screen, () -> Text.of(String.format("%s: %.1fx", this.name, zoom.value())), x + 20 + padding, y, 130 - padding, 20);
    }
}
