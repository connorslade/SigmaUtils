package com.connorcode.sigmautils.modules.misc;

import com.connorcode.sigmautils.config.settings.NumberSetting;
import com.connorcode.sigmautils.misc.Components;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

public class CameraDistance extends Module {
    public static NumberSetting distance =
            new NumberSetting(CameraDistance.class, "Distance", 0, 10).description("Camera Distance in blocks")
                    .build();

    public CameraDistance() {
        super("camera_distance", "CameraDistance", "Sets how far away the 3rd person camera is", Category.Misc);
    }

    public void drawInterface(MinecraftClient client, Screen screen, int x, int y) {
        Components.sliderConfig(screen, x, y, this, distance);
    }
}
