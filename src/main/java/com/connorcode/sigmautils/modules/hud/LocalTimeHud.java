package com.connorcode.sigmautils.modules.hud;

import com.connorcode.sigmautils.misc.TextStyle;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.HudModule;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LocalTimeHud extends HudModule {
    public LocalTimeHud() {
        super("time_hud", "Time Hud", "Shows your local time", Category.Hud);
        this.defaultTextColor = TextStyle.Color.Green;
        this.defaultOrder = 9;
    }

    public String line() {
        return String.format("§r%sTime: §f%s", this.getTextColor(), (new SimpleDateFormat("HH:mm:ss")).format(new Date()));
    }
}
