package com.connorcode.sigmautils.modules.rendering;

import com.connorcode.sigmautils.config.settings.NumberSetting;
import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.module.ModuleInfo;

@ModuleInfo(description = "There shall be no darkness")
public class FullBright extends Module {
    public static NumberSetting gamma =
            new NumberSetting(FullBright.class, "Gamma", 0, 15).value(15).description("The gamma value").build();
}