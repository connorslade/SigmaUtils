package com.connorcode.sigmautils.modules.misc;

import com.connorcode.sigmautils.module.ModuleInfo;
import com.connorcode.sigmautils.config.settings.BoolSetting;
import com.connorcode.sigmautils.module.Module;

@ModuleInfo(description = "Lets you disable built in cool-downs.")
public class NoCooldown extends Module {
    public static BoolSetting noBlockBreak =
            new BoolSetting(NoCooldown.class, "No Block Break").description("Disable block breaking cool down").build();
}