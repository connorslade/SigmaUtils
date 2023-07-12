package com.connorcode.sigmautils.modules.misc;

import com.connorcode.sigmautils.config.settings.NumberSetting;
import com.connorcode.sigmautils.misc.Components;
import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.module.ModuleInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

@ModuleInfo(description = "Forces the client world's time of day.")
public class ForceGameTime extends Module {
    public static NumberSetting forceTime = new NumberSetting(ForceGameTime.class, "ForceTime", 0, 24000).precision(0)
            .description("Force the in game time to a specific value")
            .build();

    @Override
    public void drawInterface(MinecraftClient client, Screen screen, int x, int y) {
        Components.sliderConfig(screen, x, y, this, forceTime);
    }
}
