package com.connorcode.sigmautils.modules._interface;

import com.connorcode.sigmautils.config.ModuleInfo;
import com.connorcode.sigmautils.config.settings.BoolSetting;
import com.connorcode.sigmautils.module.Module;

@ModuleInfo(description = "Lets you see the world while it is being loaded in. You can even exit the loading screen with esc.")
public class SeeThruLoading extends Module {
    public static BoolSetting escToClose = new BoolSetting(SeeThruLoading.class, "Close on ESC").value(true)
            .description("Close the loading screen when you press escape.")
            .build();
}
