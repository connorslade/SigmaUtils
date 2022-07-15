package com.connorcode.sigmautils.modules.hud;

import com.connorcode.sigmautils.module.BasicModule;
import com.connorcode.sigmautils.module.Category;

public class WatermarkHud extends BasicModule {
    public WatermarkHud() {
        super("watermark_hud", "Watermark", "Adds a \"Sigma Utils\" watermark to the Hud",
                Category.Hud);
    }
}
