package com.connorcode.sigmautils.modules.misc;

import com.connorcode.sigmautils.config.settings.BoolSetting;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;

public class NoCooldown extends Module {
    public static BoolSetting noBlockBreak =
            new BoolSetting(NoCooldown.class, "No Block Break").description("Disable block breaking cool down").build();

    public NoCooldown() {
        super("Lets you disable built in cool-downs.", Category.Misc);
    }
}