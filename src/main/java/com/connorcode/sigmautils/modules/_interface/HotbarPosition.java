package com.connorcode.sigmautils.modules._interface;

import com.connorcode.sigmautils.config.ModuleInfo;
import com.connorcode.sigmautils.config.settings.NumberSetting;
import com.connorcode.sigmautils.misc.Components;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

@ModuleInfo(description = "Lets you move the hotbar up")
public class HotbarPosition extends Module {
    public static NumberSetting yPosition = new NumberSetting(HotbarPosition.class, "Y Position", 0, 10).precision(0)
            .description("Number of pixels to move the hotbar up or down.")
            .value(6)
            .build();

    @Override
    public void drawInterface(MinecraftClient client, Screen screen, int x, int y) {
        Components.sliderConfig(screen, x, y, this, yPosition);
    }
}
