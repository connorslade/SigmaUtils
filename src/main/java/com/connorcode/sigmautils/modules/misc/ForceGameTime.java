package com.connorcode.sigmautils.modules.misc;

import com.connorcode.sigmautils.config.settings.NumberSetting;
import com.connorcode.sigmautils.misc.Components;
import com.connorcode.sigmautils.module.BasicModule;
import com.connorcode.sigmautils.module.Category;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

public class ForceGameTime extends BasicModule {
    public static NumberSetting forceTime = new NumberSetting(ForceGameTime.class, "ForceTime", 0, 24000).precision(0)
            .description("Force the in game time to a specific value")
            .build();

    public ForceGameTime() {
        super("force_game_time", "Force Game Time", "Sets how far away the 3rd person camera is", Category.Misc);
    }

    @Override
    public void drawInterface(MinecraftClient client, Screen screen, int x, int y) {
        Components.sliderConfig(screen, x, y, this, forceTime);
    }
}
