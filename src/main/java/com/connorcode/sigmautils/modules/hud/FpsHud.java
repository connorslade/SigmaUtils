package com.connorcode.sigmautils.modules.hud;

import com.connorcode.sigmautils.mixin.MinecraftClientAccessor;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.HudModule;
import net.minecraft.client.MinecraftClient;

public class FpsHud extends HudModule {
    public FpsHud() {
        super("fps_hud", "Fps Hud", "Shows the current FPS in the HUD", Category.Hud);
    }

    public String line() {
        return String.format("§r§fFps: %d", ((MinecraftClientAccessor) MinecraftClient.getInstance()).getCurrentFps());
    }
}
