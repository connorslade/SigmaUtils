package com.connorcode.sigmautils.modules.hud;

import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.HudModule;

public class WatermarkHud extends HudModule {
    public WatermarkHud() {
        super("watermark_hud", "Watermark", "Adds a \"Sigma Utils\" watermark to the Hud",
                Category.Hud);
    }

    public String line() {
        return "§r§l§aSigma Utils";
    }
}
