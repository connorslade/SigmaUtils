package com.connorcode.sigmautils.modules._interface;

import com.connorcode.sigmautils.config.settings.NumberSetting;
import com.connorcode.sigmautils.misc.Components;
import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.module.ModuleInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

@ModuleInfo(description = "Lets you move the chat panel up")
public class ChatPosition extends Module {
    public static NumberSetting yPosition = new NumberSetting(ChatPosition.class, "Y Position", 0, 10).precision(0)
            .description("The number of lines to offset the chat display by")
            .build();

    @Override
    public void drawInterface(MinecraftClient client, Screen screen, int x, int y) {
        Components.sliderConfig(screen, x, y, this, yPosition);
    }
}
