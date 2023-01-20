package com.connorcode.sigmautils.modules.rendering;

import com.connorcode.sigmautils.config.settings.NumberSetting;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;

public class FullBright extends Module {
    public static NumberSetting gamma =
            new NumberSetting(FullBright.class, "Gamma", 0, 15).value(15).description("The gamma value").build();

    public FullBright() {
        super("full_bright", "Full Bright", "There shall be no darkness", Category.Rendering);
    }
}