package com.connorcode.sigmautils.modules.hud;

import com.connorcode.sigmautils.misc.TextStyle;
import com.connorcode.sigmautils.mixin.MinecraftClientAccessor;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.HudModule;
import net.minecraft.client.MinecraftClient;

public class FpsHud extends HudModule {
    public FpsHud() {
        super("fps_hud", "Fps Hud", "Shows the current FPS in the HUD", Category.Hud);
        this.defaultTextColor = TextStyle.Color.LightPurple;
        this.defaultOrder = 1;
    }

    public String line() {
        return String.format("§r%sFps: §f%d", this.getTextColor(),
                MinecraftClientAccessor.getCurrentFps());
    }
}
