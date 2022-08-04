package com.connorcode.sigmautils.modules.hud;

import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.HudModule;
import org.apache.commons.lang3.time.DurationFormatUtils;

public class TimePlayedHud extends HudModule {
    public static final long openTimestamp = System.currentTimeMillis();

    public TimePlayedHud() {
        super("time_played_hud", "Time Played Hud", "Shows how long your client has been open for this session",
                Category.Hud);
    }

    public String line() {
        return String.format("§r§9Played: §f%s",
                DurationFormatUtils.formatDuration(System.currentTimeMillis() - openTimestamp, "HH:mm:ss"));
    }
}
