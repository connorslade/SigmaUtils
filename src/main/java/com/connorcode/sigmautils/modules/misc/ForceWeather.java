package com.connorcode.sigmautils.modules.misc;

import com.connorcode.sigmautils.config.settings.EnumSetting;
import com.connorcode.sigmautils.misc.Components;
import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.module.ModuleInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import static com.connorcode.sigmautils.config.ConfigGui.getPadding;

@ModuleInfo(description = "Forces the weather of the client rendering. (C)lear, (R)ain, (T)hunder")
public class ForceWeather extends Module {
    public static EnumSetting<Weather> weather = new EnumSetting<>(ForceWeather.class, "Weather", Weather.class)
            .description("The weather to force on the client side world")
            .value(Weather.CLEAR)
            .build();

    public void drawInterface(MinecraftClient client, Screen screen, int x, int y) {
        Components.addToggleButton(screen, this, x, y, 130 - getPadding(), false);
        Components.enumConfig(screen, x, y, weather, new char[]{
                'C',
                'R',
                'T'
        });
    }

    public enum Weather {
        CLEAR,
        RAIN,
        THUNDER
    }
}
