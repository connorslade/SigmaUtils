package com.connorcode.sigmautils.modules.misc;

import com.connorcode.sigmautils.misc.Components;
import com.connorcode.sigmautils.misc.Util;
import com.connorcode.sigmautils.mixin.ScreenAccessor;
import com.connorcode.sigmautils.module.BasicModule;
import com.connorcode.sigmautils.module.Category;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

import static com.connorcode.sigmautils.config.ConfigGui.getPadding;

public class ForceWeather extends BasicModule {
    // 0 => Clear
    // 1 => Rain
    // 2 => Thunder
    public static int weather;
    final char[] weathers = new char[]{
            'C',
            'R',
            'T',
            };

    public ForceWeather() {
        super("force_weather", "Force Weather",
                "Forces the weather of the client rendering. (C)lear, (R)ain, (T)hunder", Category.Misc);
    }

    public void drawConfigInterface(MinecraftClient client, Screen screen, int x, int y) {
        ScreenAccessor sa = (ScreenAccessor) screen;
        int padding = getPadding();

        Components.addToggleButton(screen, this, x, y, 130 - padding, false);
        Util.addDrawable(screen,
                new ButtonWidget(x + 130, y, 20, 20, Text.of(String.valueOf(weathers[weather])), button -> {
                    weather = (weather + 1) % 3;
                    sa.invokeClearAndInit();
                }));
    }

    public void loadConfig(NbtCompound config) {
        super.loadConfig(config);
        weather = config.getInt("weather");
    }

    public NbtCompound saveConfig() {
        NbtCompound nbt = super.saveConfig();
        nbt.putInt("weather", weather);
        return nbt;
    }
}
