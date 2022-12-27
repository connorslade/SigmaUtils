package com.connorcode.sigmautils.modules._interface;

import com.connorcode.sigmautils.config.settings.NumberSetting;
import com.connorcode.sigmautils.misc.Components;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

public class SplashRefresh extends Module {
    public static NumberSetting refreshTime = new NumberSetting(SplashRefresh.class, "Refresh Time", 0, 10).precision(2)
            .description("The time in seconds between each refresh of the splash screen.")
            .value(4)
            .build();

    public SplashRefresh() {
        super("splash_refresh", "Splash Refresh", "Randomly cycles through splash texts", Category.Interface);
    }

    @Override
    public void drawInterface(MinecraftClient client, Screen screen, int x, int y) {
        Components.sliderConfig(screen, x, y, this, refreshTime);
    }
}
