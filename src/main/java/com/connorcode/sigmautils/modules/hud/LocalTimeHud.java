package com.connorcode.sigmautils.modules.hud;

import com.connorcode.sigmautils.config.ModuleInfo;
import com.connorcode.sigmautils.misc.TextStyle;
import com.connorcode.sigmautils.module.HudModule;

import java.text.SimpleDateFormat;
import java.util.Date;

@ModuleInfo(description = "Shows your local time")
public class LocalTimeHud extends HudModule {
    public LocalTimeHud() {
        super();
        this.defaultTextColor = TextStyle.Color.Green;
        this.defaultOrder = 9;
    }

    public String line() {
        return String.format("§r%sTime: §f%s", this.getTextColor(),
                (new SimpleDateFormat("HH:mm:ss")).format(new Date()));
    }
}
