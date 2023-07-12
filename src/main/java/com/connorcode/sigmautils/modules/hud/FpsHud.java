package com.connorcode.sigmautils.modules.hud;

import com.connorcode.sigmautils.misc.TextStyle;
import com.connorcode.sigmautils.mixin.MinecraftClientAccessor;
import com.connorcode.sigmautils.module.HudModule;
import com.connorcode.sigmautils.module.ModuleInfo;

@ModuleInfo(description = "Shows the current FPS in the HUD")
public class FpsHud extends HudModule {
    public FpsHud() {
        super();
        this.defaultTextColor = TextStyle.Color.LightPurple;
        this.defaultOrder = 1;
    }

    public String line() {
        return String.format("§r%sFps: §f%d", this.getTextColor(),
                MinecraftClientAccessor.getCurrentFps());
    }
}
