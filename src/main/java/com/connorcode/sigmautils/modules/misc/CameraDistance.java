package com.connorcode.sigmautils.modules.misc;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.config.settings.BoolSetting;
import com.connorcode.sigmautils.config.settings.NumberSetting;
import com.connorcode.sigmautils.event.EventHandler;
import com.connorcode.sigmautils.event._interface.MouseScrollEvent;
import com.connorcode.sigmautils.event.misc.Tick;
import com.connorcode.sigmautils.misc.Components;
import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.module.ModuleInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.Perspective;

import static com.connorcode.sigmautils.SigmaUtils.client;

@ModuleInfo(description = "Sets how far away the 3rd person camera is")
public class CameraDistance extends Module {
    public static double distanceMod;
    public static NumberSetting distance =
            new NumberSetting(CameraDistance.class, "Distance", 0, 10).description("Camera Distance in blocks")
                    .build();
    public static BoolSetting scrollZoom = new BoolSetting(CameraDistance.class, "Scroll Zoom").description(
            "Makes the scroll wheal modify the distance when in F1 mode.").build();

    public static float getDistance() {
        return (float) (distance.value() + Math.signum(distanceMod) * Math.pow(Math.abs(distanceMod), 1.2));
    }

    @EventHandler
    void onMouseScroll(MouseScrollEvent event) {
        if (client.currentScreen != null || client.options.getPerspective() == Perspective.FIRST_PERSON ||
                !Config.getEnabled(CameraDistance.class) || !CameraDistance.scrollZoom.value()) return;

        CameraDistance.distanceMod -= event.vertical;
        event.cancel();
    }

    @EventHandler
    public void onTick(Tick.GameTickEvent event) {
        if (enabled && scrollZoom.value() && client.options.getPerspective() == Perspective.FIRST_PERSON)
            distanceMod = 0;
    }

    public void drawInterface(MinecraftClient client, Screen screen, int x, int y) {
        Components.sliderConfig(screen, x, y, this, distance);
    }

    @Override
    public void disable() {
        distanceMod = 0;
    }
}
