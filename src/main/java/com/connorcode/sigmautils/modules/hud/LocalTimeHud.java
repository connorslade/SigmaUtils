package com.connorcode.sigmautils.modules.hud;

import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.HudModule;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LocalTimeHud extends HudModule {
    public LocalTimeHud() {
        super("time_hud", "Time Hud", "Shows your local time", Category.Hud);
    }

    public String line() {
        return String.format("§r§cTime: §f%s", (new SimpleDateFormat("HH:mm:ss")).format(new Date()));
    }
}
